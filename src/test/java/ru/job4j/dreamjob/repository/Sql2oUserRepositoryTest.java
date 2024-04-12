package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import java.util.Optional;
import java.util.Properties;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

class Sql2oUserRepositoryTest {
    private static Sql2oUserRepository sql2oUserRepository;
    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearUsers() {
       try (var connection = sql2o.open()) {
           var sql = "DELETE FROM users";
           connection.createQuery(sql).executeUpdate();
       }
    }

    @Test
    public void whenSaveThenGetSame() {
        User user = new User("email9", "name", "password");
       User savedUser = sql2oUserRepository.save(user).get();
        User exp = sql2oUserRepository.findByEmailAndPassword(user.getEmail(), user.getPassword()).get();
        assertThat(exp).usingRecursiveComparison().isEqualTo(savedUser);
    }

    @Test
    public void whenSaveThenException() {
        User user1 = new User("email10", "name1", "password1");
        User user2 = new User("email10", "name2", "password2");
        Optional<User> savedUser1 = sql2oUserRepository.save(user1);
        Optional<User> savedUser2 = sql2oUserRepository.save(user2);
        assertThat(savedUser1.equals(user1));
        assertFalse(savedUser2.isPresent());
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oUserRepository.findByEmailAndPassword("email", "pasword")).isEqualTo(Optional.empty());
    }

    @Test
    public void whenSaveAndSearchUser() {
        User user1 = new User("user1", "name1", "password1");
        User user2 = new User("user2", "name2", "password2");
        User user3 = new User("user3", "name3", "password3");
        Optional<User> savedUser1 = sql2oUserRepository.save(user1);
        Optional<User> savedUser2 = sql2oUserRepository.save(user2);
        Optional<User> savedUser3 = sql2oUserRepository.save(user3);
        Optional<User> search = sql2oUserRepository.findByEmailAndPassword("user2", "pasword2");
        assertThat(search.isPresent());
        assertThat(savedUser3.get().getEmail().equals("user3"));
    }

}