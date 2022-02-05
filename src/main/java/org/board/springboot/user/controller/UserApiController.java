package org.board.springboot.user.controller;

import lombok.RequiredArgsConstructor;
import org.board.springboot.user.service.UserService;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;

}
