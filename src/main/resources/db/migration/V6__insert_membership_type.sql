CREATE TABLE membership_type (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,

    CONSTRAINT pk_membership_type PRIMARY KEY(id),
    CONSTRAINT uk_membership_type_name UNIQUE(name),

    INDEX idx_membership_type_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO membership_type(id, name) VALUES
(1, 'BASIC'),
(2, 'EXTRA'),
(3, 'PREMIUM');

ALTER TABLE members
ADD COLUMN membership_type_id BIGINT NULL;

ALTER TABLE members
ADD CONSTRAINT fk_members_membership_type
    FOREIGN KEY (membership_type_id) REFERENCES membership_type(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE;

CREATE INDEX idx_members_membership_type_id ON members (membership_type_id);
