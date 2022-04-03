package com.jwt.szs.repository.support.custom;

import com.jwt.szs.model.entity.MemberScrapEvent;

import java.util.Optional;

public interface MemberScrapEventRepositoryCustom {

    Optional<MemberScrapEvent> findByMemberIdAndCreatedAtDesc(Long memberId);
}
