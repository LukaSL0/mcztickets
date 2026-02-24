SELECT 'CREATE DATABASE auth_db' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'auth_db')\gexec
SELECT 'CREATE DATABASE events_db' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'events_db')\gexec
SELECT 'CREATE DATABASE orders_db' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'orders_db')\gexec
SELECT 'CREATE DATABASE payments_db' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'payments_db')\gexec