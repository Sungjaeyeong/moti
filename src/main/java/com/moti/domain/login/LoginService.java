package com.moti.domain.login;

import com.moti.domain.user.UserRepository;
import com.moti.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginService {

    private final UserRepository userRepository;

    public Optional<User> login(String email, String password) {
        return userRepository.findByEmailAndPW(email, password);
    }
}
