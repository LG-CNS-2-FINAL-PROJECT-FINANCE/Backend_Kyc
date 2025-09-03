package com.ddiring.Backend_Kyc.integration.apick;

import org.springframework.stereotype.Service;

import com.ddiring.Backend_Kyc.api.user.UserClient;
import com.ddiring.Backend_Kyc.api.user.UserNameDto;

import java.util.HashMap;
import java.util.Map;

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

    public Map<String, Object> verify(String userSeq, String name, String rrn1, String rrn2, String issueDateYmd) {
        UserNameDto userName = userClient.getUserName(userSeq);

        String authKey = props.getAuthKey();
        if (authKey == null || authKey.isBlank()) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Missing apick.auth-key configuration");
            return err;
        }
        try {
            if (userName.getUserName().equals(name)) {
                String n = name == null ? "" : name.trim();
                String r1 = rrn1 == null ? "" : rrn1.replaceAll("[^0-9]", "").trim();
                String r2 = rrn2 == null ? "" : rrn2.replaceAll("[^0-9]", "").trim();
                String d = issueDateYmd == null ? "" : issueDateYmd.replaceAll("[^0-9]", "").trim();
                return client.verifyResidentId(authKey, n, r1, r2, d);
            }
        } catch (Exception e) {
            throw new RuntimeException("이름이 일치하지 않습니다.", e);
        }

        return null;
    }
}
