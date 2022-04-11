package com.jwt.szs.service;

import com.jwt.szs.exception.AlreadyExpiredRefreshTokenException;
import com.jwt.szs.exception.CustomRuntimeException;
import com.jwt.szs.exception.ErrorCode;
import com.jwt.szs.model.dto.jwt.ReIssuanceTokenDto;
import com.jwt.szs.model.dto.member.MemberResponse;
import com.jwt.szs.repository.redis.RedisRepository;
import com.jwt.szs.service.member.MemberService;
import com.jwt.szs.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final MemberService memberService;

    private final RedisRepository redisRepository;

    public String ReIssuanceAccessToken(ReIssuanceTokenDto reIssuanceTokenDto) {

        checkWithAccessToken(reIssuanceTokenDto);

        log.info("액세스 토큰을 재발급합니다.");

        String userId = JwtTokenUtils.getUserId(reIssuanceTokenDto.getRefreshToken());
        MemberResponse memberResponse = memberService.getByUserId(userId);

        return JwtTokenUtils.generateAccessToken(memberResponse.getUserId());
    }

    public Optional<String> getUserIdByRefreshToken(String key) {

        return redisRepository.getData(key);
    }

    public void createRefreshToken(String key, Object value, long duration) {

        redisRepository.setDataContainsExpireDate(key, value, duration);
    }

    public void deleteToken(String key) {

        redisRepository.deleteData(key);
    }

    public void checkWithAccessToken(ReIssuanceTokenDto reIssuanceTokenDto) {

        log.info("리프레시 토큰의 만료기한과 redis에 저장된 토큰값을 확인합니다.");

        String userIdByRedis = getUserIdByRefreshToken(reIssuanceTokenDto.getRefreshToken())
                .orElseThrow(() -> new AlreadyExpiredRefreshTokenException());

        if(JwtTokenUtils.isTokenExpired(reIssuanceTokenDto.getRefreshToken())){
            throw new AlreadyExpiredRefreshTokenException();
        }

        String userId = JwtTokenUtils.getUserId(reIssuanceTokenDto.getRefreshToken());

        if (!userIdByRedis.equals(userId)) {
            throw new CustomRuntimeException(ErrorCode.NOT_MATCHED_VALUE);
        }

        log.info("액세스 토큰이 만료되었는지 검사합니다.");

        if (!JwtTokenUtils.isTokenExpired(reIssuanceTokenDto.getAccessToken())) {

            log.info("액세스 토큰이 만료되지 않았지만 재발급요청을 시도하였습니다. 리프레시 토큰을 폐기합니다.");

            deleteToken(reIssuanceTokenDto.getRefreshToken());

            throw new CustomRuntimeException(ErrorCode.NOT_YET_EXPIRED_TOKEN);
        }
    }
}
