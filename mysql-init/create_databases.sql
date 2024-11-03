CREATE SCHEMA IF NOT EXISTS ggumi;
CREATE SCHEMA IF NOT EXISTS batch_meta;

-- Switch to ggumi schema to create ShedLock table
USE ggumi;

CREATE TABLE IF NOT EXISTS shedlock (
    name VARCHAR(64) NOT NULL,
    lock_until TIMESTAMP(3) NULL,
    locked_at TIMESTAMP(3) NULL,
    locked_by VARCHAR(255) NULL,
    PRIMARY KEY (name)
);