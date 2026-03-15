CREATE TABLE roles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id),
    CONSTRAINT uk_roles_name UNIQUE (name),

    INDEX idx_roles_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE capabilities (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NULL,
    CONSTRAINT pk_capabilities PRIMARY KEY (id),
    CONSTRAINT uk_capabilities_name UNIQUE (name),

    INDEX idx_capabilities_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE roles_capabilities (
    role_id BIGINT NOT NULL,
    capability_id BIGINT NOT NULL,
    CONSTRAINT pk_roles_capabilities PRIMARY KEY (role_id, capability_id),

    CONSTRAINT fk_roles_capabilities_role
        FOREIGN KEY (role_id) REFERENCES roles(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_roles_capabilities_capability
        FOREIGN KEY (capability_id) REFERENCES capabilities(id)
        ON DELETE CASCADE,

    INDEX idx_roles_capabilities_capability_id (capability_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,

    uuid BINARY(16) NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role_id BIGINT NOT NULL,

    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    deleted_at DATETIME NULL,

    PRIMARY KEY (id),
    CONSTRAINT uk_users_uuid UNIQUE (uuid),
    CONSTRAINT uk_users_username UNIQUE (username),

    CONSTRAINT fk_users_role
        FOREIGN KEY (role_id) REFERENCES roles(id)
        ON DELETE RESTRICT,

    INDEX ix_users_role_id (role_id),
    INDEX ix_users_deleted (deleted),
    INDEX ix_users_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- =========================
-- Domain tables
-- =========================
CREATE TABLE sports (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,

    CONSTRAINT pk_sports PRIMARY KEY (id),
    CONSTRAINT uk_sports_name UNIQUE (name),

    INDEX idx_sports_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE attachments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    filename VARCHAR(255) NOT NULL,

    saved_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(1024) NOT NULL,

    content_type VARCHAR(255) NOT NULL,
    extension VARCHAR(50) NOT NULL,

    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    deleted_at DATETIME NULL,

    CONSTRAINT pk_attachments PRIMARY KEY (id),
    CONSTRAINT uk_attachments_saved_name UNIQUE (saved_name),

    INDEX idx_attachments_deleted (deleted),
    INDEX idx_attachments_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE personal_information (
    id BIGINT NOT NULL AUTO_INCREMENT,
    membership_id VARCHAR(255) NOT NULL,
    identity_number VARCHAR(255) NOT NULL,
    place_of_birth VARCHAR(255) NOT NULL,
    branch_of_registration VARCHAR(255) NOT NULL,
    membership_file_id BIGINT,

    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    deleted_at DATETIME NULL,

    CONSTRAINT pk_personal_information PRIMARY KEY (id),
    CONSTRAINT uk_personal_information_membership_id UNIQUE (membership_id),
    CONSTRAINT uk_personal_information_identity UNIQUE (identity_number),
    CONSTRAINT uk_personal_information_membership_file_id UNIQUE (membership_file_id),

    CONSTRAINT fk_personal_information_attachments FOREIGN KEY (membership_file_id)
        REFERENCES attachments(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,

    INDEX idx_personal_information_deleted (deleted),
    INDEX idx_personal_information_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE members (
    id BIGINT NOT NULL AUTO_INCREMENT,
    uuid BINARY(16) NOT NULL,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    vat VARCHAR(255) NOT NULL,
    sport_id BIGINT,
    user_id BIGINT NOT NULL,
    personal_info_id BIGINT NULL,

    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    deleted_at DATETIME NULL,

    CONSTRAINT pk_members PRIMARY KEY (id),
    CONSTRAINT uk_members_uuid UNIQUE (uuid),
    CONSTRAINT uk_members_vat UNIQUE (vat),

    -- enforce 1-1
    CONSTRAINT uk_members_user_id UNIQUE (user_id),
    CONSTRAINT uk_members_personal_info_id UNIQUE (personal_info_id),

    CONSTRAINT fk_members_sports FOREIGN KEY (sport_id)
        REFERENCES sports(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,

     -- adjust users table name/PK if needed
    CONSTRAINT fk_teachers_users FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT fk_teachers_personal_information FOREIGN KEY (personal_info_id)
        REFERENCES personal_information(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    INDEX idx_teachers_sport_id (sport_id),
    INDEX idx_teachers_lastname (lastname),
    INDEX idx_teachers_deleted (deleted),
    INDEX idx_teachers_deleted_at (deleted_at),
    INDEX idx_teachers_user_id (user_id),
    INDEX idx_teachers_personal_info_id (personal_info_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;