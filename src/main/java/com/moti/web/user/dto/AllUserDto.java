package com.moti.web.user.dto;

import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class AllUserDto {

  private final Long id;
  private final String email;
  private final String userName;
  private final Job job;

  @Builder
  public AllUserDto(User user) {
    this.id = user.getId();
    this.email = user.getEmail();
    this.userName = user.getName();
    this.job = user.getJob();
  }
}
