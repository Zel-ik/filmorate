package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.entity.user.User;

import java.sql.Date;
import java.sql.*;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getUsers() {
        String sql = "SELECT * FROM \"users\" u";
        List<User> users = jdbcTemplate.query(sql, this::mapRowToUser);
        log.info("Список пользователей получен. Длина = {}", users.size());
        return users;
    }

    @Override
    public Long addUser(User user) {
        String sql = "INSERT INTO \"users\" (EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getEmail().strip());
            preparedStatement.setString(2, user.getLogin().strip());
            preparedStatement.setString(3, user.getName().strip());
            preparedStatement.setDate(4, Date.valueOf(user.getBirthday()));
            return preparedStatement;
        }, keyHolder);
        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        log.info("Пользователь с id={} добавлен", id);
        return id;
    }

    @Override
    public void updateUser(User user) {
        String sql = "UPDATE \"users\" SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? WHERE USER_ID = ?";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());
        log.info("Пользователь c id = {} обновлен", user.getId());
    }

    @Override
    public Optional<User> getUserById(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM \"users\" u WHERE USER_ID = ?", id);
        if (userRows.next()) {
            User user = User.builder()
                    .id(userRows.getLong("USER_ID"))
                    .email(Objects.requireNonNull(userRows.getString("EMAIL")).strip())
                    .login(Objects.requireNonNull(userRows.getString("LOGIN")).strip())
                    .name(Objects.requireNonNull(userRows.getString("NAME")).strip())
                    .birthday(Objects.requireNonNull(userRows.getDate("BIRTHDAY")).toLocalDate())
                    .build();
            log.info("Найден пользователь c id = {} ", user.getId());
            return Optional.of(user);
        } else {
            log.info("Пользователь с id = {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public Set<User> getUserFriends(long id) {
        String sql = "SELECT u.USER_ID, u.EMAIL, u.LOGIN, u.NAME, u.BIRTHDAY FROM \"user_friends\" AS uf\n" +
                "INNER JOIN \"users\" u ON uf.FRIEND_USER_ID = u.USER_ID \n" +
                "WHERE uf.USER_ID = ? ORDER BY u.USER_ID ASC;";
        List<User> friendsList = jdbcTemplate.query(sql, this::mapRowToUser, id);
        log.info("Получен список друзей пользователя с id = {}, длина списка = {}", id, friendsList.size());
        Set<User> userSet = new TreeSet<>(Comparator.comparing(User::getId));
        userSet.addAll(friendsList);
        return userSet;
    }

    @Override
    public void deleteUserById(long userId) {
        String sql = "DELETE FROM \"users\" WHERE USER_ID = ?;";
        jdbcTemplate.update(sql, userId);
    }

    @Override
    public void addFriend(long userId, long friendId) {
        String sql = "SELECT * FROM \"user_friends\" uf WHERE USER_ID = ? AND FRIEND_USER_ID = ?";
        SqlRowSet userFriendsRows = jdbcTemplate.queryForRowSet(sql, friendId, userId);
        if (userFriendsRows.next()) {
            String sql2 = "INSERT INTO \"user_friends\" (USER_ID, FRIEND_USER_ID, IS_ACCEPTED) VALUES (?, ?, 'TRUE')";
            jdbcTemplate.update(sql2, userId, friendId);
            log.info("Дружба подтверждена между пользователями {} и {}", userId, friendId);
            String sql3 = "UPDATE \"user_friends\" SET IS_ACCEPTED = TRUE WHERE USER_ID = ? AND FRIEND_USER_ID = ?";
            jdbcTemplate.update(sql3, friendId, userId);
            log.info("Дружба подтверждена между пользователями {} и {}", friendId, userId);
        } else {
            String sql4 = "INSERT INTO \"user_friends\" (USER_ID, FRIEND_USER_ID) VALUES (?, ?)";
            jdbcTemplate.update(sql4, userId, friendId);
            log.info("Запрос на дружбу пользователь {} отправил пользователю {}", userId, friendId);
        }
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        String sql = "DELETE FROM \"user_friends\"  WHERE USER_ID  = ? AND FRIEND_USER_ID = ?";
        jdbcTemplate.update(sql, userId, friendId);
        jdbcTemplate.update(sql, friendId, userId);
        log.info("Пользователи id={} и id={} теперь не друзья", userId, friendId);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("USER_ID"))
                .email(resultSet.getString("EMAIL"))
                .login(resultSet.getString("LOGIN"))
                .name(resultSet.getString("NAME"))
                .birthday(Objects.requireNonNull(resultSet.getDate("BIRTHDAY")).toLocalDate())
                .build();
    }
}
