package com.jwt.szs.service.member;

import com.jwt.szs.model.dto.member.MemberCreationRequest;
import com.jwt.szs.model.entity.MemberSignUpEvent;
import com.jwt.szs.repository.MemberSignUpEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 유저 스크랩 저장 요청 상태를 관리합니다.
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberSignUpEventService {

    private final MemberSignUpEventRepository signUpEventRepository;

    public void createRequestEvent(String userId, String password) {

        log.info("화원가입 요청 이력을 생성합니다.");

        MemberSignUpEvent memberSignUpEvent = new MemberSignUpEvent(
                userId,
                password);

        signUpEventRepository.save(memberSignUpEvent);
    }


    public void requestFailed(String userId, String password) {

        //MemberSignUpEvent memberSignUpEvent = signUpEventRepository.findByUserIdAndPasswordLately(userId, password);



    }
}
