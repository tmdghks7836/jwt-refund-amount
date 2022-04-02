package com.jwt.szs.repository;

import com.jwt.szs.model.entity.MemberSignUpEvent;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberSignUpEventRepository extends JpaRepository<MemberSignUpEvent, Long> {
}
