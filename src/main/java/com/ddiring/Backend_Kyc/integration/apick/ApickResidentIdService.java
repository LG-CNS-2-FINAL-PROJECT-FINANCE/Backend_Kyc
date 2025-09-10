package com.ddiring.Backend_Kyc.integration.apick;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.core.ParameterizedTypeReference;

import com.ddiring.Backend_Kyc.api.user.UserClient;
import com.ddiring.Backend_Kyc.api.user.UserNameDto;
import com.ddiring.Backend_Kyc.common.exception.ErrorCode;
import com.ddiring.Backend_Kyc.common.util.PrivacyMaskUtils;

import lombok.extern.slf4j.Slf4j;
import reactor.netty.http.client.HttpClient;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ApickResidentIdService {

    private final UserClient userClient;
    private final ApickProperties props;
    private final HttpClient httpClient;

    public ApickResidentIdService(UserClient userClient, ApickProperties props, HttpClient httpClient) {
        this.userClient = userClient;
        this.props = props;
        this.httpClient = httpClient;
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
                log.info("이름이 일치합니다. userName={}, name={}", PrivacyMaskUtils.maskName(userName.getUserName()),
                        PrivacyMaskUtils.maskName(n));
            }
        } catch (Exception e) {
            log.error("이름이 일치하지 않습니다. userName={}, name={}: {}", PrivacyMaskUtils.maskName(userName.getUserName()),
                    PrivacyMaskUtils.maskName(n), e.getMessage());
        }
        String r1 = rrn1 == null ? "" : rrn1.replaceAll("[^0-9]", "").trim();
        String r2 = rrn2 == null ? "" : rrn2.replaceAll("[^0-9]", "").trim();
        String d = issueDateYmd == null ? "" : issueDateYmd.replaceAll("[^0-9]", "").trim();

        log.info("[Apick API 요청] CL_AUTH_KEY={}, name={}, rrn1={}, rrn2={}, date={}",
                PrivacyMaskUtils.maskApiKey(authKey),
                PrivacyMaskUtils.maskName(n),
                PrivacyMaskUtils.maskRrn1(r1),
                PrivacyMaskUtils.maskRrn2(r2),
                PrivacyMaskUtils.maskDate(d));

        WebClient webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl("https://apick.app")
                .defaultHeader("CL_AUTH_KEY", authKey)
                .filter((request, next) -> {
                    if (log.isDebugEnabled()) {
                        String maskedHeaders = request.headers().toString()
                                .replaceAll("CL_AUTH_KEY=([^,\\]]+)",
                                        "CL_AUTH_KEY=" + PrivacyMaskUtils.maskApiKey(authKey));
                        log.debug("[WebClient RAW 요청 헤더] {} {}\n{}", request.method(), request.url(), maskedHeaders);
                        if (request.headers().getContentType() != null &&
                                request.headers().getContentType().toString().startsWith("multipart/form-data")) {
                            log.debug("[WebClient RAW multipart Content-Type] {}", request.headers().getContentType());
                        }
                    }
                    return next.exchange(request);
                })
                .build();

        LinkedMultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("name", n);
        formData.add("rrn1", r1);
        formData.add("rrn2", r2);
        formData.add("date", d);

        try {
            Map<String, Object> response = webClient.post()
                    .uri("/rest/identi_card/1")
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                    })
                    .block();
            log.info("[Apick API 응답] bodyKeys={}", response != null ? response.keySet() : null);

            // API 응답 검증
            validateApickResponse(response);

            return response;
        } catch (Exception e) {
            log.error("[Apick API 오류] WebClient 요청 실패: {}", e.getMessage(), e);
            Map<String, Object> err = new HashMap<>();
            err.put("error", e.getMessage());
            return err;
        }
    }

    // 신원인증 API 응답 검증
    private void validateApickResponse(Map<String, Object> response) {
        if (response == null) {
            log.error("[신원인증 실패] API 응답이 null입니다.");
            throw ErrorCode.kycVerificationFailed();
        }

        Object dataObj = response.get("data");
        if (!(dataObj instanceof Map)) {
            log.error("[신원인증 실패] data 필드가 존재하지 않거나 올바르지 않습니다.");
            throw ErrorCode.kycVerificationFailed();
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) dataObj;

        Object resultObj = data.get("result");

        Integer result = null;

        try {
            if (resultObj instanceof Number) {
                result = ((Number) resultObj).intValue();
            }
        } catch (Exception e) {
            log.error("[신원인증 실패] result 값 변환 실패: {}", e.getMessage());
            throw ErrorCode.kycVerificationFailed();
        }

        if (result == null) {
            log.error("[신원인증 실패] result 값이 존재하지 않습니다. result={}", result);
            throw ErrorCode.kycVerificationFailed();
        }

        if (result != 1) {
            String msg = data.get("msg") != null ? data.get("msg").toString() : "";
            log.error("[신원인증 실패] result={}, msg={}", result, msg);
            throw ErrorCode.kycVerificationFailed();
        }

        log.info("[신원인증 성공] result={}", result);
    }
}
