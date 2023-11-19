CREATE TABLE room
(
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    capacity INT NOT NULL DEFAULT 0,
    location VARCHAR(50)
);

CREATE TABLE schedule (
	id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    borrow_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    lendee VARCHAR(50) NOT NULL,
    lender VARCHAR(50) NOT NULL,
    detail VARCHAR(100) NOT NULL,
    room_id BIGINT,
    FOREIGN KEY (room_id) REFERENCES room(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE facility (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    facility_name VARCHAR(50) NOT NULL,
    amount INT NOT NULL,
    room_id BIGINT,
    FOREIGN KEY (room_id) REFERENCES room(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE request (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    borrow_date DATE NOT NULL,
    start_Time TIME NOT NULL,
    end_time TIME NOT NULL,
    lendee VARCHAR(50) NOT NULL,
    detail VARCHAR(100) NOT NULL,
    status ENUM('Pending', 'Accepted', 'Declined'),
    room_id BIGINT NOT NULL
);