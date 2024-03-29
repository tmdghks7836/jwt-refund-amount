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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Value("${szs.find-sign-up-event-seconds}")
    private Integer findSignUpEventSeconds;

    private final PasswordEncoder passwordEncoder;

    @Value("${api.test-3o3.timeout}")
    private Integer test3o3ApiTimeout;

    @Transactional
    public void createRequestEvent(HasUserIdPassword userIdPassword) {

        log.info("화원가입 요청 이력을 생성합니다.");
        String encodePassword = passwordEncoder.encode(userIdPassword.getPassword());

        MemberSignUpEvent memberSignUpEvent = new MemberSignUpEvent(
                userIdPassword.getUserId(),
                encodePassword,
                MemberSignUpStatus.PENDING);

        signUpEventRepository.save(memberSignUpEvent);
    }

    @Transactional
    public void requestComplete(HasUserIdPassword userIdPassword) {

        log.info("화원가입 요청 이력을 생성합니다.");
        String encodePassword = passwordEncoder.encode(userIdPassword.getPassword());

        MemberSignUpEvent memberSignUpEvent = new MemberSignUpEvent(
                userIdPassword.getUserId(),
                encodePassword,
                MemberSignUpStatus.COMPLETED);

        signUpEventRepository.saveAndFlush(memberSignUpEvent);
    }

    @Transactional
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

    /**
     * 로그인 진행 시 아직 회원가입 완료, 요청중, 실패되었는지 일정시간동안 확인합니다.
     */
    public void validateBeforeLogin(HasUserIdPassword useridPassword) {

        log.info("{}초 내에 생성된 회원의 가입요청 상태정보를 찾습니다..", findSignUpEventSeconds);

        /**
         * 유저아이디와 패스워드로 최근 회원가입이 완료되었는지 확인합니다.
         * */
        if (checkCompletedRequest(useridPassword)) {
            return;
        }

        /**
         * 최근에 해당 아이디로 회원가입 진행중인 이벤트를 찾습니다.
         * */
        if (didSomeOneRequestPending(useridPassword.getUserId())) {
            throw new CustomRuntimeException(ErrorCode.REQUEST_PENDING);
        }

        /**
         * 회원가입 완료, 요청중이 아니라면, 비밀번호 검증 실패 또는 요청 실패입니다.
         * */
        if (checkFailedRequest(useridPassword)) {
            throw new CustomRuntimeException(ErrorCode.REQUEST_FAILED,
                    "회원가입에 실패한 정보입니다. 다시 회원가입을 진행해주세요.");
        }
    }

    /**
     * SoS에서 응답을 받는 시간동안 해당 아이디는 회원가입 요청중 상태로 lock을 잡아놓습니다.
     */
    public boolean didSomeOneRequestPending(String userId) {

        return signUpEventRepository.findByUserIdAfterSeconds(
                userId,
                MemberSignUpStatus.PENDING,
                test3o3ApiTimeout).isPresent();
    }

    public boolean checkCompletedRequest(HasUserIdPassword useridPassword) {

        Optional<MemberSignUpEvent> signUpEventOptional = signUpEventRepository.findByUserIdAfterSeconds(
                useridPassword.getUserId(),
                MemberSignUpStatus.COMPLETED,
                findSignUpEventSeconds);

        if (!signUpEventOptional.isPresent()) {
            return false;
        }

        MemberSignUpEvent memberSignUpEvent = signUpEventOptional.get();

        /**
         * 같은아이디로 회원가입 요청한 다른 유저가 아닌
         * 해당 유저가 회원가입이 성공했는지 비밀번호 검증을 통해 확인합니다.
         * */
        if (passwordEncoder.matches(useridPassword.getPassword(), memberSignUpEvent.getPassword())) {
            return true;
        }

        return false;
    }

    public boolean checkFailedRequest(HasUserIdPassword hasUserIdPassword) {

        /**
         * 해당아이디의 회원가입 실패기록이 있는지 확인합니다.
         * */
        Optional<MemberSignUpEvent> signUpEventOptional = signUpEventRepository.findByUserIdAfterSeconds(
                hasUserIdPassword.getUserId(),
                MemberSignUpStatus.FAILED,
                findSignUpEventSeconds
        );

        if(!signUpEventOptional.isPresent()){
            return false;
        }

        MemberSignUpEvent memberSignUpEvent = signUpEventOptional.get();

        if (!passwordEncoder.matches(hasUserIdPassword.getPassword(), memberSignUpEvent.getPassword())) {
            return false;
        }

        log.info("멤버의 패스워드가 같으므로 해당 멤버의 계정은 실패처리된 계정입니다.");

        return true;
    }
}
