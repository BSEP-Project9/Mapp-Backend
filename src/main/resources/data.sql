-- ROLE
INSERT INTO role(name, seniority) VALUES('ROLE_ADMIN', 'NONE');
INSERT INTO role(name, seniority) VALUES('ROLE_SWE', 'JUNIOR');
INSERT INTO role(name, seniority) VALUES('ROLE_HR', 'NONE');
INSERT INTO role(name, seniority) VALUES('ROLE_PM', 'NONE');

-- ADDRESS
INSERT INTO address(country, city, street_name, street_number) VALUES('Serbia', 'Belgrade', 'Kosovska', '27');
INSERT INTO address(country, city, street_name, street_number) VALUES('Serbia', 'Novi Sad', 'Danila Kisa', '15');
INSERT INTO address(country, city, street_name, street_number) VALUES('Serbia', 'Novi Sad', 'Strumicka', '23');
INSERT INTO address(country, city, street_name, street_number) VALUES('Serbia', 'Novi Sad', 'Radnicka', '15');

-- USER
INSERT INTO "user"(email, password, name, surname, phone_number, is_activated, role_id, address_id, start_of_employment)
VALUES('admin@gmail.com', '$2a$10$AmSd1Bo28xit.Cq947HO0exPCnL2VUZ5eTAwgkMP7MfDl6sscz60O', 'Admin', 'Admin', '0635483452', true, 1, 1, '2023-05-30');
INSERT INTO "user"(email, password, name, surname, phone_number, is_activated, role_id, address_id, start_of_employment)
VALUES('stefan@gmail.com', '$2a$10$AmSd1Bo28xit.Cq947HO0exPCnL2VUZ5eTAwgkMP7MfDl6sscz60O', 'Stefan', 'Pekez', '0665483452', false, 2, 2, '2023-03-20');
INSERT INTO "user"(email, password, name, surname, phone_number, is_activated, role_id, address_id, start_of_employment)
VALUES('jelena@gmail.com', '$2a$10$AmSd1Bo28xit.Cq947HO0exPCnL2VUZ5eTAwgkMP7MfDl6sscz60O', 'Jelena', 'Petric', '0665483452', true, 2, 3, '2023-01-14');
INSERT INTO "user"(email, password, name, surname, phone_number, is_activated, role_id, address_id, start_of_employment)
VALUES('bojana@gmail.com', '$2a$10$AmSd1Bo28xit.Cq947HO0exPCnL2VUZ5eTAwgkMP7MfDl6sscz60O', 'Bojana', 'Zekanovic', '0665483452', true, 2, 4, '2000-07-08');
INSERT INTO "user"(email, password, name, surname, phone_number, is_activated, role_id, address_id, start_of_employment)
VALUES('ana@gmail.com', '$2a$10$AmSd1Bo28xit.Cq947HO0exPCnL2VUZ5eTAwgkMP7MfDl6sscz60O', 'ana', 'Anic', '0665483452', true, 3, 4, '2000-07-08');
INSERT INTO "user"(email, password, name, surname, phone_number, is_activated, role_id, address_id, start_of_employment)
VALUES('sara@gmail.com', '$2a$10$AmSd1Bo28xit.Cq947HO0exPCnL2VUZ5eTAwgkMP7MfDl6sscz60O', 'Sara', 'saric', '0665483452', true, 4, 4, '2000-07-08');

-- SKILL
INSERT INTO skill(name, rating, user_id) VALUES('Git', 2, 3);
INSERT INTO skill(name, rating, user_id) VALUES('Java', 5, 3);
INSERT INTO skill(name, rating, user_id) VALUES('English', 4, 4);

-- PROJECT
INSERT INTO project(name, start_date, end_date) VALUES('Project A', '2023-06-10', '2023-08-10');
INSERT INTO project(name, start_date, end_date) VALUES('Project B', '2023-01-01', '2023-06-20');
INSERT INTO project(name, start_date, end_date) VALUES('Project C', '2023-05-30', '2023-10-30');

-- CONTRIBUTION
INSERT INTO contribution(job_description, job_start_time, job_end_time, project_id, worker_id)
VALUES('Worked a lot', '2023-06-10', '2023-08-05', 1, 2);
INSERT INTO contribution(job_description, job_start_time, job_end_time, project_id, worker_id)
VALUES('Worked a bit more', '2023-02-15', '2023-08-27', 2, 4);
INSERT INTO contribution(job_description, job_start_time, job_end_time, project_id, worker_id)
VALUES('Worked the most', '2022-11-16', '2023-01-12', 3, 3);
INSERT INTO contribution(job_description, job_start_time, job_end_time, project_id, worker_id)
VALUES('sdad', '2023-02-15', '2023-02-10', 2, 3);
INSERT INTO contribution(job_description, job_start_time, job_end_time, project_id, worker_id)
VALUES('ghfg', '2022-11-16', '2023-01-12', 3, 4);
INSERT INTO contribution(job_description, job_start_time, job_end_time, project_id, worker_id)
VALUES('ghfg', '2022-11-16', '2023-01-12', 2, 6);
INSERT INTO contribution(job_description, job_start_time, job_end_time, project_id, worker_id)
VALUES('ghfg', '2022-11-16', '2023-01-12', 3, 6);
