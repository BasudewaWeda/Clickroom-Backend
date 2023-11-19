INSERT INTO room(id, name, capacity, location) VALUES (100, 'Ruang 2.2', 50, 'fmipa');
INSERT INTO room(id, name, capacity, location) VALUES (101, 'Ruang 2.1', 40, 'fk');
INSERT INTO room(id, name, capacity, location) VALUES (102, 'Ruang 2.3', 30, 'fh');
INSERT INTO room(id, name, capacity, location) VALUES (103, 'Ruang 2.4', 40, 'ft');

INSERT INTO schedule(id, borrow_date, start_time, end_time, lendee, lender, detail, room_id) VALUES (100, '2023-11-08', '10:30', '12:00', '2022A', 'admin1', 'Kuliah Rekayasa Perangkat Lunak', 100);
INSERT INTO schedule(id, borrow_date, start_time, end_time, lendee, lender, detail, room_id) VALUES (101, '2023-11-08', '08:00', '10:00', '2022D', 'admin1', 'Kuliah Basis Data', 100);
INSERT INTO schedule(id, borrow_date, start_time, end_time, lendee, lender, detail, room_id) VALUES (102, '2023-11-09', '10:30', '12:00', '2022B', 'admin1', 'Kuliah Teori Bahasa Dan Otomata', 101);
INSERT INTO schedule(id, borrow_date, start_time, end_time, lendee, lender, detail, room_id) VALUES (103, '2023-11-08', '12:00', '13:30', '2022C', 'admin1', 'Kuliah Pemrograman Berorientasi Objek', 102);

INSERT INTO facility(id, facility_name, amount, room_id) VALUES (100, 'Chair', 40, 100);
INSERT INTO facility(id, facility_name, amount, room_id) VALUES (101, 'Table', 20, 100);
INSERT INTO facility(id, facility_name, amount, room_id) VALUES (102, 'AC', 2, 100);

INSERT INTO request(id, borrow_date, start_time, end_time, lendee, detail, status, room_id) VALUES (100, '2023-11-20', '10:30', '12:00', '2022A', 'Kuliah Rekayasa Perangkat Lunak', 'Pending', 100);
INSERT INTO request(id, borrow_date, start_time, end_time, lendee, detail, status, room_id) VALUES (101, '2023-11-20', '10:30', '12:00', '2022B', 'Kuliah Basis Data', 'Pending', 101);
INSERT INTO request(id, borrow_date, start_time, end_time, lendee, detail, status, room_id) VALUES (102, '2023-11-20', '10:30', '12:00', '2022C', 'Kuliah Teori Bahasa Dan Otomata', 'Pending', 102);
INSERT INTO request(id, borrow_date, start_time, end_time, lendee, detail, status, room_id) VALUES (103, '2023-11-20', '12:30', '15:00', '2022A', 'Kuliah Pemrograman Berorientasi Objek', 'Pending', 103);