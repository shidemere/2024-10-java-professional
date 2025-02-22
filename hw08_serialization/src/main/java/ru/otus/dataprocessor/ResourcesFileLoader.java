package ru.otus.dataprocessor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.model.Measurement;

public class ResourcesFileLoader implements Loader {
    private static final Logger log = LoggerFactory.getLogger(ResourcesFileLoader.class);
    private final String fileName;

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
    }

    // Читает файл и возвращает результат коллекцию
    @Override
    public List<Measurement> load() {

        try (InputStream resource = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(resource, new TypeReference<>() {});
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new FileProcessException(e);
        }
    }
}
