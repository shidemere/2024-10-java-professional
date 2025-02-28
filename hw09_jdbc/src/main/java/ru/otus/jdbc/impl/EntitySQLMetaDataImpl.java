package ru.otus.jdbc.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.jdbc.EntityClassMetaData;
import ru.otus.jdbc.EntitySQLMetaData;

public class EntitySQLMetaDataImpl implements EntitySQLMetaData {

    private static final Logger log = LoggerFactory.getLogger(EntitySQLMetaDataImpl.class);

    private final EntityClassMetaData<?> entityClassMetaData;

    public EntitySQLMetaDataImpl(EntityClassMetaData<?> entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public String getSelectAllSql() {
        StringBuilder sql = new StringBuilder("SELECT ");
        entityClassMetaData.getAllFields().forEach(field -> sql.append(field.getName())
                .append(", "));

        sql.deleteCharAt(sql.length() - 1);
        sql.append(" FROM ");
        entityClassMetaData.getName();
        log.info("Для выборки всех записей был сгенерирован запрос {}", sql);
        return sql.toString();
    }

    @Override
    public String getSelectByIdSql() {
        StringBuilder sql = new StringBuilder("SELECT ");
        entityClassMetaData.getAllFields().forEach(field -> sql.append(field.getName())
                .append(", "));
        sql.deleteCharAt(sql.length() - 2);
        sql.append(" FROM ");
        sql.append(entityClassMetaData.getName());
        sql.append(" WHERE ");
        sql.append(entityClassMetaData.getIdField().getName()).append(" = ?");
        log.info("Для получения записи по ID был сгенерирован запрос {}", sql);
        return sql.toString();
    }

    @Override
    public String getInsertSql() {
        StringBuilder sql = new StringBuilder("INSERT INTO ");

        sql.append(entityClassMetaData.getName().toLowerCase());
        sql.append(" (");
        entityClassMetaData.getFieldsWithoutId().forEach(field -> sql.append(field.getName())
                .append(", "));
        sql.deleteCharAt(sql.length() - 2);
        sql.append(") VALUES (");
        entityClassMetaData.getFieldsWithoutId().forEach(field -> sql.append("?")
                .append(", "));
        sql.deleteCharAt(sql.length() - 2);
        sql.append(")");
        log.warn("Для вставки записи был сгенерирован запрос {}", sql);
        return sql.toString();
    }

    @Override
    public String getUpdateSql() {
        StringBuilder sql = new StringBuilder("UPDATE ");
        sql.append(entityClassMetaData.getIdField().getName());
        sql.append(" SET ");
        entityClassMetaData.getAllFields().forEach(field -> sql.append(field.getName())
                .append(" = ?"));
        sql.deleteCharAt(sql.length() - 1);
        sql.append(" WHERE ");
        sql.append(entityClassMetaData.getIdField().getName()).append(" = ?");
        log.info("Для обновления записи по ID был сгенерирован запрос {}", sql);
        return sql.toString();
    }
}
