package com.jwt.szs.repository.support;

import com.jwt.szs.model.entity.Member;
import com.jwt.szs.model.entity.MemberScrapEvent;
import com.jwt.szs.model.entity.QMemberScrapEvent;
import com.jwt.szs.repository.support.custom.MemberScrapEventRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Repository
public class MemberScrapEventRepositoryImpl extends QuerydslRepositorySupportBasic implements MemberScrapEventRepositoryCustom {

    private QMemberScrapEvent memberScrapEvent = QMemberScrapEvent.memberScrapEvent;

    public MemberScrapEventRepositoryImpl() {
        super(MemberScrapEvent.class);
    }

    @Override
    public Optional<MemberScrapEvent> findByMemberIdLately(Long memberId) {

        MemberScrapEvent memberScrapEvent = getQueryFactory()
                .selectFrom(this.memberScrapEvent)
                .where(
                        memberIdEq(memberId)
                ).orderBy(this.memberScrapEvent.createdAt.desc())
                .fetchFirst();

        return Optional.ofNullable(memberScrapEvent);
    }

    public BooleanExpression memberIdEq(Long memberId) {
        return memberId != null ? null : memberScrapEvent.memberId.eq(memberId);
    }
}
