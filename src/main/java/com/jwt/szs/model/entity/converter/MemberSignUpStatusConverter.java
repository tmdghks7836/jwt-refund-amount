package com.jwt.szs.model.entity.converter;

import com.jwt.szs.api.codetest3o3.model.type.MemberSignUpStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class MemberSignUpStatusConverter implements AttributeConverter<MemberSignUpStatus, String> {

    @Override
    public String convertToDatabaseColumn(MemberSignUpStatus status) {
        return status.getCode();
    }

    @Override
    public MemberSignUpStatus convertToEntityAttribute(String code) {

    return Stream.of(MemberSignUpStatus.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}