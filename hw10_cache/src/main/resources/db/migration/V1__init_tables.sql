CREATE TABLE IF NOT EXISTS customer
(
    id BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    name VARCHAR(255) NOT NULL
);

-- Создание таблицы item
CREATE TABLE IF NOT EXISTS item
(
    id BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(19, 2),
    customer_id BIGINT,
    CONSTRAINT fk_item_customer
    FOREIGN KEY (customer_id)
    REFERENCES public.customer(id)
    ON DELETE CASCADE
);

-- Создание индекса для ускорения поиска по customer_id в item
CREATE INDEX IF NOT EXISTS idx_item_customer_id ON item(customer_id);