package org.board.springboot.web;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(IndexController.class)
public class IndexControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void 메인화면_로딩_성공() throws Exception {
        //given
        String url = "http://localhost:8080/";

        //when
        ResultActions resultActions = mockMvc.perform(get(url));

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("게시판")));
    }

    @Test
    void 회원가입화면_로딩_성공() throws Exception {
        //given
        String url = "http://localhost:8080/auth/register";

        //when
        ResultActions resultActions = mockMvc.perform(get(url));

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("회원가입")));
    }
}
