use inove;

create table tb_admin_course (admin_id bigint not null, course_id bigint not null) engine=InnoDB;
create table tb_content (id bigint not null auto_increment, description varchar(255), title varchar(255), section_id bigint, primary key (id)) engine=InnoDB;
create table tb_course (id bigint not null auto_increment, description varchar(255), name varchar(255), primary key (id)) engine=InnoDB;
create table tb_feedback (id bigint not null auto_increment, comment varchar(255), course_id bigint, student_id bigint, primary key (id)) engine=InnoDB;
create table tb_instructor_course (instructor_id bigint not null, course_id bigint not null) engine=InnoDB;
create table tb_school (id bigint not null auto_increment, city varchar(255), federative_unit varchar(255), name varchar(255), primary key (id)) engine=InnoDB;
create table tb_section (id bigint not null auto_increment, description varchar(255), title varchar(255), course_id bigint, primary key (id)) engine=InnoDB;
create table tb_student_course (student_id bigint not null, course_id bigint not null) engine=InnoDB;
create table tb_user (id bigint not null auto_increment, birth_date datetime(6), cpf varchar(255) not null, email varchar(255) not null, name varchar(255) not null, password varchar(255) not null, role enum ('ADMINISTRATOR','INSTRUCTOR','STUDENT'), school_id bigint, primary key (id)) engine=InnoDB;
alter table tb_feedback add constraint UK_7h94xafctgk9f5dnjrsbex0nu unique (student_id);
alter table tb_user add constraint UK_869sa3rebuf3nm0d4jwxdtouk unique (cpf);
alter table tb_user add constraint UK_4vih17mube9j7cqyjlfbcrk4m unique (email);
alter table tb_admin_course add constraint FK5kj3lvbgd3rxhknt70gwwr1vu foreign key (course_id) references tb_course (id);
alter table tb_admin_course add constraint FKot9sg4el78uimtgy0xavbmxp foreign key (admin_id) references tb_user (id);
alter table tb_content add constraint FKs9bf9or8p2agsk6goxdtx311j foreign key (section_id) references tb_section (id);
alter table tb_content add column theorical_content_file_name varchar(255);
alter table tb_content add column theorical_content_file_url varchar(255);
alter table tb_content add column theorical_content_text varchar(255);
alter table tb_content add column video_title varchar(255);
alter table tb_content add column video_url varchar(255);
alter table tb_feedback add constraint FK9i84l1o5wfrm8ayur3cxsi8gw foreign key (course_id) references tb_course (id);
alter table tb_feedback add constraint FK3lj962uxt0lyw81ivx0r5cm50 foreign key (student_id) references tb_user (id);
alter table tb_instructor_course add constraint FKjrev2w3nkxnqpi7f5y3mjewcr foreign key (course_id) references tb_course (id);
alter table tb_instructor_course add constraint FKgylfytld029fnl0om6vtfn2oj foreign key (instructor_id) references tb_user (id);
alter table tb_section add constraint FKfcg6y2tv2q6bx5ajdpra7mba0 foreign key (course_id) references tb_course (id);
alter table tb_student_course add constraint FKn8iklc57fcw2r42l84wvv902s foreign key (student_id) references tb_user (id);
alter table tb_student_course add constraint FKaj059itv6ls7nwg5mk0oh0slq foreign key (course_id) references tb_course (id);
alter table tb_user add constraint FK1uo2sv9ra9fbpkets01b2c442 foreign key (school_id) references tb_school (id);