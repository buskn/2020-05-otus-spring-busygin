package ru.otus.hw4.core.credentials;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Getter
@ToString
public class Credentials {
    private String
        name = "",
        surname = "";

    public void setName(String name) {
        if (name == null)
            throw new CredentialsException("name mustn't be null");
        this.name = name;
    }

    public void setSurname(String surname) {
        if (surname == null)
            throw new CredentialsException("surname mustn't be null");
        this.surname = surname;
    }

    public boolean isLoggedIn() {
        return ! ("".equals(name) && "".equals(surname));
    }

    public String getUsername() {
        if (!isLoggedIn())
            throw new CredentialsException("invoke getUsername() before logged in");
        return String.join(" ", name, surname);
    }
}
