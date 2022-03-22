package org.board.springboot.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/auth/register")
    public String register() {
        return "register";
    }

    @GetMapping("/posts/create")
    public String postsCreate() {
        return "posts/create";
    }

    @GetMapping("/users/{email}")
    public String userInform(@PathVariable String email) {
        return "user/inform";
    }
}
