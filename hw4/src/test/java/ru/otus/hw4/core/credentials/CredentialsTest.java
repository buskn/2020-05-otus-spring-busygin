package ru.otus.hw4.core.credentials;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CredentialsTest {

    @Configuration
    @Import(Credentials.class)
    static class Config {}

    @Autowired Credentials credentials;

    @Test
    void givenName_whenSetName_thenSuccess() {
        String name = "qwerty";
        credentials.setName(name);
        assertThat(credentials.getName()).isEqualTo(name);
    }

    @Test
    void givenNull_whenSetName_thenThrows() {
        assertThatExceptionOfType(CredentialsException.class)
                .isThrownBy(() -> credentials.setName(null));
    }

    @Test
    void givenSurname_whenSetSurname_thenSuccess() {
        String surname = "qwerty";
        credentials.setSurname(surname);
        assertThat(credentials.getSurname()).isEqualTo(surname);
    }

    @Test
    void givenNull_whenSetSurname_thenThrows() {
        assertThatExceptionOfType(CredentialsException.class)
                .isThrownBy(() -> credentials.setSurname(null));
    }

    @Test
    void givenNonEmptyNameAndSurname_whenIsLoggedIn_thenTrue() {
        credentials.setName("qwerty");
        credentials.setSurname("asdfgh");
        assertThat(credentials.isLoggedIn()).isTrue();
    }

    @Test
    void givenEmptyNameAndSurname_whenIsLoggedIn_thenFalse() {
        credentials.setName("");
        credentials.setSurname("");
        assertThat(credentials.isLoggedIn()).isFalse();
    }

    @Test
    void givenNonEmptyName_whenIsLoggedIn_thenTrue() {
        credentials.setName("qwerty");
        credentials.setSurname("");
        assertThat(credentials.isLoggedIn()).isTrue();
    }

    @Test
    void givenNonEmptySurname_whenIsLoggedIn_thenTrue() {
        credentials.setName("");
        credentials.setSurname("asdfgh");
        assertThat(credentials.isLoggedIn()).isTrue();
    }

    @Test
    void givenLoggedIn_whenGetUsername_thenSuccess() {
        credentials.setName("qwerty");
        credentials.setSurname("asdfgh");
        assertThat(credentials.getUsername())
            .contains("qwerty", "asdfgh");
    }

    @Test
    void givenNotLoggedIn_whenGetUsername_thenThrows() {
        credentials.setName("");
        credentials.setSurname("");
        assertThatExceptionOfType(CredentialsException.class)
                .isThrownBy(() -> credentials.getUsername());
    }
}