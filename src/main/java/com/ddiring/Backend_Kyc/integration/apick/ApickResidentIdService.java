package com.ddiring.Backend_Kyc.integration.apick;

import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.core.ParameterizedTypeReference;

import com.ddiring.Backend_Kyc.api.user.UserClient;
import com.ddiring.Backend_Kyc.api.user.UserNameDto;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ApickResidentIdService {

    private final UserClient userClient;
    private final ApickProperties props;

    public ApickResidentIdService(UserClient userClient, ApickProperties props) {
        this.userClient = userClient;
        this.props = props;
    }

    public Map<String, Object> verify(String authorization, String userSeq, String name, String rrn1, String rrn2,
            String issueDateYmd) {
        UserNameDto userName = userClient.getUserName(authorization, userSeq);

        String authKey = props.getAuthKey();
        if (authKey == null || authKey.isBlank()) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Missing apick.auth-key configuration");
            return err;
        }

        String n = name == null ? "" : name.trim();
        try {
            if (userName.getUserName().equals(name)) {
                log.info("이름이 일치합니다. userName={}, name={}", userName, n);
            }
        } catch (Exception e) {
            log.error("이름이 일치하지 않습니다. userName={}, name={}: {}", userName, n, e.getMessage());
        }
        String r1 = rrn1 == null ? "" : rrn1.replaceAll("[^0-9]", "").trim();
        String r2 = rrn2 == null ? "" : rrn2.replaceAll("[^0-9]", "").trim();
        String d = issueDateYmd == null ? "" : issueDateYmd.replaceAll("[^0-9]", "").trim();

        log.info("[Apick API 요청] CL_AUTH_KEY={}, name={}, rrn1={}, rrn2={}, date={}", authKey, n, r1, r2, d);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        headers.set("CL_AUTH_KEY", authKey);
        headers.set(HttpHeaders.USER_AGENT, "curl/7.88.1");

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("name", n);
        body.add("rrn1", r1);
        body.add("rrn2", r2);
        body.add("date", d);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    "https://apick.app/rest/identi_card/1",
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });
            log.info("[Apick API 응답] status={}, bodyKeys={}", response.getStatusCode(),
                    response.getBody() != null ? response.getBody().keySet() : null);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("[Apick API 오류] status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            Map<String, Object> err = new HashMap<>();
            err.put("status", e.getStatusCode().value());
            err.put("body", e.getResponseBodyAsString());
            return err;
        }
    }
}
