package com.moti.web.team;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moti.domain.team.TeamRepository;
import com.moti.domain.team.TeamService;
import com.moti.domain.team.TeamStatus;
import com.moti.domain.team.entity.Team;
import com.moti.domain.team.entity.TeamUser;
import com.moti.domain.user.entity.Job;
import com.moti.domain.user.entity.User;
import com.moti.web.SessionConst;
import com.moti.web.team.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.moti.com", uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
class TeamControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired EntityManager em;
    @Autowired TeamRepository teamRepository;
    @Autowired TeamService teamService;

    User user;
    MockHttpSession session;
    Team team;

    @BeforeEach
    void init() {
        teamRepository.deleteAll();

        user = User.builder()
                .email("wodud@afd.com")
                .password("abcdef")
                .name("wodud")
                .job(Job.PM)
                .build();

        em.persist(user);

        TeamUser teamUser = TeamUser.createTeamUser(user);
        team = Team.createTeam("team", user);

        em.persist(team);

        session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_USER, user.getId());
    }

    @Nested
    @DisplayName("팀 생성")
    class Create {
        @Test
        @DisplayName("팀 생성")
        public void createTeam() throws Exception {
            // given
            CreateTeamDto createTeamDto = CreateTeamDto.builder()
                    .userId(user.getId())
                    .teamName("새로운 팀")
                    .build();

            String json = objectMapper.writeValueAsString(createTeamDto);

            // when
            mockMvc.perform(post("/teams")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
                            .session(session)
                    )
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andDo(document("create-team",
                            requestFields(
                                    fieldWithPath("userId").description("유저 ID").attributes(Attributes.key("required").value("O")),
                                    fieldWithPath("teamName").description("팀이름").attributes(Attributes.key("required").value("O"))
                            ),
                            responseFields(
                                    fieldWithPath("id").description("팀 ID")
                            )
                    ));

            // then
            Long count = teamRepository.count();
            assertThat(count).isEqualTo(2L);

        }

        @Test
        @DisplayName("팀 생성 실패")
        public void createTeam_failed() throws Exception {
            // given
            CreateTeamDto createTeamDto = CreateTeamDto.builder()
                    .userId(user.getId())
                    .teamName("새로운 팀 이름 10글자 넘어서 실패")
                    .build();

            String json = objectMapper.writeValueAsString(createTeamDto);

            // when
            mockMvc.perform(post("/teams")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
                            .session(session)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validation.teamName").value("0 ~ 10자 사이로 입력해주세요."))
                    .andDo(print());

            // then
            Long count = teamRepository.count();
            assertThat(count).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("팀 가입")
    class JoinTeam {
        @Test
        @DisplayName("팀 가입 성공")
        public void joinTeam() throws Exception {
            // given
            User newUser = User.builder()
                    .email("jaeyeong@afd.com")
                    .password("abcdef")
                    .name("jaeyeong")
                    .job(Job.DEV)
                    .build();

            em.persist(newUser);

            JoinTeamDto joinTeamDto = JoinTeamDto.builder()
                    .userId(newUser.getId())
                    .build();

            String json = objectMapper.writeValueAsString(joinTeamDto);

            // when
            mockMvc.perform(RestDocumentationRequestBuilders.post("/teams/{teamId}", team.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
                            .session(session)
                    )
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andDo(document("join-team",
                            pathParameters(parameterWithName("teamId").description("팀 ID").attributes(Attributes.key("required").value("O"))),
                            requestFields(
                                    fieldWithPath("userId").description("유저 ID").attributes(Attributes.key("required").value("O"))
                            )
                    ));

            // then
            assertThat(team.getTeamUsers().size()).isEqualTo(2);
        }

        @Test
        @DisplayName("팀 가입 실패")
        public void joinTeam_failed() throws Exception {
            // given
            User newUser = User.builder()
                    .email("jaeyeong@afd.com")
                    .password("abcdef")
                    .name("jaeyeong")
                    .job(Job.DEV)
                    .build();

            // when
            mockMvc.perform(post("/teams/{teamId}", team.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}")
                            .session(session)
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print());

            // then
            assertThat(team.getTeamUsers().size()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("팀 조회")
    class FindTeam {
        @Test
        @DisplayName("팀 전부 조회")
        public void findAll() throws Exception {
            // given
            User newUser1 = User.builder()
                    .email("jaeyeong1@moti.com")
                    .password("abcdef")
                    .name("jaeyeong1")
                    .job(Job.PM)
                    .build();

            User newUser2 = User.builder()
                    .email("jaeyeong2@moti.com")
                    .password("abcdef")
                    .name("jaeyeong2")
                    .job(Job.PM)
                    .build();

            em.persist(newUser1);
            em.persist(newUser2);

            TeamUser teamUser1 = TeamUser.createTeamUser(newUser1);
            TeamUser teamUser2 = TeamUser.createTeamUser(newUser2);

            Team team1 = Team.createTeam("team1", newUser1);
            Team team2 = Team.createTeam("team2", newUser1);
            Team team3 = Team.createTeam("team3", newUser1);
            Team team4 = Team.createTeam("team4", newUser1);
            Team team5 = Team.createTeam("team5", newUser2);
            Team team6 = Team.createTeam("team6", newUser2);
            Team team7 = Team.createTeam("team7", newUser2);

            em.persist(team1);
            em.persist(team2);
            em.persist(team3);
            em.persist(team4);
            em.persist(team5);
            em.persist(team6);
            em.persist(team7);

            // when
            MvcResult mvcResult = mockMvc.perform(get("/teams")
                            .param("page", String.valueOf(2))
                            .param("limit", String.valueOf(4))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.count").value(4))
                    .andDo(print())
                    .andDo(document("get-teams",
                            requestParameters(
                                    parameterWithName("page").description("불러올 페이지"),
                                    parameterWithName("limit").description("한 페이지의 최대 데이터 개수")
                            ),
                            responseFields(
                                    fieldWithPath("responseTeamDtoList[]").description("팀 리스트"),
                                    fieldWithPath("count").description("총 팀 수")
                            ).andWithPrefix("responseTeamDtoList[].", new FieldDescriptor[] {
                                    fieldWithPath("teamId").description("팀 ID"),
                                    fieldWithPath("teamName").description("팀 이름"),
                                    fieldWithPath("teamStatus").description("팀 상태"),
                                    fieldWithPath("teamUsers[]").description("유저 리스트"),
                            }).andWithPrefix("responseTeamDtoList[].teamUsers[].", new FieldDescriptor[] {
                                    fieldWithPath("id").description("유저 ID"),
                                    fieldWithPath("email").description("이메일"),
                                    fieldWithPath("name").description("유저 이름"),
                                    fieldWithPath("job").description("유저 직업"),
                                    fieldWithPath("introduce").description("유저 소개"),
                            })
                    ))
                    .andReturn();

            // then
            String contentAsString = mvcResult.getResponse().getContentAsString();
            ResponseTeamsDto response = objectMapper.readValue(contentAsString, ResponseTeamsDto.class);
            assertThat(response.getResponseTeamDtoList().get(0).getTeamName()).isEqualTo("team4");

        }

        @Test
        @DisplayName("팀으로 팀조회")
        public void findByTeam() throws Exception {
            // given
            User newUser1 = User.builder()
                    .email("jaeyeong1@moti.com")
                    .password("abcdef")
                    .name("jaeyeong1")
                    .job(Job.PM)
                    .build();

            User newUser2 = User.builder()
                    .email("jaeyeong2@moti.com")
                    .password("abcdef")
                    .name("jaeyeong2")
                    .job(Job.PM)
                    .build();

            em.persist(newUser1);
            em.persist(newUser2);

            teamService.joinTeam(newUser1.getId(), team.getId());
            teamService.joinTeam(newUser2.getId(), team.getId());

            // when
            MvcResult mvcResult = mockMvc.perform(RestDocumentationRequestBuilders.get("/teams/{teamId}", team.getId())
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.teamName").value("team"))
                    .andDo(print())
                    .andDo(document("get-team",
                            pathParameters(parameterWithName("teamId").description("팀 ID").attributes(Attributes.key("required").value("O"))),
                            responseFields(
                                    fieldWithPath("teamId").description("팀 ID"),
                                    fieldWithPath("teamName").description("팀 이름"),
                                    fieldWithPath("teamStatus").description("팀 상태"),
                                    fieldWithPath("teamUsers[]").description("팀에 속한 유저 리스트")
                            ).andWithPrefix("teamUsers[].", new FieldDescriptor[] {
                                    fieldWithPath("id").description("유저 ID"),
                                    fieldWithPath("email").description("이메일"),
                                    fieldWithPath("name").description("유저 이름"),
                                    fieldWithPath("job").description("유저 직업"),
                                    fieldWithPath("introduce").description("유저 소개"),
                            })
                    ))
                    .andReturn();

            // then
            String contentAsString = mvcResult.getResponse().getContentAsString();
            ResponseTeamDto response = objectMapper.readValue(contentAsString, ResponseTeamDto.class);
            assertThat(response.getTeamUsers().size()).isEqualTo(3);

        }
    }

    @Test
    @DisplayName("팀 상태변경")
    public void changeTeam() throws Exception {
        // given
        ChangeTeamStatusDto changeTeamStatusDto = ChangeTeamStatusDto.builder()
                .teamStatus(TeamStatus.COMP)
                .build();

        String json = objectMapper.writeValueAsString(changeTeamStatusDto);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/teams/{teamId}", team.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .session(session)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("update-team",
                        pathParameters(parameterWithName("teamId").description("팀 ID").attributes(Attributes.key("required").value("O"))),
                        requestFields(
                                fieldWithPath("teamStatus").description("팀 상태").attributes(Attributes.key("required").value("O"))
                        )
                ));

        // then
        assertThat(team.getStatus()).isEqualTo(TeamStatus.COMP);
    }

    @Test
    @DisplayName("팀 탈퇴")
    public void leaveTeam() throws Exception {
        // when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/teams/{teamId}/users/{userId}", team.getId(), user.getId())
                        .session(session)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("exit-team",
                        pathParameters(
                                parameterWithName("teamId").description("팀 ID").attributes(Attributes.key("required").value("O")),
                                parameterWithName("userId").description("유저 ID").attributes(Attributes.key("required").value("O"))
                        )
                ));

        // then
        Long count = teamRepository.count();
        assertThat(count).isEqualTo(0L);

        Long count2 = em.createQuery("select count(tu) from TeamUser tu", Long.class)
                .getSingleResult();
        assertThat(count2).isEqualTo(0L);
    }

}