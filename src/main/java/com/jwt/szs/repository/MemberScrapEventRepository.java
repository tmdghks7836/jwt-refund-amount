package com.jwt.szs.repository;

import com.jwt.szs.model.entity.MemberScrapEvent;
import com.jwt.szs.repository.support.custom.MemberScrapEventRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MemberScrapEventRepository extends JpaRepository<MemberScrapEvent, Long>, MemberScrapEventRepositoryCustom {

}
