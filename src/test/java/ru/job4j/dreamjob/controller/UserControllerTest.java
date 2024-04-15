package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class UserControllerTest {
    private UserService userService;

    private UserController userController;

    @BeforeEach
    public void initServices() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    public void whenRegisterUserThenSaveUserAndRedirectToVacanciesPage() {
        var user = new User("email", "User", "password");
        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(userService.save(userArgumentCaptor.capture())).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.register(model, user);
        var actualUser = userArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualUser).isEqualTo(user);
    }

    @Test
    public void whenRegisterExistingUserThenReturnErrorMessageAndShowErrorPage() {
        var user = new User("email", "User", "password");
        when(userService.save(user)).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.register(model, user);
        var message = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(message).isEqualTo("Пользователь с такой почтой уже существует");
    }

    @Test
    public void whenLoginUserWithValidCredentialsThenSetUserInSessionAndRedirectToVacanciesPage() {
        var user = new User("email", "User", "password");
        when(userService.findByEmailAndPassword("email", "password")).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var request = mock(HttpServletRequest.class);
        var session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        var view = userController.loginUser(user, model, request);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(session).setAttribute(eq("user"), userCaptor.capture());

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(userCaptor.getValue()).isEqualTo(user);
    }

    @Test
    public void whenLoginUserWithInvalidCredentialsThenReturnErrorMessageAndShowLoginPage() {
        var user = new User("email", "User", "password");
        when(userService.findByEmailAndPassword("email", "incorrectpassword")).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.loginUser(user, model, mock(HttpServletRequest.class));
        var error = model.getAttribute("error");

        assertThat(view).isEqualTo("users/login");
        assertThat(error).isEqualTo("Почта или пароль введены неверно");
    }

    @Test
    public void whenLogoutUserThenInvalidateSessionAndRedirectToLoginPage() {
        var session = mock(HttpSession.class);
        when(session.getId()).thenReturn("session-id");

        var view = userController.logout(session);

        verify(session).invalidate();
        assertThat(view).isEqualTo("redirect:/users/login");
    }
}