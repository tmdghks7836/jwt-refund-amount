package com.jwt.szs.repository.support;

import com.jwt.szs.api.codetest3o3.model.type.MemberSignUpStatus;
import com.jwt.szs.model.entity.MemberScrapEvent;
import com.jwt.szs.model.entity.MemberSignUpEvent;
import com.jwt.szs.model.entity.QMemberSignUpEvent;
import com.jwt.szs.repository.support.custom.MemberSignUpEventRepositoryCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.TimeZone;

@Slf4j
@Repository
public class MemberSignUpEventRepositoryImpl extends QuerydslRepositorySupportBasic implements MemberSignUpEventRepositoryCustom {

    private QMemberSignUpEvent qMemberSignUpEvent = QMemberSignUpEvent.memberSignUpEvent;

    public MemberSignUpEventRepositoryImpl() {
        super(MemberScrapEvent.class);
    }

    @Override
    public Optional<MemberSignUpEvent> findByUserIdAfterSeconds(String userId, MemberSignUpStatus status, Integer createdSeconds) {

        LocalDateTime now =  LocalDateTime.now(TimeZone.getDefault().toZoneId()).minusSeconds(createdSeconds);

        log.info(now.toString());
        MemberSignUpEvent memberSignUpEvent = getQueryFactory()
                .selectFrom(qMemberSignUpEvent)
                .where(
                        qMemberSignUpEvent.status.eq(status),
                        qMemberSignUpEvent.userId.eq(userId),
                        qMemberSignUpEvent.createdAt.after(now)
                ).orderBy(qMemberSignUpEvent.createdAt.desc())
                .fetchFirst();

        return Optional.ofNullable(memberSignUpEvent);
    }

}
