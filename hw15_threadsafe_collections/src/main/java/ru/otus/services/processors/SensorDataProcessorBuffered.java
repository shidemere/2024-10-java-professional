package ru.otus.services.processors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.api.SensorDataProcessor;
import ru.otus.api.model.SensorData;
import ru.otus.lib.SensorDataBufferedWriter;

@SuppressWarnings({"java:S1068", "java:S125"})
public class SensorDataProcessorBuffered implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);

    private final int bufferSize;
    private final SensorDataBufferedWriter writer;
    private final BlockingQueue<SensorData> buffer;

    public SensorDataProcessorBuffered(int bufferSize, SensorDataBufferedWriter writer) {
        if (bufferSize < 1) {
            throw new IllegalArgumentException("Buffer size can't be less than 1");
        }

        this.bufferSize = bufferSize;
        this.writer = writer;
        var sensorDataComparator = Comparator.comparing(SensorData::getMeasurementTime);
        this.buffer = new PriorityBlockingQueue<>(bufferSize, sensorDataComparator);
    }

    @Override
    public void process(SensorData data) {
        if (data == null) {
            log.debug("Incorrect input: input is null");
            return;
        }
        if (buffer.size() >= bufferSize) {
            flush();
        }
        boolean isOffer = buffer.offer(data);
        if (!isOffer) {
            log.debug("Can't insert data into queue: {}", data);
        }
    }

    public void flush() {
        List<SensorData> bufferedData = new ArrayList<>(bufferSize);
        try {
            buffer.drainTo(bufferedData);
            if (bufferedData.isEmpty()) {
                log.debug("Nothing to flush");
                return;
            }
            log.debug("Flushed buffer: {}", bufferedData);
            writer.writeBufferedData(bufferedData);
        } catch (Exception e) {
            log.error("Ошибка в процессе записи буфера", e);
        }

    }

    @Override
    public void onProcessingEnd() {
        flush();
    }
}
