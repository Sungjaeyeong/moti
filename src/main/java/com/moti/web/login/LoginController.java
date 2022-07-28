package com.moti.web.login;

import com.moti.domain.login.LoginService;
import com.moti.domain.user.entity.User;
import com.moti.web.SessionConst;
import com.moti.web.exception.NotMatchUserException;
import com.moti.web.login.dto.LoginRequestDto;
import com.moti.web.login.dto.LoginResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody @Validated LoginRequestDto loginRequestDto, HttpServletRequest request, HttpServletResponse response) {

        log.info("loginRequestDto: {}", loginRequestDto);

        User loginUser = loginService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());

        if (loginUser == null) {
            throw new NotMatchUserException();
        }

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_USER, loginUser.getId());
        response.addCookie(new Cookie(SessionConst.LOGIN_USER, String.valueOf(loginUser.getId())));
        return new LoginResponseDto(loginUser);
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return "logged out";
    }

}
