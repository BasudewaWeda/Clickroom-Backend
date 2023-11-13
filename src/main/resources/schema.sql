CREATE TABLE room
(
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    capacity INT NOT NULL DEFAULT 0,
    location VARCHAR(20)
);

CREATE TABLE schedule (
	id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    borrow_date DATE,
    start_time TIME,
    end_time TIME,
    lendee VARCHAR(50),
    lender VARCHAR(50),
    room_id BIGINT,
    FOREIGN KEY (room_id) REFERENCES room(id) ON UPDATE CASCADE ON DELETE CASCADE
);