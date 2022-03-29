package com.jwt.szs.repository.support.custom;

import com.jwt.szs.model.entity.Member;

import java.util.Optional;

public interface MemberRepositoryCustom {

    Optional<Member> findByUserId(String userId);
}
