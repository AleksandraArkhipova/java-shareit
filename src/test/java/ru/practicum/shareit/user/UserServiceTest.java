package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.core.exception.DuplicatedEmailException;
import ru.practicum.shareit.core.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.TestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserJpaRepository repo;

    @InjectMocks
    private UserService service;

    @Test
    void getById_shouldThrowNotFoundException() {
        when(repo.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getById(1)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getById_shouldReturnUser() {
        long id = 1;
        User user = TestUtils.makeUser(id);
        when(repo.findById(id)).thenReturn(Optional.of(user));
        assertThat(service.getById(id)).isEqualTo(user);
    }

    @Test
    void update_shouldThrowNotFoundException() {
        when(repo.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getById(1)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_shouldUpdateEmail() {
        long id = 1;
        String newEmail = "newEmail@test.test";

        UpdateUserDto dto = new UpdateUserDto(null, newEmail);
        User user = TestUtils.makeUser(id);
        User newUser = TestUtils.makeUser(id);
        newUser.setEmail(newEmail);

        when(repo.findById(id)).thenReturn(Optional.of(user));
        when(repo.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        assertThat(service.update(id, dto)).isEqualTo(newUser);
    }

    @Test
    void update_shouldThrowDuplicateEmailException() {
        long id = 1;
        String newEmail = "newEmail@test.test";

        UpdateUserDto dto = new UpdateUserDto(null, newEmail);
        User user = TestUtils.makeUser(id);

        when(repo.findById(id)).thenReturn(Optional.of(user));
        when(repo.findByEmail(newEmail)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.update(id, dto)).isInstanceOf(DuplicatedEmailException.class);
    }

    @Test
    void update_shouldUpdateName() {
        long id = 1;
        String newName = "new name";

        UpdateUserDto dto = new UpdateUserDto(newName, null);
        User user = TestUtils.makeUser(id);
        User newUser = TestUtils.makeUser(id);
        newUser.setName(newName);

        when(repo.findById(id)).thenReturn(Optional.of(user));
        when(repo.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        assertThat(service.update(id, dto)).isEqualTo(newUser);
    }

    @Test
    void getAll_shouldReturnListOfUsers() {
        List<User> users = List.of(
                TestUtils.makeUser(1),
                TestUtils.makeUser(2),
                TestUtils.makeUser(3)
        );

        when(repo.findAll()).thenReturn(users);

        assertThat(service.findAll()).isEqualTo(users);
    }

    @Test
    void delete_shouldReturnDeletedUser() {
        long userId = 1;
        User user = TestUtils.makeUser(userId);

        when(repo.findById(userId)).thenReturn(Optional.of(user));

        assertThat(service.delete(userId)).isEqualTo(user);
    }
}
