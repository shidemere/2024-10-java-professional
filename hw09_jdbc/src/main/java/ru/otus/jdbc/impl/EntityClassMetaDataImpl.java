package ru.otus.jdbc.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import ru.otus.crm.metadata.Id;
import ru.otus.jdbc.EntityClassMetaData;
import ru.otus.jdbc.exception.AnnotationNotPresentException;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {

    private final List<Field> fields;
    private final Constructor<T> constructor;
    private final Field idField;
    private final String className;
    private final List<Field> fieldsWithoutId;

    public EntityClassMetaDataImpl(Class<T> entityClass) {

        try {
            constructor = entityClass.getConstructor();
            className = entityClass.getSimpleName();
            idField = Arrays.stream(entityClass.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(Id.class))
                    .findFirst()
                    .orElseThrow(() -> new AnnotationNotPresentException("id field not found"));
            fields = Arrays.stream(entityClass.getDeclaredFields()).toList();
            fieldsWithoutId = Arrays.stream(entityClass.getDeclaredFields())
                    .filter(field -> !field.isAnnotationPresent(Id.class))
                    .toList();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return className;
    }

    @Override
    public Constructor<T> getConstructor() {
        return constructor;
    }

    @Override
    public Field getIdField() {
        return idField;
    }

    @Override
    public List<Field> getAllFields() {
        return fields;
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return fieldsWithoutId;
    }
}
