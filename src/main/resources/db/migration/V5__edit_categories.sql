ALTER TABLE categories
ADD COLUMN dashboard_id BIGINT NOT NULL,
ADD CONSTRAINT fk_dashboard
FOREIGN KEY (dashboard_id) REFERENCES dashboards(id);
