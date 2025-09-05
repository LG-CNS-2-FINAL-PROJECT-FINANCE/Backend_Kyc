package com.ddiring.Backend_Kyc.integration.apick;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "apickClient", url = "${apick.base-url}", configuration = FeignMultipartSupportConfig.class)
public interface ApickClient {

    @PostMapping(value = "/rest/identi_card/1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Map<String, Object> verifyResidentId(
            @RequestHeader("CL_AUTH_KEY") String authKey,
            @RequestParam("name") String name,
            @RequestParam("rrn1") String rrn1,
            @RequestParam("rrn2") String rrn2,
            @RequestParam("date") String issueDate);
}
