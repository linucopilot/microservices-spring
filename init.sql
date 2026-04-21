-- Create users table for auth-service
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255),
    enabled BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index on username for faster lookups
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Insert sample data for testing
-- Password: admin (bcrypt encoded)
INSERT INTO users (username, password, email, full_name, enabled) 
VALUES (
    'admin',
    '$2a$10$slYQmyNdGzin7olVN3z5Be8DlH.PKZbv5H8KnzzVgXXbVxzy.Sm2m',
    'admin@prasac.com',
    'Admin User',
    true
) ON CONFLICT (username) DO NOTHING;

-- Create users_detail table (optional - for user-service)
CREATE TABLE IF NOT EXISTS users_detail (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255),
    phone VARCHAR(20),
    address TEXT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_detail_username ON users_detail(username);
CREATE INDEX IF NOT EXISTS idx_users_detail_email ON users_detail(email);
