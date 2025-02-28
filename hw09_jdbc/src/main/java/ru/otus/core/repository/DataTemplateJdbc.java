package ru.otus.core.repository;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.core.repository.executor.DbExecutor;
import ru.otus.jdbc.EntityClassMetaData;
import ru.otus.jdbc.EntitySQLMetaData;

/** Сохратяет объект в базу, читает объект из базы */
@SuppressWarnings("java:S1068")
public class DataTemplateJdbc<T> implements DataTemplate<T> {
    private static final Logger log = LoggerFactory.getLogger(DataTemplateJdbc.class);
    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData<T> entityClassMetaData;

    public DataTemplateJdbc(
            DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData, EntityClassMetaData<T> entityClassMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), rs -> {
            try {
                if (rs.next()) {
                    return createAndFillInstance(rs);
                }
                return null;
            } catch (SQLException e) {
                log.error("Ошибка={} получения пользователя по id={}", e.getMessage(), id);
                throw new DataTemplateException(e);
            }
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        return dbExecutor
                .executeSelect(connection, entitySQLMetaData.getSelectAllSql(), Collections.emptyList(), rs -> {
                    ArrayList<T> result = new ArrayList<>();
                    try {
                        while (rs.next()) {
                            result.add(createAndFillInstance(rs));
                        }
                        return result;
                    } catch (SQLException e) {
                        log.error("Ошибка получения всех пользователей: {}", e.getMessage());
                        throw new DataTemplateException(e);
                    }
                })
                .orElseThrow(() -> new RuntimeException("Unexpected error"));
    }

    @Override
    public long insert(Connection connection, T instance) {
        try {
            return dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(), prepareParams(instance));
        } catch (Exception e) {
            log.error("Ошибка добавления пользователя={}", e.getMessage());
            throw new DataTemplateException(e);
        }
    }

    @Override
    public void update(Connection connection, T instance) {
        try {
            dbExecutor.executeStatement(connection, entitySQLMetaData.getUpdateSql(), prepareParamsForUpdate(instance));
        } catch (Exception e) {
            log.error("Ошибка обновления пользователя={}", e.getMessage());
            throw new DataTemplateException(e);
        }
    }

    /**
     * Маппинг из строки ResultSet в объект.
     * @param rs результирующий набор, пришедший из БД
     * @return объект, заполненный значениями
     */
    private T createAndFillInstance(ResultSet rs) {
        try {
            Constructor<T> constructor = entityClassMetaData.getConstructor();
            constructor.setAccessible(true);
            T instance = constructor.newInstance();
            for (Field field : entityClassMetaData.getAllFields()) {
                field.setAccessible(true);
                field.set(instance, rs.getObject(field.getName()));
            }
            return instance;
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    /**
     * Получение значения полей инстанса.
     * Используется для подготовки к вставке и обновлению.
     * @param instance объекта
     * @return список значений полей у объекта КРОМЕ идентификатора
     */
    private List<Object> prepareParams(T instance) {
        List<Field> fields = entityClassMetaData.getFieldsWithoutId();
        return fields.stream()
                .map(f -> {
                    try {
                        f.setAccessible(true);
                        return f.get(instance);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }

    /**
     * Получение значений полей инстанса.
     * Используется для обновления
     * @param instance объекта
     * @return список значений полей у объекта ВМЕСТЕ С идентификатором.
     */
    private List<Object> prepareParamsForUpdate(T instance) {
        List<Object> params = new ArrayList<>(prepareParams(instance));
        Field idField = entityClassMetaData.getIdField();
        try {
            idField.setAccessible(true);
            params.add(idField.get(instance));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return params;
    }
}
