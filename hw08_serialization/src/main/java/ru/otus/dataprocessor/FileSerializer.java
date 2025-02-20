package ru.otus.dataprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class FileSerializer implements Serializer {
    private static final Logger log = LoggerFactory.getLogger(FileSerializer.class);

    private final String fullOutputFilePath;

    public FileSerializer(String fileName) {
        this.fullOutputFilePath = fileName;
    }

    @Override
    public void serialize(Map<String, Double> data) {
        try {
            new ObjectMapper().writeValue(new File(fullOutputFilePath), data);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new FileProcessException(e);
        }
    }
}
