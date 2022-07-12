package com.moti.web.user;

import com.moti.domain.user.UserService;
import com.moti.domain.user.entity.User;
import com.moti.web.user.dto.AddUserDto;
import com.moti.web.user.dto.AddUserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController()
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/add")
    public AddUserResponseDto join(@RequestBody @Valid AddUserDto addUserDto) {

        User user = new User(addUserDto.getEmail(), addUserDto.getPw(), addUserDto.getName(), addUserDto.getJob());
        Long userId = userService.join(user);

        return new AddUserResponseDto(userId);

    }
}
