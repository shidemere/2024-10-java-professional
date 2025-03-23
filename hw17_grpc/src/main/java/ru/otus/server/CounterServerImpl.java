package ru.otus.server;

import io.grpc.stub.StreamObserver;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import ru.otus.CounterRequest;
import ru.otus.CounterResponse;
import ru.otus.RemoteCounterServiceGrpc;

@Slf4j
public class CounterServerImpl extends RemoteCounterServiceGrpc.RemoteCounterServiceImplBase {
    /**
     * пул потоков для запуска отложенных и периодических задач.
     * Если задачи короткие - пул может быть меньше чем количество задач.
     * Если долгие не надо создавать пул размером больше, чем количество задач.
     */
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    @Override
    public void countFromTo(CounterRequest request, StreamObserver<CounterResponse> responseObserver) {
        log.atInfo().log("Get request for counting from {} to {}", request.getFirstValue(), request.getLastValue());
        // todo почему нельзя использовать обычный int внутри потока?
        var counter = new AtomicLong(request.getFirstValue());

        Runnable task = () -> {
            var response = CounterResponse.newBuilder()
                    .setResponseValue(counter.incrementAndGet())
                    .build();
            // Возвращаем ответ таким образом
            responseObserver.onNext(response);
            if (counter.get() == request.getLastValue()) {
                executor.shutdown();
                // Завершаем соединение, чтобы не ждало, зависнув
                responseObserver.onCompleted();
            }
        };
        /*
         * Запуск указанной задачи.
         * С задержкой в 0 секунд
         * И интервалом 2 секунды
         */
        executor.scheduleAtFixedRate(task, 0, 2, TimeUnit.SECONDS);
    }
}
