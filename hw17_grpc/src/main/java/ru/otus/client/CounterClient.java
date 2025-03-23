package ru.otus.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.extern.slf4j.Slf4j;
import ru.otus.CounterRequest;
import ru.otus.RemoteCounterServiceGrpc;

/**
 * Напоминаю что у gRPC есть 4 вида общения:
 * 1. Унарный (одно сообщение)
 * 2. Client-side (клиент закидывает сообщения на сервер)
 * 3. Server-side (сервер закидывает сообщения на клиента)
 * 4. Stream (данные летят туда-сюда)
 * Каждый вид делится на блокирующий/не блокирующий.
 * В неблокирующем ответ не получается напрямую, и еще нужно использовать синхронизацию при помощи {@link CountDownLatch}
 */
@Slf4j
public class CounterClient {
    private static final String HOST = "localhost";
    private static final int PORT = 8093;
    private static final int REQUEST_START_VALUE = 0;
    private static final int REQUEST_END_VALUE = 30;
    private static final int LOOP_START_VALUE = 0;
    private static final int LOOP_END_VALUE = 50;

    public static void main(String[] args) throws InterruptedException {
        log.info("Starting counter client");
        ManagedChannel channel =
                ManagedChannelBuilder.forAddress(HOST, PORT).usePlaintext().build();

        // В зависимости от того, блокирующий у нас или не блокирующий стаб - мы можем получить ответ.
        var asyncClient = RemoteCounterServiceGrpc.newStub(channel);

        CounterRequest counterRequest = CounterRequest.newBuilder()
                .setFirstValue(REQUEST_START_VALUE)
                .setLastValue(REQUEST_END_VALUE)
                .build();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        BlockingQueue<Long> responseQueue = new LinkedBlockingQueue<>();
        /*
           Данные будут обрабатываться в отдельном потоке, так делает Netty.
           Поэтому достать их наружу не получится
        */
        asyncClient.countFromTo(counterRequest, new CounterClientObserver(countDownLatch, responseQueue));

        /*
           На стороне сервера +1, поэтому ответ в виде нуля у нас никогда не произойдет.
        */
        Thread.ofVirtual().name("CounterThread").start(() -> {
            // Выводимое на экран число
            long currentValue = 0;
            // Последнее полученное от сервера число
            long lastServerValue = 0;
            for (int i = LOOP_START_VALUE; i < LOOP_END_VALUE; i++) {
                long buffer;
                try {
                    /*
                       todo Не совсем понимаю строку из ТЗ "сервер может вернуть несколько чисел, надо взять именно ПОСЛЕДНЕЕ"
                       Как это может произойти, если сервер возвращает ответ только раз в две секунды, а наш цикл отрабатывает
                       в два раза быстрее? Понятное дело что тут роль играют издержки работы по сети, но как будто бы очень маловероятно такое
                    */
                    // полученное от сервера числа неблокирующим вариантом.
                    buffer = Optional.of(responseQueue.take()).orElse(0L);
                    /*
                       В случае если полученное сейчас значение из сервера совпадает с тем, что было до этого ->
                           нам нужно просто инкрементировать текущее значение.

                       В обратном случае необходимо запускать логику увеличения значения И заменить последнее значение
                           из сервера только что полученным.
                    */
                    if (buffer == lastServerValue) {
                        ++currentValue;
                    } else {
                        currentValue += buffer + 1;
                        lastServerValue = buffer;
                    }
                    log.info("currentValue: {}", currentValue);
                    // todo Возможно есть какой то другой способ, кроме как явно вырубать поток таким образом?
                    Thread.sleep(Duration.ofSeconds(1L));
                } catch (InterruptedException e) {
                    log.error("Что то пошло не так {}", e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });

        countDownLatch.await();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Counter client stopped");
            channel.shutdownNow();
        }));
    }
}
