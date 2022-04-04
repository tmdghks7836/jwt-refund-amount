package com.jwt.szs.repository.support.custom;

import com.jwt.szs.api.codetest3o3.model.type.MemberSignUpStatus;
import com.jwt.szs.model.entity.MemberSignUpEvent;

import java.util.Optional;

public interface MemberSignUpEventRepositoryCustom {

    Optional<MemberSignUpEvent> findByUserIdAfterSeconds(String userId, MemberSignUpStatus status, Integer createdSeconds);
}
