package com.moti.domain.login;

import com.moti.domain.user.UserRepository;
import com.moti.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;

    public User login(String email, String password) {
        List<User> userList = userRepository.findByEmail(email);
        if (userList.get(0).getPassword().equals(password)) {
            return userList.get(0);
        } else {
            return null;
        }
    }
}
