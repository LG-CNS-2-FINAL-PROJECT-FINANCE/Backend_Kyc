package com.ddiring.Backend_Kyc.integration.apick;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ApickResidentIdService {
    private final ApickClient client;
    private final ApickProperties props;

    public ApickResidentIdService(ApickClient client, ApickProperties props) {
        this.client = client;
        this.props = props;
    }

    public Map<String, Object> verify(String name, String rrn1, String rrn2, String issueDateYmd) {
        String authKey = props.getAuthKey();
        if (authKey == null || authKey.isBlank()) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Missing apick.auth-key configuration");
            return err;
        }
        String n = name == null ? "" : name.trim();
        String r1 = rrn1 == null ? "" : rrn1.replaceAll("[^0-9]", "").trim();
        String r2 = rrn2 == null ? "" : rrn2.replaceAll("[^0-9]", "").trim();
        String d = issueDateYmd == null ? "" : issueDateYmd.replaceAll("[^0-9]", "").trim();
        return client.verifyResidentId(authKey, n, r1, r2, d);
    }
}
