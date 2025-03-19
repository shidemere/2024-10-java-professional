package ru.otus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static volatile long currentThreadId;
    private static volatile boolean isForward = true;
    private static volatile boolean isRunning = true;

    public synchronized void countAndPrint(int begin, int end) {
        int current = begin;
        while (isRunning) {
            try {
                /*
                   В первой итерации застрахуемся от срабатывания второго потока первым при помомщи явного присваивания
                   в методе main. Здесь поток 2 будет заморожен и дальше пойдет поток 1.
                */
                while (currentThreadId == Thread.currentThread().threadId()) {
                    wait();
                }

                logger.info(Thread.currentThread().getName() + " " + current);

                /*
                       Заканчивать нам надо только если мы достигли нижней границы при обратном направлении.
                       Это здесь и проверяется.
                */
                if (current == begin && !isForward) {
                    isRunning = false; // Останавливаем все потоки
                }

                // Меняем направление при достижении верхней границы
                if (current == end) {
                    isForward = false;
                }

                // Обновляем текущее значение (если не достигли 1)
                if (isRunning) {
                    current = isForward ? current + 1 : current - 1;
                }

                // Передаём ход другому потоку
                currentThreadId = Thread.currentThread().threadId();
                notifyAll();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        Main main = new Main();

        var first = new Thread(() -> main.countAndPrint(1, 10));
        first.setName("Поток #1");
        first.start();

        var second = new Thread(() -> main.countAndPrint(1, 10));
        second.setName("Поток #2");
        currentThreadId = second.threadId();
        second.start();
    }
}
