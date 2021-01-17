insert into authors(id, name) values (1, 'author1');
insert into authors(id, name) values (2, 'author2');
insert into authors(id, name) values (3, 'author3');
insert into authors(id, name) values (4, 'author4');
insert into authors(id, name) values (5, '_another_author5');
insert into authors(id, name) values (6, '_ANOTHER_author6');
insert into authors(id, name) values (7, 'author_for_delete');

insert into genres(id, genre) values(1, 'genre1');
insert into genres(id, genre) values(2, 'genre2');
insert into genres(id, genre) values(3, 'genre3');
insert into genres(id, genre) values(4, 'genre4');
insert into genres(id, genre) values(5, '_another_genre5');
insert into genres(id, genre) values(6, '_ANOTHER_genre6');
insert into genres(id, genre) values(7, 'genre_for_delete');

insert into books(id, title, author_id) values (1, 'book1', 1);
insert into books(id, title, author_id) values (2, 'some_book2', 2);
insert into books(id, title, author_id) values (3, 'SoMe_book3', 2);
insert into books(id, title, author_id) values (4, 'book_for_delete', 2);

insert into book_genre(book_id, genre_id) values (1, 1);
insert into book_genre(book_id, genre_id) values (1, 2);
insert into book_genre(book_id, genre_id) values (2, 2);
insert into book_genre(book_id, genre_id) values (2, 3);

insert into comments(id, book_id, comment) values(1, 1, 'comment1');
insert into comments(id, book_id, comment) values(2, 1, 'comment2');
insert into comments(id, book_id, comment) values(3, 1, 'comment3');
insert into comments(id, book_id, comment) values(4, 2, 'comment4');
insert into comments(id, book_id, comment) values(5, 2, 'comment_for_delete');
