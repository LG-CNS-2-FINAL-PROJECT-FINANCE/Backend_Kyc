package com.ddiring.Backend_Kyc.web;

import com.ddiring.Backend_Kyc.common.util.GatewayRequestHeaderUtils;
import com.ddiring.Backend_Kyc.integration.apick.ApickResidentIdService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/kyc")
public class ApickResidentIdController {
    private final ApickResidentIdService service;

    public ApickResidentIdController(ApickResidentIdService service) {
        this.service = service;
    }

    @PostMapping("/resident-id/verify")
    public ResponseEntity<Map<String, Object>> verify(@RequestBody Map<String, String> req) {
        String userSeq = GatewayRequestHeaderUtils.getUserSeq();
        String name = req.getOrDefault("name", "");
        String rrn1 = req.getOrDefault("rrn1", "");
        String rrn2 = req.getOrDefault("rrn2", "");
        String date = req.getOrDefault("date", "");
        Map<String, Object> res = service.verify(userSeq, name, rrn1, rrn2, date);
        return ResponseEntity.ok(res);
    }
}
