SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

use movieservice;


CREATE TABLE User (
    user_id VARCHAR(10) not null primary key,
    name VARCHAR(45) not null,
    password INT not null
);

CREATE TABLE Theater (
theater_id VARCHAR(10) not null primary key,
t_name VARCHAR(45) not null,
total_screen INT not null
);

CREATE TABLE Movie (
movie_id VARCHAR(10) not null primary key,
title VARCHAR(45) not null,
genre VARCHAR(45) not null
);

CREATE TABLE Screen (
screen_id VARCHAR(10) not null primary key,
theater_id VARCHAR(10) not null, 
FOREIGN KEY (theater_id) REFERENCES Theater (theater_id),
s_name VARCHAR(45) not null,
total_seats INT
);

CREATE TABLE Seat (
seat_id VARCHAR(10) not null primary key,
screen_id VARCHAR(10) not null, 
FOREIGN KEY (screen_id) REFERENCES Screen (screen_id),
row_num VARCHAR(10) not null,
seat_num INT not null,
is_seats BOOLEAN
);

CREATE TABLE MovieSchedule (
schedule_id VARCHAR(10) not null primary key,
movie_id VARCHAR(10) not null, 
theater_id VARCHAR(10) not null, 
screen_id VARCHAR(10) not null, 
FOREIGN KEY (movie_id) REFERENCES Movie (movie_id),
FOREIGN KEY (theater_id) REFERENCES Theater (theater_id),
FOREIGN KEY (screen_id) REFERENCES Screen (screen_id),
date VARCHAR(45) not null,
time VARCHAR(45) not null
);

CREATE TABLE Reservation (
    reserv_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(10) NOT NULL,
    schedule_id VARCHAR(10),
    reservd_at VARCHAR(45),
    is_canceled BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (schedule_id) REFERENCES MovieSchedule(schedule_id)
);


CREATE TABLE ReservationSeat (
    reserv_id INT NOT NULL,
    seat_id VARCHAR(10) NOT NULL,
    PRIMARY KEY (reserv_id, seat_id),
    FOREIGN KEY (reserv_id) REFERENCES Reservation(reserv_id),
    FOREIGN KEY (seat_id) REFERENCES Seat(seat_id)
);


CREATE TABLE ScheduleSeat (
    schedule_id VARCHAR(10),
    seat_id VARCHAR(10),
    is_reserved BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (schedule_id, seat_id),
    FOREIGN KEY (schedule_id) REFERENCES MovieSchedule(schedule_id),
    FOREIGN KEY (seat_id) REFERENCES Seat(seat_id)
);


INSERT INTO Theater (theater_id, t_name, total_screen) VALUES
('T1', '롯데시네마', 5),
('T2', 'CGV', 5),
('T3', '메가박스', 5);


INSERT INTO Movie (movie_id, title, genre) VALUES
('M1', '좀비딸', '기타'),
('M2', '귀멸의칼날', '애니메이션'),
('M3', 'F1', '액션'),
('M4', '오징어게임', '드라마'),
('M5', '인셉션', 'SF'),
('M6', '토르:러브앤썬더', '액션'),
('M7', '스파이더맨', '히어로');

INSERT INTO Screen (screen_id, theater_id, s_name, total_seats) VALUES
('S1', 'T1', '1관', 25),
('S2', 'T1', '2관', 25),
('S3', 'T1', '3관', 25),
('S4', 'T2', '1관', 25),
('S5', 'T2', '2관', 25),
('S6', 'T2', '3관', 25),
('S7', 'T3', '1관', 25),
('S8', 'T3', '2관', 25),
('S9', 'T3', '3관', 25);




INSERT INTO MovieSchedule (schedule_id, movie_id, theater_id, screen_id, date, time) VALUES
-- 좀비딸
('SCH1', 'M1', 'T1', 'S1', '2025-08-26', '14:00'),
('SCH2', 'M1', 'T1', 'S1', '2025-08-26', '16:00'),
('SCH3', 'M1', 'T2', 'S4', '2025-08-27', '13:00'),
('SCH4', 'M1', 'T2', 'S5', '2025-08-27', '15:00'),
('SCH5', 'M1', 'T3', 'S7', '2025-08-28', '18:00'),
('SCH6', 'M1', 'T3', 'S9', '2025-08-28', '20:00'),

-- 귀멸의칼날
('SCH7', 'M2', 'T1', 'S1', '2025-08-26', '12:00'),
('SCH8', 'M2', 'T1', 'S2', '2025-08-26', '14:00'),
('SCH9', 'M2', 'T2', 'S4', '2025-08-27', '16:00'),
('SCH10', 'M2', 'T2', 'S5', '2025-08-27', '18:00'),
('SCH11', 'M2', 'T3', 'S7', '2025-08-28', '13:00'),
('SCH12', 'M2', 'T3', 'S8', '2025-08-28', '15:00'),

-- F1
('SCH13', 'M3', 'T1', 'S1', '2025-08-28', '13:00'),
('SCH14', 'M3', 'T1', 'S2', '2025-08-28', '16:00'),
('SCH15', 'M3', 'T2', 'S4', '2025-08-29', '14:00'),
('SCH16', 'M3', 'T2', 'S5', '2025-08-29', '17:00'),
('SCH17', 'M3', 'T3', 'S7', '2025-08-30', '15:00'),
('SCH18', 'M3', 'T3', 'S9', '2025-08-30', '18:00'),

-- 오징어게임
('SCH19', 'M4', 'T1', 'S1', '2025-09-01', '14:00'),
('SCH20', 'M4', 'T1', 'S2', '2025-09-01', '16:00'),
('SCH21', 'M4', 'T2', 'S4', '2025-09-02', '13:00'),
('SCH22', 'M4', 'T2', 'S5', '2025-09-02', '15:00'),
('SCH23', 'M4', 'T3', 'S7', '2025-09-03', '18:00'),
('SCH24', 'M4', 'T3', 'S9', '2025-09-03', '20:00'),

-- 인셉션
('SCH25', 'M5', 'T1', 'S1', '2025-09-05', '12:00'),
('SCH26', 'M5', 'T1', 'S2', '2025-09-05', '14:00'),
('SCH27', 'M5', 'T2', 'S4', '2025-09-06', '16:00'),
('SCH28', 'M5', 'T2', 'S5', '2025-09-06', '18:00'),
('SCH29', 'M5', 'T3', 'S7', '2025-09-07', '15:00'),
('SCH30', 'M5', 'T3', 'S9', '2025-09-07', '17:00'),

-- 토르: 러브앤썬더
('SCH31', 'M6', 'T1', 'S1', '2025-09-08', '13:00'),
('SCH32', 'M6', 'T1', 'S2', '2025-09-08', '16:00'),
('SCH33', 'M6', 'T2', 'S4', '2025-09-09', '14:00'),
('SCH34', 'M6', 'T2', 'S5', '2025-09-09', '17:00'),

-- 스파이더맨
('SCH35', 'M7', 'T1', 'S1', '2025-09-11', '14:00'),
('SCH36', 'M7', 'T1', 'S2', '2025-09-11', '17:00'),
('SCH37', 'M7', 'T2', 'S4', '2025-09-12', '15:00'),
('SCH38', 'M7', 'T2', 'S5', '2025-09-12', '18:00'),
('SCH39', 'M7', 'T3', 'S7', '2025-09-13', '16:00'),
('SCH40', 'M7', 'T3', 'S9', '2025-09-13', '19:00');


INSERT INTO Seat (seat_id, screen_id, row_num, seat_num, is_seats) VALUES
-- S1
('S1-A1','S1','A',1,TRUE),('S1-A2','S1','A',2,TRUE),('S1-A3','S1','A',3,TRUE),('S1-A4','S1','A',4,TRUE),('S1-A5','S1','A',5,TRUE),
('S1-B1','S1','B',1,TRUE),('S1-B2','S1','B',2,TRUE),('S1-B3','S1','B',3,TRUE),('S1-B4','S1','B',4,TRUE),('S1-B5','S1','B',5,TRUE),
('S1-C1','S1','C',1,TRUE),('S1-C2','S1','C',2,TRUE),('S1-C3','S1','C',3,TRUE),('S1-C4','S1','C',4,TRUE),('S1-C5','S1','C',5,TRUE),
('S1-D1','S1','D',1,TRUE),('S1-D2','S1','D',2,TRUE),('S1-D3','S1','D',3,TRUE),('S1-D4','S1','D',4,TRUE),('S1-D5','S1','D',5,TRUE),
('S1-E1','S1','E',1,TRUE),('S1-E2','S1','E',2,TRUE),('S1-E3','S1','E',3,TRUE),('S1-E4','S1','E',4,TRUE),('S1-E5','S1','E',5,TRUE),

-- S2
('S2-A1','S2','A',1,TRUE),('S2-A2','S2','A',2,TRUE),('S2-A3','S2','A',3,TRUE),('S2-A4','S2','A',4,TRUE),('S2-A5','S2','A',5,TRUE),
('S2-B1','S2','B',1,TRUE),('S2-B2','S2','B',2,TRUE),('S2-B3','S2','B',3,TRUE),('S2-B4','S2','B',4,TRUE),('S2-B5','S2','B',5,TRUE),
('S2-C1','S2','C',1,TRUE),('S2-C2','S2','C',2,TRUE),('S2-C3','S2','C',3,TRUE),('S2-C4','S2','C',4,TRUE),('S2-C5','S2','C',5,TRUE),
('S2-D1','S2','D',1,TRUE),('S2-D2','S2','D',2,TRUE),('S2-D3','S2','D',3,TRUE),('S2-D4','S2','D',4,TRUE),('S2-D5','S2','D',5,TRUE),
('S2-E1','S2','E',1,TRUE),('S2-E2','S2','E',2,TRUE),('S2-E3','S2','E',3,TRUE),('S2-E4','S2','E',4,TRUE),('S2-E5','S2','E',5,TRUE),

-- S3
('S3-A1','S3','A',1,TRUE),('S3-A2','S3','A',2,TRUE),('S3-A3','S3','A',3,TRUE),('S3-A4','S3','A',4,TRUE),('S3-A5','S3','A',5,TRUE),
('S3-B1','S3','B',1,TRUE),('S3-B2','S3','B',2,TRUE),('S3-B3','S3','B',3,TRUE),('S3-B4','S3','B',4,TRUE),('S3-B5','S3','B',5,TRUE),
('S3-C1','S3','C',1,TRUE),('S3-C2','S3','C',2,TRUE),('S3-C3','S3','C',3,TRUE),('S3-C4','S3','C',4,TRUE),('S3-C5','S3','C',5,TRUE),
('S3-D1','S3','D',1,TRUE),('S3-D2','S3','D',2,TRUE),('S3-D3','S3','D',3,TRUE),('S3-D4','S3','D',4,TRUE),('S3-D5','S3','D',5,TRUE),
('S3-E1','S3','E',1,TRUE),('S3-E2','S3','E',2,TRUE),('S3-E3','S3','E',3,TRUE),('S3-E4','S3','E',4,TRUE),('S3-E5','S3','E',5,TRUE),

-- S4
('S4-A1','S4','A',1,TRUE),('S4-A2','S4','A',2,TRUE),('S4-A3','S4','A',3,TRUE),('S4-A4','S4','A',4,TRUE),('S4-A5','S4','A',5,TRUE),
('S4-B1','S4','B',1,TRUE),('S4-B2','S4','B',2,TRUE),('S4-B3','S4','B',3,TRUE),('S4-B4','S4','B',4,TRUE),('S4-B5','S4','B',5,TRUE),
('S4-C1','S4','C',1,TRUE),('S4-C2','S4','C',2,TRUE),('S4-C3','S4','C',3,TRUE),('S4-C4','S4','C',4,TRUE),('S4-C5','S4','C',5,TRUE),
('S4-D1','S4','D',1,TRUE),('S4-D2','S4','D',2,TRUE),('S4-D3','S4','D',3,TRUE),('S4-D4','S4','D',4,TRUE),('S4-D5','S4','D',5,TRUE),
('S4-E1','S4','E',1,TRUE),('S4-E2','S4','E',2,TRUE),('S4-E3','S4','E',3,TRUE),('S4-E4','S4','E',4,TRUE),('S4-E5','S4','E',5,TRUE),

-- S5
('S5-A1','S5','A',1,TRUE),('S5-A2','S5','A',2,TRUE),('S5-A3','S5','A',3,TRUE),('S5-A4','S5','A',4,TRUE),('S5-A5','S5','A',5,TRUE),
('S5-B1','S5','B',1,TRUE),('S5-B2','S5','B',2,TRUE),('S5-B3','S5','B',3,TRUE),('S5-B4','S5','B',4,TRUE),('S5-B5','S5','B',5,TRUE),
('S5-C1','S5','C',1,TRUE),('S5-C2','S5','C',2,TRUE),('S5-C3','S5','C',3,TRUE),('S5-C4','S5','C',4,TRUE),('S5-C5','S5','C',5,TRUE),
('S5-D1','S5','D',1,TRUE),('S5-D2','S5','D',2,TRUE),('S5-D3','S5','D',3,TRUE),('S5-D4','S5','D',4,TRUE),('S5-D5','S5','D',5,TRUE),
('S5-E1','S5','E',1,TRUE),('S5-E2','S5','E',2,TRUE),('S5-E3','S5','E',3,TRUE),('S5-E4','S5','E',4,TRUE),('S5-E5','S5','E',5,TRUE),

-- S6
('S6-A1','S6','A',1,TRUE),('S6-A2','S6','A',2,TRUE),('S6-A3','S6','A',3,TRUE),('S6-A4','S6','A',4,TRUE),('S6-A5','S6','A',5,TRUE),
('S6-B1','S6','B',1,TRUE),('S6-B2','S6','B',2,TRUE),('S6-B3','S6','B',3,TRUE),('S6-B4','S6','B',4,TRUE),('S6-B5','S6','B',5,TRUE),
('S6-C1','S6','C',1,TRUE),('S6-C2','S6','C',2,TRUE),('S6-C3','S6','C',3,TRUE),('S6-C4','S6','C',4,TRUE),('S6-C5','S6','C',5,TRUE),
('S6-D1','S6','D',1,TRUE),('S6-D2','S6','D',2,TRUE),('S6-D3','S6','D',3,TRUE),('S6-D4','S6','D',4,TRUE),('S6-D5','S6','D',5,TRUE),
('S6-E1','S6','E',1,TRUE),('S6-E2','S6','E',2,TRUE),('S6-E3','S6','E',3,TRUE),('S6-E4','S6','E',4,TRUE),('S6-E5','S6','E',5,TRUE),

-- S7
('S7-A1','S7','A',1,TRUE),('S7-A2','S7','A',2,TRUE),('S7-A3','S7','A',3,TRUE),('S7-A4','S7','A',4,TRUE),('S7-A5','S7','A',5,TRUE),
('S7-B1','S7','B',1,TRUE),('S7-B2','S7','B',2,TRUE),('S7-B3','S7','B',3,TRUE),('S7-B4','S7','B',4,TRUE),('S7-B5','S7','B',5,TRUE),
('S7-C1','S7','C',1,TRUE),('S7-C2','S7','C',2,TRUE),('S7-C3','S7','C',3,TRUE),('S7-C4','S7','C',4,TRUE),('S7-C5','S7','C',5,TRUE),
('S7-D1','S7','D',1,TRUE),('S7-D2','S7','D',2,TRUE),('S7-D3','S7','D',3,TRUE),('S7-D4','S7','D',4,TRUE),('S7-D5','S7','D',5,TRUE),
('S7-E1','S7','E',1,TRUE),('S7-E2','S7','E',2,TRUE),('S7-E3','S7','E',3,TRUE),('S7-E4','S7','E',4,TRUE),('S7-E5','S7','E',5,TRUE),

-- S8
('S8-A1','S8','A',1,TRUE),('S8-A2','S8','A',2,TRUE),('S8-A3','S8','A',3,TRUE),('S8-A4','S8','A',4,TRUE),('S8-A5','S8','A',5,TRUE),
('S8-B1','S8','B',1,TRUE),('S8-B2','S8','B',2,TRUE),('S8-B3','S8','B',3,TRUE),('S8-B4','S8','B',4,TRUE),('S8-B5','S8','B',5,TRUE),
('S8-C1','S8','C',1,TRUE),('S8-C2','S8','C',2,TRUE),('S8-C3','S8','C',3,TRUE),('S8-C4','S8','C',4,TRUE),('S8-C5','S8','C',5,TRUE),
('S8-D1','S8','D',1,TRUE),('S8-D2','S8','D',2,TRUE),('S8-D3','S8','D',3,TRUE),('S8-D4','S8','D',4,TRUE),('S8-D5','S8','D',5,TRUE),
('S8-E1','S8','E',1,TRUE),('S8-E2','S8','E',2,TRUE),('S8-E3','S8','E',3,TRUE),('S8-E4','S8','E',4,TRUE),('S8-E5','S8','E',5,TRUE),

-- S9
('S9-A1','S9','A',1,TRUE),('S9-A2','S9','A',2,TRUE),('S9-A3','S9','A',3,TRUE),('S9-A4','S9','A',4,TRUE),('S9-A5','S9','A',5,TRUE),
('S9-B1','S9','B',1,TRUE),('S9-B2','S9','B',2,TRUE),('S9-B3','S9','B',3,TRUE),('S9-B4','S9','B',4,TRUE),('S9-B5','S9','B',5,TRUE),
('S9-C1','S9','C',1,TRUE),('S9-C2','S9','C',2,TRUE),('S9-C3','S9','C',3,TRUE),('S9-C4','S9','C',4,TRUE),('S9-C5','S9','C',5,TRUE),
('S9-D1','S9','D',1,TRUE),('S9-D2','S9','D',2,TRUE),('S9-D3','S9','D',3,TRUE),('S9-D4','S9','D',4,TRUE),('S9-D5','S9','D',5,TRUE),
('S9-E1','S9','E',1,TRUE),('S9-E2','S9','E',2,TRUE),('S9-E3','S9','E',3,TRUE),('S9-E4','S9','E',4,TRUE),('S9-E5','S9','E',5,TRUE);




