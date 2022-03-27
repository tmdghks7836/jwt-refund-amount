package com.jwt.szs.model.mapper;

import com.jwt.szs.model.dto.MemberResponse;
import com.jwt.szs.model.dto.MemberResponse.MemberResponseBuilder;
import com.jwt.szs.model.entity.Member;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-03-27T21:56:55+0900",
    comments = "version: 1.4.1.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-6.8.3.jar, environment: Java 11.0.14.1 (Amazon.com Inc.)"
)
@Component
public class MemberMapperImpl implements MemberMapper {

    @Override
    public MemberResponse modelToDto(Member member) {
        if ( member == null ) {
            return null;
        }

        MemberResponseBuilder memberResponse = MemberResponse.builder();

        memberResponse.id( member.getId() );
        memberResponse.username( member.getUsername() );

        return memberResponse.build();
    }
}
