package com.jwt.szs.service;

import com.jwt.szs.exception.CustomRuntimeException;
import com.jwt.szs.exception.ErrorCode;
import com.jwt.szs.exception.ResourceNotFoundException;
import com.jwt.szs.model.dto.MemberCreationRequest;
import com.jwt.szs.model.dto.MemberResponse;
import com.jwt.szs.model.dto.UserDetailsImpl;
import com.jwt.szs.model.entity.Member;
import com.jwt.szs.model.mapper.MemberMapper;
import com.jwt.szs.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MemberService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(final String userId) {
        Optional<Member> memberOptional = memberRepository.findByUserId(userId);

        if (!memberOptional.isPresent()) {
            throw new RuntimeException();
        }

        Member member = memberOptional.get();
        return new UserDetailsImpl(member.getId(), member.getUserId(), member.getPassword());
    }

    public MemberResponse getByUserId(String userId) {

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(new StringBuilder().append(userId).append(" not found").toString()));

        return MemberMapper.INSTANCE.modelToDto(member);
    }

    public MemberResponse getByUserIdAndPassword(String userId, String password) {

        Optional<Member> memberOptional = memberRepository.findByUserId(userId);

        if (!memberOptional.isPresent()) {
            throw new UsernameNotFoundException(userId + " not found");
        }

        Member member = memberOptional.get();

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new BadCredentialsException(ErrorCode.NOT_MATCHED_PASSWORD.getDescription());
        }

        return MemberMapper.INSTANCE.modelToDto(member);
    }

    @Transactional
    public Long signUp(MemberCreationRequest creationRequest) {

        String encodedPassword = passwordEncoder.encode(creationRequest.getPassword());
        Member member = new Member(
                creationRequest.getUserId(),
                creationRequest.getName(),
                creationRequest.getRegNo(),
                encodedPassword
        );

        memberRepository.save(member);

        return member.getId();
    }

    public MemberResponse getById(Long id) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() ->
                        new CustomRuntimeException(ErrorCode.RESOURCE_NOT_FOUND));

        return MemberMapper.INSTANCE.modelToDto(member);
    }
}
