package com.jwt.szs.model.entity.converter;

import com.jwt.szs.api.codetest3o3.model.type.MemberScrapStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class MemberScrapStatusConverter implements AttributeConverter<MemberScrapStatus, String> {

    @Override
    public String convertToDatabaseColumn(MemberScrapStatus status) {
        return status.getCode();
    }

    @Override
    public MemberScrapStatus convertToEntityAttribute(String code) {

    return Stream.of(MemberScrapStatus.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}