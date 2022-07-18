package com.moti.domain.user;

import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired UserRepository userRepository;

    @Test
    public void tdd() throws Exception {
        // given
        User user = User.builder()
                .email("wodud@afd.com")
                .password("abcdef")
                .name("wodud")
                .job(Job.DEV)
                .build();

        // when
        Long saveId = userRepository.save(user);
        User findUser = userRepository.findOne(saveId);
        Optional<User> byEmailUser = userRepository.findByEmail("aaa@bbb.com");
        User nullUser = userRepository.findOne(2L);

        // then
        Assertions.assertThat(findUser.getId()).isEqualTo(user.getId());
        Assertions.assertThat(byEmailUser).isEqualTo(Optional.empty());
        Assertions.assertThat(nullUser).isEqualTo(null);
    }

}