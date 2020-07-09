package ru.otus.hw5.dao;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class BookBuilderTest {
    @Configuration
    @Import(Book.Builder.class)
    public static class Config {}

    @Autowired Book.Builder builder;

    @Test
    void whenSetTitle_thenSuccess() {
        String title = "someTitle";
        assertThat(builder.setTitle(title)).isEqualTo(builder);
        assertThat(builder.getTitle().get()).isEqualTo(title);
    }

    @Test
    void whenSetAuthor_thenSuccess() {
        Author author = mock(Author.class);
        assertThat(builder.setAuthor(author)).isEqualTo(builder);
        assertThat(builder.getAuthor().get()).isEqualTo(author);
    }

    @Test
    void whenSetGenres_thenSuccess() {
        val genres = List.of(mock(Genre.class), mock(Genre.class));
        assertThat(builder.setGenres(genres)).isEqualTo(builder);
        assertThat(builder.getGenres()).containsExactlyInAnyOrderElementsOf(genres);
    }

    @Test
    void whenAddGenre_thenSuccess() {
        val genre = mock(Genre.class);
        assertThat(builder.addGenre(genre)).isEqualTo(builder);
        assertThat(builder.getGenres()).contains(genre);
    }

    @Test
    void whenRemoveGenre_thenSuccess() {
        val genre = mock(Genre.class);
        builder.addGenre(genre);
        assertThat(builder.removeGenre(genre)).isEqualTo(builder);
        assertThat(builder.getGenres()).doesNotContain(genre);
    }

    @Test
    void givenNullTitle_whenReady_returnFalse() {
        builder.setTitle(null);
        builder.setAuthor(mock(Author.class));
        builder.setGenres(List.of());

        assertThat(builder.ready()).isFalse();
    }

    @Test
    void givenEmptyTitle_whenReady_returnFalse() {
        builder.setTitle("");
        builder.setAuthor(mock(Author.class));
        builder.setGenres(List.of());

        assertThat(builder.ready()).isFalse();
    }

    @Test
    void givenNullAuthor_whenReady_returnFalse() {
        builder.setTitle("someTitle");
        builder.setAuthor(null);
        builder.setGenres(List.of());

        assertThat(builder.ready()).isFalse();
    }

    @Test
    void givenFullInfo_whenReady_returnTrue() {
        builder.setTitle("someTitle");
        builder.setAuthor(mock(Author.class));
        builder.setGenres(List.of());

        assertThat(builder.ready()).isTrue();
    }

    @Test
    void whenBuild_thenSuccess() {
        val title = "someTitle";
        val author = mock(Author.class);
        val genres = List.of(mock(Genre.class));
        builder.setTitle(title);
        builder.setAuthor(author);
        builder.setGenres(genres);

        assertThat(builder.build()).isEqualTo(new Book(0, title, author, genres));
    }
}