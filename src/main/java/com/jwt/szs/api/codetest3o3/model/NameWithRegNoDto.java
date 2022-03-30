package com.jwt.szs.api.codetest3o3.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class NameWithRegNoDto {

    private String name;

    private String regNo;
}
