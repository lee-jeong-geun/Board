package org.board.springboot.user;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.board.springboot.user.controller.UserApiController;
import org.board.springboot.user.dto.UserSaveRequestDto;
import org.board.springboot.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@WebMvcTest(UserApiController.class)
public class UserApiControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void 유저저장_호출_성공() throws Exception {
        //given
        String name = "jk";
        String email = "jk@jk.com";
        String password = "jkjk";
        UserSaveRequestDto userSaveRequestDto = UserSaveRequestDto.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();

        String url = "http://localhost:8080/api/v1/users";
        given(userService.save(any())).willReturn(1l);

        //when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(userSaveRequestDto)));

        //then
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("1"));
    }

}
