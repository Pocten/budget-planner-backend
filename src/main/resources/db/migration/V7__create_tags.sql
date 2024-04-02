CREATE TABLE tags (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    dashboard_id BIGINT NOT NULL,
    FOREIGN KEY (dashboard_id) REFERENCES dashboards (id)
);

CREATE TABLE financial_record_tag (
    financial_record_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    CONSTRAINT pk_financial_record_tag PRIMARY KEY (financial_record_id, tag_id),
    CONSTRAINT fk_financial_record FOREIGN KEY (financial_record_id) REFERENCES financial_records (id),
    CONSTRAINT fk_tag FOREIGN KEY (tag_id) REFERENCES tags (id)
);
