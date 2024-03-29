CREATE TABLE financial_records (
    id BIGSERIAL PRIMARY KEY,
    dashboard_id BIGINT NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    category_id BIGINT,
    date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    description VARCHAR(500),
    FOREIGN KEY (dashboard_id) REFERENCES dashboards(id)
);

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    CONSTRAINT unique_name UNIQUE (name)
);

