package com.moti.domain.user;

import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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

        // then
        Assertions.assertThat(findUser.getId()).isEqualTo(user.getId());
    }

}