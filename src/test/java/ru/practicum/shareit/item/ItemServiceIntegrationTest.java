package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.utils.TestUtils;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class ItemServiceIntegrationTest {
    ItemService itemService;
    UserService userService;

    @Test
    void shouldCreateItemsAndGetByUserId() {
        CreateItemDto createItemDto = TestUtils.makeCreateItemDto(true, null);

        User user1 = userService.create(new CreateUserDto("test name", "test1@test.test"));
        User user2 = userService.create(new CreateUserDto("test name", "test2@test.test"));

        itemService.create(user1.getId(), createItemDto);
        itemService.create(user1.getId(), createItemDto);
        itemService.create(user1.getId(), createItemDto);

        itemService.create(user2.getId(), createItemDto);

        List<ItemDto> items = itemService.getByUserId(user1.getId(), null);

        assertThat(items).hasSize(3);
    }
}
