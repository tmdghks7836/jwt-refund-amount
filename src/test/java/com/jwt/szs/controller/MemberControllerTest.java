package com.jwt.szs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.jwt.szs.exception.ErrorCode;
import com.jwt.szs.exception.ErrorResponse;
import com.jwt.szs.model.base.BaseMember;
import com.jwt.szs.model.dto.member.AuthenticationRequest;
import com.jwt.szs.model.dto.member.MemberCreationRequest;
import com.jwt.szs.model.dto.member.MemberResponse;
import com.jwt.szs.model.dto.member.UserDetailsImpl;
import com.jwt.szs.model.type.JwtTokenType;
import com.jwt.szs.repository.redis.RedisRepository;
import com.jwt.szs.service.member.MemberService;
import com.jwt.szs.utils.CookieUtil;
import com.jwt.szs.utils.JwtTokenUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private RedisRepository redisRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private MemberService memberService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    public void 회원가입() throws Exception {

        Gson gson = new Gson();
        String json = gson.toJson(
                MemberCreationRequest.builder()
                        .userId("tmdghks")
                        .name("홍길동")
                        .regNo("860824-1655068")
                        .password("123")
                        .build()
        );

        mockMvc.perform(post("/szs/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @Test
    public void 회원가입_실패_빈값체크() throws Exception {

        Gson gson = new Gson();
        String json = gson.toJson(
                MemberCreationRequest.builder()
                        .userId("")
                        .name("")
                        .regNo("")
                        .password("")
                        .build()
        );

        mockMvc.perform(post("/szs/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }

    @Test
    public void 회원가입_실패_주민번호_정규식() throws Exception {

        Gson gson = new Gson();

        String json = gson.toJson(
                MemberCreationRequest.builder()
                        .userId("tmdghks")
                        .name("홍길동")
                        .regNo("8608241655068")
                        .password("123")
                        .build()
        );

        mockMvc.perform(post("/szs/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }

    @Test
    public void 로그인() throws Exception {
        long id = 1l;
        String userId = "tmdghks";
        String password = "123";
        MemberResponse memberResponse = MemberResponse.builder().userId(userId).id(id).build();

        Mockito.when(memberService.loadUserByUsername(any()))
                .thenReturn(
                        new UserDetailsImpl(
                                userId,
                                passwordEncoder.encode(password)
                        ));

        String json = usernamePasswordToJson(userId, password);
        MvcResult mvcResult = mockMvc.perform(post("/szs/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        HashMap<String, String> hashMap = new ObjectMapper()
                .readValue(mvcResult.getResponse().getContentAsString(), HashMap.class);
        String token = hashMap.get("token");

        Assertions.assertTrue(!JwtTokenUtils.isTokenExpired(token));
    }

    @Test
    public void 내정보_보기() throws Exception {

        mockMvc.perform(get("/api/test/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", ""))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void 내정보_보기_인증실패() throws Exception {

        mockMvc.perform(get("/api/test/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", ""))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void 토큰검증_실패_토큰없이전송() throws Exception {

        mockMvc.perform(get("/api/test/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", ""))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void 토큰재발급() throws Exception {

        MemberResponse memberResponse = MemberResponse.builder().id(123l).userId("tmdghks").build();
        String token = generateExpiredAccessToken(memberResponse.getUserId());
        Cookie refreshTokenCookie = generateRefreshTokenCookie(memberResponse.getUserId());

        Mockito.when(redisRepository.getData(anyString()))
                .thenReturn(Optional.of(memberResponse.getUserId()));
        Mockito.when(memberService.getByUserId(any()))
                .thenReturn(memberResponse);

        MvcResult mvcResult = mockMvc.perform(get("/szs/token/re-issuance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .cookie(refreshTokenCookie))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        log.info("액세스 토큰재발급 response : {}", mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void 토큰재발급_실패_아직만료되지않은_액세스토큰() throws Exception {

        MemberResponse memberResponse = MemberResponse.builder().id(123l).userId("tmdghks").build();
        String token = generateAccessToken(memberResponse.getUserId());
        Cookie refreshTokenCookie = generateRefreshTokenCookie(memberResponse.getUserId());

        Mockito.when(redisRepository.getData(anyString()))
                .thenReturn(Optional.of(memberResponse.getUserId()));

        MvcResult mvcResult = mockMvc.perform(get("/szs/token/re-issuance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .cookie(refreshTokenCookie))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        ErrorCode errorCode = jsonToErrorCode(mvcResult.getResponse().getContentAsString());
        Assertions.assertEquals(ErrorCode.NOT_YET_EXPIRED_TOKEN, errorCode);
    }

    @Test
    public void 토큰재발급_실패_만료된_리프레시토큰() throws Exception {

        MemberResponse memberResponse = MemberResponse.builder().id(123l).userId("tmdghks").build();

        String token = generateAccessToken(memberResponse.getUserId());
        Cookie refreshTokenCookie = generateExpiredRefreshTokenCookie(memberResponse.getUserId());

        Mockito.when(redisRepository.getData(anyString()))
                .thenReturn(Optional.of(memberResponse.getUserId()));

        MvcResult mvcResult = mockMvc.perform(get("/szs/token/re-issuance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .cookie(refreshTokenCookie))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        ErrorCode errorCode = jsonToErrorCode(mvcResult.getResponse().getContentAsString());
        Assertions.assertEquals(ErrorCode.TOKEN_EXPIRED, errorCode);
    }

    private ErrorCode jsonToErrorCode(String json) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorResponse = objectMapper.readValue(json, ErrorResponse.class);
        return ErrorCode.findByCode(errorResponse.getCode());
    }

    private String usernamePasswordToJson(String userId, String password) {

        AuthenticationRequest request = AuthenticationRequest.builder()
                .userId(userId)
                .password(password)
                .build();

        Gson gson = new Gson();
        return gson.toJson(request);
    }

    private String generateAccessToken(String userId) {

        return JwtTokenUtils.generateToken(userId, JwtTokenType.ACCESS);
    }

    private Cookie generateRefreshTokenCookie(String userId) {

        final String token = JwtTokenUtils.generateToken(userId, JwtTokenType.REFRESH);
        return JwtTokenUtils.createRefreshTokenCookie(token);
    }

    private String generateExpiredAccessToken(String userId) {

        return JwtTokenUtils.generateToken(userId, JwtTokenType.ACCESS, -1l);
    }

    private Cookie generateExpiredRefreshTokenCookie(String userId) {

        final String token = JwtTokenUtils.generateToken(userId, JwtTokenType.REFRESH, -1);
        return CookieUtil.createCookie(
                JwtTokenType.REFRESH.getCookieName(),
                token,
                JwtTokenType.REFRESH.getValidationSeconds());
    }



    @Getter
    @Setter
    @Builder
    public static class SimpleMember implements BaseMember {

        private Long id;

        private String userId;

        private String name;
    }

}