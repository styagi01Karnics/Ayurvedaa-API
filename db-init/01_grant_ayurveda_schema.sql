-- Run this script as PostgreSQL superuser (e.g. postgres) on ayurveda_db
-- Grants the application user permission to create tables in the ayurveda schema.

GRANT USAGE, CREATE ON SCHEMA ayurveda TO ayurveda;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA ayurveda TO ayurveda;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA ayurveda TO ayurveda;
ALTER DEFAULT PRIVILEGES IN SCHEMA ayurveda GRANT ALL ON TABLES TO ayurveda;
ALTER DEFAULT PRIVILEGES IN SCHEMA ayurveda GRANT ALL ON SEQUENCES TO ayurveda;
