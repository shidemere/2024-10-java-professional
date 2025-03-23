package ru.otus.client;

import io.grpc.stub.StreamObserver;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;
import ru.otus.CounterResponse;

@Slf4j
public class CounterClientObserver implements StreamObserver<CounterResponse> {
    private final CountDownLatch countDownLatch;
    private final BlockingQueue<Long> responseQueue;

    /**
     * Из-за использования неблокирующего stub приходится использовать синхронизатор.
     * Но я не совсем уверен как его лучше использовать.
     */
    public CounterClientObserver(CountDownLatch countDownLatch, BlockingQueue<Long> responseQueue) {
        this.countDownLatch = countDownLatch;
        this.responseQueue = responseQueue;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onNext(CounterResponse counterResponse) {
        log.atInfo().log("newValue {}", counterResponse.getResponseValue());
        responseQueue.offer(counterResponse.getResponseValue());
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("Возникло исключение: {}", throwable.getMessage());
    }

    @Override
    public void onCompleted() {
        log.atInfo().log("Completed work");
        countDownLatch.countDown();
    }
}
