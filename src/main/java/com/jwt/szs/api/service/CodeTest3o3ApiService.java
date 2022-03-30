package com.jwt.szs.api.service;

import com.jwt.szs.api.codetest3o3.CodeTest3o3Api;
import com.jwt.szs.api.codetest3o3.model.NameWithRegNoDto;
import com.jwt.szs.core.CustomCallback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import retrofit2.Call;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeTest3o3ApiService {

    private final CodeTest3o3Api codeTest3o3Api;

    public void getScrapByNameAndRegNo(NameWithRegNoDto request, CustomCallback callback) {

        Call call = codeTest3o3Api.getScrapByNameAndRegNo(request);

        call.enqueue(callback);
    }
}
