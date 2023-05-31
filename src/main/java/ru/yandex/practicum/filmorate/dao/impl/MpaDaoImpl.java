package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.entity.film.Mpa;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class MpaDaoImpl implements MpaDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Mpa> getMpaById(int id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM \"mpa\" m WHERE MPA_ID = ?;", id);
        if (mpaRows.next()) {
            Mpa mpa = Mpa.builder()
                    .id(mpaRows.getInt("MPA_ID"))
                    .name(Objects.requireNonNull(mpaRows.getString("NAME")).strip())
                    .build();
            log.info("Найден рейтинг: {}", mpa);
            return Optional.of(mpa);
        } else {
            log.info("Рейтинг с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM \"mpa\" m";
        List<Mpa> mpaList = jdbcTemplate.query(sql, (rs, rowNum) -> Mpa.builder()
                .id(rs.getInt("MPA_ID"))
                .name(rs.getString("NAME").strip())
                .build());
        log.info("Список рейтингов MPA получен, длина = {}", mpaList.size());
        return mpaList;
    }
}
