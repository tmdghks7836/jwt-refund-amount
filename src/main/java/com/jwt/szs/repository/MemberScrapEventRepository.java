package com.jwt.szs.repository;

import com.jwt.szs.model.entity.MemberScrapEvent;
import com.jwt.szs.repository.support.custom.MemberScrapEventRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberScrapEventRepository extends JpaRepository<MemberScrapEvent, Long>, MemberScrapEventRepositoryCustom {

}
