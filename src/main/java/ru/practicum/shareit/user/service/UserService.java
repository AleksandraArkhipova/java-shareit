package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.core.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.core.exception.DuplicatedEmailException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;
import ru.practicum.shareit.user.dto.CreateUserDto;

import java.util.List;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserJpaRepository repo;
    UserMapper mapper;

    public List<User> findAll() {
        return repo.findAll();
    }

    public User getById(long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("user", id));
    }

    public User create(CreateUserDto dto) {
        User user = mapper.toUser(dto);

        return repo.save(user);
    }

    public User update(long id, UpdateUserDto dto) {
        User user = repo.findById(id).orElseThrow(() -> new NotFoundException("user", id));

        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            checkIfEmailHasDuplicates(dto.getEmail());
            user.setEmail(dto.getEmail());
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }

        return repo.save(user);
    }

    public User delete(long id) {
        User user = repo.findById(id).orElseThrow(() -> new NotFoundException("user", id));
        repo.deleteById(id);
        return user;
    }

    private void checkIfEmailHasDuplicates(String email) {
        if (repo.findByEmail(email).isPresent()) {
            throw new DuplicatedEmailException(email);
        }
    }
}
