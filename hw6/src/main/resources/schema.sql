create table authors (
    id bigint primary key auto_increment,
    name varchar(255) not null unique
);

create table if not exists books (
	id bigint primary key auto_increment,
	title varchar(255) not null,
	author_id bigint references authors
);

create table if not exists genres (
	id bigint primary key auto_increment,
	genre varchar(255) not null unique
);

create table if not exists book_genre (
	book_id bigint references books,
	genre_id bigint references genres,
	primary key (book_id, genre_id)
);

create table if not exists comments (
    id bigint primary key auto_increment,
    book_id bigint references books,
    comment varchar(2048) not null
)
