package com.moti.web.user;

import com.moti.domain.user.UserService;
import com.moti.domain.user.entity.User;
import com.moti.web.user.dto.AddUserDto;
import com.moti.web.user.dto.AddUserResponseDto;
import com.moti.web.user.dto.getUserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController()
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/add")
    public AddUserResponseDto join(@RequestBody @Valid AddUserDto addUserDto) {

        User user = User.builder()
                .email(addUserDto.getEmail())
                .password(addUserDto.getPw())
                .name(addUserDto.getName())
                .job(addUserDto.getJob())
                .introduce(addUserDto.getIntroduce())
                .build();

        Long userId = userService.join(user);

        return new AddUserResponseDto(userId);

    }

    // 유저정보 조회
    @GetMapping("/{userId}")
    public getUserResponseDto get(@PathVariable Long userId) {
        User findUser = userService.findOne(userId);
        return getUserResponseDto.builder()
                .id(findUser.getId())
                .email(findUser.getEmail())
                .name(findUser.getName())
                .job(findUser.getJob())
                .introduce(findUser.getIntroduce())
                .build();
    }

    // 유저정보 수정
}
