package com.ddiring.Backend_Kyc.integration.apick;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;

import java.util.Map;

@FeignClient(name = "apickClient", url = "${apick.base-url:https://apick.app}")
public interface ApickClient {

    @PostMapping(value = "/rest/identi_card/1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Map<String, Object> verifyResidentId(
            @RequestHeader("CL_AUTH_KEY") String authKey,
            @RequestPart("name") String name,
            @RequestPart("rrn1") String rrn1,
            @RequestPart("rrn2") String rrn2,
            @RequestPart("date") String issueDate);
}
