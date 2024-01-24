CREATE TABLE nonprofits (
    id serial PRIMARY KEY,
    legal_name VARCHAR(255),
    EIN VARCHAR(255),
    mission TEXT,

    address_street VARCHAR(255),
    address_city VARCHAR(255),
    address_state VARCHAR(255),
    address_zip VARCHAR(255),

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TYPE GRANT_TYPE AS ENUM ('OPERATING_GRANT', 'PROJECT_GRANT', 'OTHER');

CREATE TABLE grant_submissions (
    id serial PRIMARY KEY,
    nonprofit_id INT NOT NULL,
    grant_name VARCHAR(255),
    requested_amount BIGINT,
    awarded_amount BIGINT,
    grant_type GRANT_TYPE,
    tags TEXT,
    duration_grant_start DATE,
    duration_grant_end DATE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    FOREIGN KEY (nonprofit_id) REFERENCES nonprofits (id)
);