package com.jwt.szs.repository.support;

import com.jwt.szs.model.entity.Member;
import com.jwt.szs.model.entity.QMember;
import com.jwt.szs.repository.support.custom.MemberRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Repository
public class MemberRepositoryImpl extends QuerydslRepositorySupportBasic implements MemberRepositoryCustom {

    private final QMember qMember = QMember.member;

    public MemberRepositoryImpl() {
        super(Member.class);
    }

    @Override
    public Optional<Member> findByUserId(String userId) {

        Member member = getQueryFactory()
                .selectFrom(qMember)
                .where(
                        qMember.userId.eq(userId)
                ).fetchFirst();

        return Optional.ofNullable(member);
    }
}
