package com.jwt.szs.service;

import com.jwt.szs.exception.CustomRuntimeException;
import com.jwt.szs.exception.ErrorCode;
import com.jwt.szs.model.dto.MemberCreationRequest;
import com.jwt.szs.model.dto.MemberResponse;
import com.jwt.szs.model.entity.Member;
import com.jwt.szs.model.mapper.MemberMapper;
import com.jwt.szs.repository.MemberRepositorySupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepositorySupport memberRepositorySupport;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public MemberResponse authenticate(String username, String password) {

        Optional<Member> memberOptional = memberRepositorySupport.findByUsername(username);

        if (!memberOptional.isPresent()) {
            throw new UsernameNotFoundException(username +" not found");
        }

        Member member = memberOptional.get();

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new BadCredentialsException(ErrorCode.NOT_MATCHED_PASSWORD.getDescription());
        }

        return MemberMapper.INSTANCE.modelToDto(member);
    }

    @Transactional
    public Long join(MemberCreationRequest creationRequest) {

        String encodedPassword = passwordEncoder.encode(creationRequest.getPassword());
        Member member = new Member(
                creationRequest.getUserId(),
                creationRequest.getName(),
                creationRequest.getRegNo(),
                encodedPassword
        );

        memberRepositorySupport.save(member);

        return member.getId();
    }

    public MemberResponse getById(Long id){

        Member member = memberRepositorySupport.findById(id)
                .orElseThrow(() ->
                        new CustomRuntimeException(ErrorCode.RESOURCE_NOT_FOUND));

        return MemberMapper.INSTANCE.modelToDto(member);
    }
}
