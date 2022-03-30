package com.jwt.szs.api.codetest3o3.model;

import com.google.gson.annotations.SerializedName;
import com.jwt.szs.exception.ResourceNotFoundException;
import lombok.*;

import java.util.List;

@Getter
@ToString
public class ScrapResponse {

    @SerializedName("jsonList")
    private EmployeeData employeeData;

    private String appVer;

    private String hostNm;

    private String workerResDt;

    private String workerReqDt;

    public IncomeInfo getIncomeInfo() {

        if(employeeData == null){
            return null;
        }

        return employeeData.getIncomeInfos()
                .stream()
                .findFirst().orElseThrow(() ->
                        new ResourceNotFoundException("not found IncomeInfo."));
    }

    public CalculatedTax getCalculatedTex() {

        if(employeeData == null){
            return null;
        }

        return employeeData.getCalculatedTaxes()
                .stream()
                .findFirst().orElseThrow(() ->
                        new ResourceNotFoundException("not found CalculatedTax."));
    }

    @Getter
    @ToString
    public static class EmployeeData {

        @SerializedName("scrap001")
        private List<IncomeInfo> incomeInfos;

        @SerializedName("scrap002")
        private List<CalculatedTax> calculatedTaxes;

        private String errMsg;

        private String company;

        private String svcCd;

        private Long userId;
    }

    @Getter
    @ToString
    public static class CalculatedTax {

        @SerializedName("총사용금액")
        private Long totalAmountUsed;

        @SerializedName("소득구분")
        private String incomeClassification;
    }

    @Getter
    @ToString
    public static class IncomeInfo {

        @SerializedName("소득내역")
        private String history;

        @SerializedName("총지급액")
        private Long paymentAmount;

        @SerializedName("업무시작일")
        private String businessStartDate;

        @SerializedName("기업명")
        private String companyName;

        @SerializedName("이름")
        private String workerName;

        @SerializedName("지급일")
        private String paymentDate;

        @SerializedName("업무종료일")
        private String businessEndDate;

        @SerializedName("주민등록번호")
        private String regNo;

        @SerializedName("소득구분")
        private String classification;

        @SerializedName("사업자등록번호")
        private String companyRegistrationNumber;
    }
}
