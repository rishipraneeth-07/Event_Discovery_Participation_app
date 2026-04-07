CREATE TABLE IF NOT EXISTS saved_events (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    CONSTRAINT pk_saved_events PRIMARY KEY (id),
    CONSTRAINT uk_saved_events_user_event UNIQUE (user_id, event_id),
    CONSTRAINT fk_saved_events_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_saved_events_event FOREIGN KEY (event_id) REFERENCES events (id),
    INDEX idx_saved_events_user_id (user_id),
    INDEX idx_saved_events_event_id (event_id),
    INDEX idx_saved_events_created_at (created_at)
);

CREATE TABLE IF NOT EXISTS user_interests (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    interest VARCHAR(100) NOT NULL,
    created_at DATETIME NOT NULL,
    CONSTRAINT pk_user_interests PRIMARY KEY (id),
    CONSTRAINT uk_user_interests_user_interest UNIQUE (user_id, interest),
    CONSTRAINT fk_user_interests_user FOREIGN KEY (user_id) REFERENCES users (id),
    INDEX idx_user_interests_user_id (user_id),
    INDEX idx_user_interests_interest (interest),
    INDEX idx_user_interests_created_at (created_at)
);

CREATE TABLE IF NOT EXISTS recently_viewed_events (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    viewed_at DATETIME NOT NULL,
    CONSTRAINT pk_recently_viewed_events PRIMARY KEY (id),
    CONSTRAINT uk_recently_viewed_user_event UNIQUE (user_id, event_id),
    CONSTRAINT fk_recently_viewed_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_recently_viewed_event FOREIGN KEY (event_id) REFERENCES events (id),
    INDEX idx_recently_viewed_user_id (user_id),
    INDEX idx_recently_viewed_event_id (event_id),
    INDEX idx_recently_viewed_viewed_at (viewed_at)
);
