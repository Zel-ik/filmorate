package ru.yandex.practicum.filmorate.entity.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private long id;

    @Email
    private String email;

    @UserLogin
    private String login;
    private String name;

    @Past
    private LocalDate birthday;
}
