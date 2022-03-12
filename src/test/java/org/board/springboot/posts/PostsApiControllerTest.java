package org.board.springboot.posts;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.board.springboot.posts.controller.PostsApiController;
import org.board.springboot.posts.dto.PostsFindResponseDto;
import org.board.springboot.posts.dto.PostsListResponseDto;
import org.board.springboot.posts.service.PostsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@WebMvcTest(PostsApiController.class)
public class PostsApiControllerTest {

    @MockBean
    private PostsService postsService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void 게시글_리스트_조회_성공() throws Exception {
        //given
        String title = "title";
        String content = "content";
        String userName = "jk";
        String url = "/api/v1/posts";
        boolean success = true;
        PostsFindResponseDto postsFindResponseDto = PostsFindResponseDto.builder()
                .title(title)
                .content(content)
                .userName(userName)
                .build();
        List<PostsFindResponseDto> list = new ArrayList<>();
        list.add(postsFindResponseDto);
        PostsListResponseDto postsListResponseDto = PostsListResponseDto.builder()
                .success(success)
                .postsList(list)
                .build();
        given(postsService.findAll()).willReturn(list);

        //when
        ResultActions resultActions = mockMvc.perform(get(url));


        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(new ObjectMapper().writeValueAsString(postsListResponseDto)));
    }

}
