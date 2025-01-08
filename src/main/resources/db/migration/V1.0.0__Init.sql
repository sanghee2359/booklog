create TABLE IF NOT EXISTS ecomm."user" (
    id uuid NOT NULL DEFAULT random_uuid(),
    username varchar(16),
    password varchar(72),
    first_name varchar(16),
    last_name varchar(16),
    email varchar(24),
    phone varchar(24),
    user_status varchar(16) NOT NULL DEFAULT 'ACTIVE' NULL_TO_DEFAULT,
    role varchar(16) NOT NULL DEFAULT 'ROLE_USER' NULL_TO_DEFAULT,
    PRIMARY KEY(id)
    );
