-- Πίνακας για τους χρήστες
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,        
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    identity_number VARCHAR(20) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT FALSE, 
    role ENUM('CUSTOMER', 'SECRETARY', 'MECHANIC') NOT NULL
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Πίνακας για τους πελάτες 
CREATE TABLE customers (
    user_id BIGINT PRIMARY KEY,            
    tax_number VARCHAR(20) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL,
    CONSTRAINT fk_customer_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE 
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Πίνακας για τους μηχανικούς 
CREATE TABLE mechanics (
    user_id BIGINT PRIMARY KEY,           
    specialty VARCHAR(100) NOT NULL,
    CONSTRAINT fk_mechanic_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE 
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Πίνακας για τα αυτοκίνητα
CREATE TABLE cars (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    serial_number VARCHAR(50) NOT NULL UNIQUE,
    model VARCHAR(50) NOT NULL,
    make VARCHAR(50) NOT NULL,
    car_type ENUM('PASSENGER', 'TRUCK', 'BUS', 'OTHER') NOT NULL,
    movement_type ENUM('ELECTRIC', 'DIESEL', 'GASOLINE', 'LPG', 'HYBRID', 'OTHER') NOT NULL,
    door_count INT NOT NULL,
    wheel_count INT NOT NULL,
    production_date DATE NOT NULL,
    acquisition_year INT NOT NULL,
    owner_id BIGINT NOT NULL,            
    CONSTRAINT fk_car_owner FOREIGN KEY (owner_id) REFERENCES customers(user_id) ON DELETE CASCADE 
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Πίνακας για τα ραντεβού
CREATE TABLE appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date_time DATETIME NOT NULL,
    reason ENUM('REPAIR', 'SERVICE') NOT NULL,
    problem_description TEXT,              
    creation_date DATETIME NOT NULL,
    status ENUM('CREATED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'CREATED',
    total_cost DECIMAL(10, 2),            
    car_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    assigned_mechanic_id BIGINT,         
    CONSTRAINT fk_appointment_car FOREIGN KEY (car_id) REFERENCES cars(id) ON DELETE CASCADE, 
    CONSTRAINT fk_appointment_customer FOREIGN KEY (customer_id) REFERENCES customers(user_id) ON DELETE CASCADE, 
    CONSTRAINT fk_appointment_mechanic FOREIGN KEY (assigned_mechanic_id) REFERENCES mechanics(user_id) ON DELETE SET NULL 
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Πίνακας για τις εργασίες 
CREATE TABLE work_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description TEXT NOT NULL,
    materials TEXT,                      
    completion_time TIME,                  
    cost DECIMAL(10, 2) NOT NULL,
    appointment_id BIGINT NOT NULL,
    CONSTRAINT fk_workitem_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE cars ADD INDEX idx_car_owner (owner_id);
ALTER TABLE appointments ADD INDEX idx_appointment_car (car_id);
ALTER TABLE appointments ADD INDEX idx_appointment_customer (customer_id);
ALTER TABLE appointments ADD INDEX idx_appointment_mechanic (assigned_mechanic_id);
ALTER TABLE appointments ADD INDEX idx_appointment_datetime (date_time);
ALTER TABLE appointments ADD INDEX idx_appointment_status (status);
ALTER TABLE work_items ADD INDEX idx_workitem_appointment (appointment_id);
ALTER TABLE users ADD INDEX idx_user_lastname (last_name);
ALTER TABLE customers ADD INDEX idx_customer_taxnumber (tax_number); 