package com.ddiring.Backend_Kyc.integration.apick;

import org.springframework.stereotype.Service;

import com.ddiring.Backend_Kyc.api.user.UserClient;
import com.ddiring.Backend_Kyc.api.user.UserNameDto;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ApickResidentIdService {

    private final UserClient userClient;
    private final ApickClient client;
    private final ApickProperties props;

    public ApickResidentIdService(UserClient userClient, ApickClient client, ApickProperties props) {
        this.userClient = userClient;
        this.client = client;
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
                log.info("이름이 일치합니다. userSeq={}, name={}", userSeq, n);
            }
        } catch (Exception e) {
            log.error("이름이 일치하지 않습니다. userSeq={}, name={}: {}", userSeq, n, e.getMessage());
        }
        String r1 = rrn1 == null ? "" : rrn1.replaceAll("[^0-9]", "").trim();
        String r2 = rrn2 == null ? "" : rrn2.replaceAll("[^0-9]", "").trim();
        String d = issueDateYmd == null ? "" : issueDateYmd.replaceAll("[^0-9]", "").trim();

        // 외부 API 호출 전 인증키와 파라미터 로그 출력
        log.info("[Apick API 요청] CL_AUTH_KEY={}, name={}, rrn1={}, rrn2={}, date={}", authKey, n, r1, r2, d);

        return client.verifyResidentId(authKey, n, r1, r2, d);
    }
}
