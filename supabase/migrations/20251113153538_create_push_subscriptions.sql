CREATE TABLE IF NOT EXISTS push_subscriptions
(
    id          UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    user_id     UUID         NOT NULL,
    endpoint    VARCHAR(500) NOT NULL UNIQUE,
    p256dh      VARCHAR(200) NOT NULL,
    auth        VARCHAR(50)  NOT NULL,
    device_type VARCHAR(50),
    user_agent  VARCHAR(500),
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_push_subscriptions_user_id ON push_subscriptions (user_id);
CREATE INDEX IF NOT EXISTS idx_push_subscriptions_endpoint ON push_subscriptions (endpoint);

COMMENT ON TABLE push_subscriptions IS 'Stores push notification subscriptions for users';
COMMENT ON COLUMN push_subscriptions.endpoint IS 'Unique push service endpoint URL';
COMMENT ON COLUMN push_subscriptions.p256dh IS 'Public key for encryption';
COMMENT ON COLUMN push_subscriptions.auth IS 'Auth secret for encryption';

