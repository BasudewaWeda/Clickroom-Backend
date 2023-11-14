INSERT INTO room(id, capacity, location) VALUES (100, 50, 'fmipa');
INSERT INTO room(id, capacity, location) VALUES (101, 40, 'fk');
INSERT INTO room(id, capacity, location) VALUES (102, 30, 'fh');

INSERT INTO schedule(id, borrow_date, start_time, end_time, lendee, lender, detail, room_id) VALUES (100, '2023-11-08', '10:30', '12:00', '2022A', 'admin1', 'Kuliah Rekayasa Perangkat Lunak', 100);
INSERT INTO schedule(id, borrow_date, start_time, end_time, lendee, lender, detail, room_id) VALUES (101, '2023-11-08', '08:00', '10:00', '2022D', 'admin1', 'Kuliah Basis Data', 100);
INSERT INTO schedule(id, borrow_date, start_time, end_time, lendee, lender, detail, room_id) VALUES (102, '2023-11-09', '10:30', '12:00', '2022B', 'admin1', 'Kuliah Teori Bahasa Dan Otomata', 101);
INSERT INTO schedule(id, borrow_date, start_time, end_time, lendee, lender, detail, room_id) VALUES (103, '2023-11-08', '12:00', '13:30', '2022C', 'admin1', 'Kuliah Pemrograman Berorientasi Objek', 102);