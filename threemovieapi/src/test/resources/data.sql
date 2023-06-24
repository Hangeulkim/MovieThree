INSERT INTO movie_data(id, admission_code, category, country, making_note, movie_id, name_en, name_kr, netizen_avg_rate,
                       poster, release_date, reservation_rank, reservation_rate, running_time, summary, total_audience)
VALUES ("12345678-1234-1234-1234-000000000000", null, 한국, null, null, "moviethree_2023", null, "moviethree", 10.0, null,
        2023, 1, 10.0, 365, null, 20230505);
INSERT INTO movie_creator(id, link, name_en, name_kr, role_kr, movie_id)
VALUES ("12345678-1234-1234-1234-000000000001", null, hdobby, 한글, 총괄, moviethree_2023);
INSERT INTO movie_creator(id, link, name_en, type, movie_id)
VALUES ("12345678-1234-1234-1234-000000000002", null, hdobby, image, moviethree_2023);

INSERT INTO theater_data(id, addr_en, addr_kr, brch_en, brch_kr, city, movie_theater, theater_code)
VALUES ("12345678-1234-1234-1234-000000000003", null, "서울시 강서구", mt, 무비쓰리, 서울, mt, mtmt);

INSERT INTO show_time(id, play_kind, screen_en, screen_kr, show_ymd, total_seat, updated_at, movie_id, theater_data_id)
VALUES ("12345678-1234-1234-1234-000000000004", 4d, 2, 2관, 20230506, 203, 202305060708, "moviethree_2023",
        "12345678-1234-1234-1234-000000000003");

INSERT INTO show_time_reserve(id, end_time, rest_seat, start_time, ticket_page, updated_at, show_time_id)
VALUES ("12345678-1234-1234-1234-000000000005", 202306231200, 150, 202306231000, "http://moviethree.synology.me",
        202306231200, "12345678-1234-1234-1234-000000000004");

INSERT INTO user_login(id, email, password, role)
VALUES ("12345678-1234-1234-1234-000000000006", "test@movie.com", moviethreepass, "USER");

INSERT INTO user_data(id, birth, brch, categories, nick_name, sex, user_login_id)
VALUES ("12345678-1234-1234-1234-000000000007", "2023 - 04 - 04", null, null, mttt, true,
        "12345678-1234-1234-1234-000000000006");
