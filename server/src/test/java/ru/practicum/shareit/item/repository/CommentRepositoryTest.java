package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void findAllByItemIdTest() {
        User testUser = User.builder()
                .name("name")
                .email("email@email.ru")
                .build();

        userRepository.save(testUser);

        ItemRequest testRequest = itemRequestRepository.save(ItemRequest.builder()
                .description("Test request description")
                .requestor(testUser)
                .created(LocalDateTime.now())
                .build());

        Item testItem = Item.builder()
                .name("name of item")
                .description("description of item")
                .available(true)
                .owner(testUser)
                .request(testRequest)
                .build();

        itemRepository.save(testItem);

        Comment testComment1 = Comment.builder()
                .text("comment text")
                .item(testItem)
                .author(testUser)
                .created(LocalDateTime.now())
                .build();
        commentRepository.save(testComment1);

        Comment testComment2 = Comment.builder()
                .text("comment text 2")
                .item(testItem)
                .author(testUser)
                .created(LocalDateTime.now())
                .build();
        commentRepository.save(testComment2);

        List<Comment> comments = commentRepository.findAllByItemId(testItem.getId());

        assertThat(comments)
                .hasSize(2)
                .containsExactlyInAnyOrder(testComment1, testComment2);

    }


}