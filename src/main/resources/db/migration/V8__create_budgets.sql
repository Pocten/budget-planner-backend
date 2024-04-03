CREATE TABLE budgets (
    id SERIAL PRIMARY KEY,
    dashboard_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    total_amount NUMERIC(19,2) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    FOREIGN KEY (dashboard_id) REFERENCES dashboards(id)
);
