package com.jwt.szs.repository;

import com.jwt.szs.model.entity.MemberSignUpEvent;
import com.jwt.szs.repository.support.custom.MemberScrapEventRepositoryCustom;
import com.jwt.szs.repository.support.custom.MemberSignUpEventRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberSignUpEventRepository extends JpaRepository<MemberSignUpEvent, Long>, MemberSignUpEventRepositoryCustom {
}
