package org.board.springboot.posts;

import org.board.springboot.posts.domain.Posts;
import org.board.springboot.posts.dto.PostsSaveRequestDto;
import org.board.springboot.user.domain.User;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PostsDtoTest {

    @Test
    public void PostsSaveRequest_toEntity_성공() {
        //given
        String title = "title";
        String content = "content";
        String name = "jk";
        User user = User.builder()
                .name(name)
                .build();
        PostsSaveRequestDto postsSaveRequestDto = PostsSaveRequestDto.builder()
                .title(title)
                .content(content)
                .user(user)
                .build();

        //when
        Posts posts = postsSaveRequestDto.toEntity();

        //then
        assertThat(posts.getTitle()).isEqualTo(title);
        assertThat(posts.getContent()).isEqualTo(content);
        assertThat(posts.getUser().getName()).isEqualTo(name);

    }

}
