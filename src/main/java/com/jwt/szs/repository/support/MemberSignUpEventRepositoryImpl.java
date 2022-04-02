package com.jwt.szs.repository.support;

import com.jwt.szs.model.base.HasUserIdPassword;
import com.jwt.szs.model.entity.MemberScrapEvent;
import com.jwt.szs.model.entity.MemberSignUpEvent;
import com.jwt.szs.model.entity.QMemberSignUpEvent;
import com.jwt.szs.repository.support.custom.MemberSignUpEventRepositoryCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Repository
public class MemberSignUpEventRepositoryImpl extends QuerydslRepositorySupportBasic implements MemberSignUpEventRepositoryCustom {

    private QMemberSignUpEvent qMemberSignUpEvent = QMemberSignUpEvent.memberSignUpEvent;

    public MemberSignUpEventRepositoryImpl() {
        super(MemberScrapEvent.class);
    }

    @Override
    public Optional<MemberSignUpEvent> findByUserIdAndPasswordInSec(String userId, Integer createdSeconds) {

        LocalDateTime now = LocalDateTime.now().minusSeconds(createdSeconds);

        MemberSignUpEvent memberSignUpEvent = getQueryFactory()
                .selectFrom(qMemberSignUpEvent)
                .where(
                        qMemberSignUpEvent.userId.eq(userId),
                        qMemberSignUpEvent.createdAt.after(now)
                ).orderBy(qMemberSignUpEvent.createdAt.desc())
                .fetchFirst();

        return Optional.ofNullable(memberSignUpEvent);
    }

}
