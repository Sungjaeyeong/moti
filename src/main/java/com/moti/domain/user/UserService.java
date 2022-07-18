package com.moti.domain.user;

import com.moti.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    // 회원가입
    @Transactional
    public Long join(User user) {
        validateDuplicateUser(user);
        userRepository.save(user);
        return user.getId();
    }

    // 이메일 중복확인 (동시성 문제 발생할 수 있음.)
    private void validateDuplicateUser(User user) {
        Optional<User> userOptional = userRepository.findByEmail(user.getEmail());
        if (!userOptional.isEmpty()) {
            throw new IllegalStateException("이미 사용중인 이메일입니다.");
        }
    }

    // 유저 정보조회
    public User findOne(Long userId) {
        User findUser = userRepository.findOne(userId);
        if (findUser == null) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }
        return findUser;
    }


    // 유저 정보수정
//    public void edit(Long userId, EditUserDto editUserDto) {
//        User findUser = userRepository.findOne(userId);
//        if (findUser == null) {
//            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
//        }
//    }

}
