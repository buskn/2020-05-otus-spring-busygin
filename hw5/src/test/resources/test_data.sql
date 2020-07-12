insert into authors(id, name) values (1, 'author1');
insert into authors(id, name) values (2, 'author2');
insert into authors(id, name) values (3, 'author3');
insert into authors(id, name) values (4, 'author4');

insert into genres(id, genre) values(1, 'genre1');
insert into genres(id, genre) values(2, 'genre2');
insert into genres(id, genre) values(3, 'genre3');
insert into genres(id, genre) values(4, 'genre4');

insert into books(id, title, author_id) values (1, 'book1', 1);
insert into books(id, title, author_id) values (2, 'book2', 2);
insert into books(id, title, author_id) values (3, 'book3', 2);

insert into book_genre(book_id, genre_id) values (1, 1);
insert into book_genre(book_id, genre_id) values (1, 2);
insert into book_genre(book_id, genre_id) values (2, 2);
insert into book_genre(book_id, genre_id) values (2, 3);