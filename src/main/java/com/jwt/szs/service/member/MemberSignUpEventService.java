package com.jwt.szs.service.member;

import com.jwt.szs.api.codetest3o3.model.type.MemberSignUpStatus;
import com.jwt.szs.exception.CustomRuntimeException;
import com.jwt.szs.exception.ErrorCode;
import com.jwt.szs.model.base.HasUserIdPassword;
import com.jwt.szs.model.entity.MemberSignUpEvent;
import com.jwt.szs.repository.MemberSignUpEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 유저 회원가입 요청 상태를 관리합니다.
 * 가입시 이메일 정보가 포함된다면 유저 회원가입 이벤트 처리를 메일 발송기능으로 대체하였을 것 같습니다.
 * 해당 서비스는 가입 요청 후 로그인 시 10분 내에생성된 userId, password와 매칭된 요청이력을 찾고
 * 상태를 응답합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberSignUpEventService {

    private final MemberSignUpEventRepository signUpEventRepository;

    private final Integer secWhichFindMember = 1000 * 60 * 5;

    private final PasswordEncoder passwordEncoder;

    @Value("${api.test-3o3.timeout}")
    private Integer test3o3ApiTimeout;

    public void createRequestEvent(HasUserIdPassword userIdPassword) {

        log.info("화원가입 요청 이력을 생성합니다.");
        String encodePassword = passwordEncoder.encode(userIdPassword.getPassword());

        MemberSignUpEvent memberSignUpEvent = new MemberSignUpEvent(
                userIdPassword.getUserId(),
                encodePassword,
                MemberSignUpStatus.PENDING);

        signUpEventRepository.save(memberSignUpEvent);
    }

    public void requestComplete(HasUserIdPassword userIdPassword) {

        log.info("화원가입 요청 이력을 생성합니다.");
        String encodePassword = passwordEncoder.encode(userIdPassword.getPassword());

        MemberSignUpEvent memberSignUpEvent = new MemberSignUpEvent(
                userIdPassword.getUserId(),
                encodePassword,
                MemberSignUpStatus.COMPLETED);

        signUpEventRepository.save(memberSignUpEvent);
    }

    public void requestFailed(HasUserIdPassword userIdPassword, String message) {

        log.info("화원가입 요청 실패 이력을 생성합니다.");
        String encodePassword = passwordEncoder.encode(userIdPassword.getPassword());

        MemberSignUpEvent memberSignUpEvent = new MemberSignUpEvent(
                userIdPassword.getUserId(),
                encodePassword,
                MemberSignUpStatus.FAILED,
                message);

        signUpEventRepository.save(memberSignUpEvent);
    }

    public void validateHistoryInSeconds(HasUserIdPassword useridPassword) {

        log.info("{}분 내에 생성된 회원의 가입요청 상태정보를 찾습니다..", secWhichFindMember);

        Optional<MemberSignUpEvent> signUpEventOptional = signUpEventRepository.findByUserIdAndPasswordInSec(
                useridPassword.getUserId(),
                secWhichFindMember
        );

        if (!signUpEventOptional.isPresent() ) {
            return;
        }

        MemberSignUpEvent memberSignUpEvent = signUpEventOptional.get();

        if(!passwordEncoder.matches(useridPassword.getPassword(), memberSignUpEvent.getPassword())){
            return;
        }

        if (memberSignUpEvent.isPending()) {
            throw new CustomRuntimeException(ErrorCode.REQUEST_PENDING, memberSignUpEvent.getMessage());
        }

        if (memberSignUpEvent.isFailed()) {
            throw new CustomRuntimeException(ErrorCode.REQUEST_FAILED, "회원가입에 실패한 정보입니다. 다시 회원가입을 진행해주세요.");
        }
    }

    public boolean isSomeOneRequestPending(HasUserIdPassword useridPassword) {

        Optional<MemberSignUpEvent> signUpEventOptional = signUpEventRepository.findByUserIdAndPasswordInSec(
                useridPassword.getUserId(),
                secWhichFindMember);

        if(!signUpEventOptional.isPresent()){
            return false;
        }

        return signUpEventOptional.get().isPending();
    }
}
