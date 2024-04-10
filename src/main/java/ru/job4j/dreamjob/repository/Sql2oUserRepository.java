package ru.job4j.dreamjob.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.dreamjob.model.User;

import java.util.Optional;
@Repository
@Slf4j
public class Sql2oUserRepository implements UserRepository {
    private final Sql2o sql2o;
    public Sql2oUserRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<User> save(User user) {
        log.info("user{}", user);
        try (var connection = sql2o.open()) {
            var sql = """
                      INSERT INTO users(email, name, password)
                      VALUES (:email, :name, :password)
                      """;
            var query = connection.createQuery(sql, true)
                    .addParameter("email", user.getEmail())
                    .addParameter("name", user.getName())
                    .addParameter("password", user.getPassword());
            int generatedId = query.executeUpdate().getKey(Integer.class);
            user.setId(generatedId);
            return Optional.ofNullable(user);
        } catch (Sql2oException e) {
            log.info("e.getMessage(){}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        try (var connection = sql2o.open()) {
            var sql = "SELECT * FROM users WHERE email = :email AND password = :password";
            var query = connection.createQuery(sql, true)
                    .addParameter("email", email)
                    .addParameter("password", password);
            var user = query.setColumnMappings(User.COLUMN_MAPPING).executeAndFetchFirst(User.class);
            return Optional.ofNullable(user);
        }
    }
}
