-- Removing old uniqueness constraint for 'name'
alter table categories drop constraint unique_name;

-- Adding a new compound unique constraint for 'name' and 'dashboard_id'
alter table categories add CONSTRAINT categories_name_dashboard_id_unique UNIQUE (name, dashboard_id);

-- Add unique constraint for 'name' and 'dashboard_id' in tags table
alter table tags add CONSTRAINT tags_name_dashboard_id_unique UNIQUE (name, dashboard_id);

-- Add unique constraint for 'title' and 'dashboard_id' in budgets table
alter table budgets add CONSTRAINT budgets_title_dashboard_id_unique UNIQUE (title, dashboard_id);
