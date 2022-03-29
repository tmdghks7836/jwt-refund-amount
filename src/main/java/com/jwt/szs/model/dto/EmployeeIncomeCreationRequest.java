package com.jwt.szs.model.dto;

import com.jwt.szs.api.codetest3o3.model.ScrapResponse;
import com.jwt.szs.utils.TimeUtils;
import com.jwt.szs.utils.type.DateFormatType;
import lombok.*;

import java.sql.Time;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmployeeIncomeCreationRequest {

    private String companyName;

    private Long paymentAmount;

    private Long calculatedTax;

    private LocalDate businessStartDate;

    private LocalDate businessEndDate;

    private LocalDate paymentDate;

    public EmployeeIncomeCreationRequest(ScrapResponse scrapResponse) {

        ScrapResponse.IncomeInfo incomeInfo = scrapResponse.getIncomeInfo();

        this.calculatedTax = scrapResponse.getCalculatedTex().getTotalAmountUsed();
        this.companyName = incomeInfo.getCompanyName();
        this.paymentAmount = incomeInfo.getPaymentAmount();
        this.businessStartDate = TimeUtils.parseLocalDate(incomeInfo.getBusinessStartDate(), DateFormatType.COMMA);
        this.businessEndDate = TimeUtils.parseLocalDate(incomeInfo.getBusinessEndDate(), DateFormatType.COMMA);
        this.paymentDate = TimeUtils.parseLocalDate(incomeInfo.getPaymentDate(), DateFormatType.COMMA);
    }
}
