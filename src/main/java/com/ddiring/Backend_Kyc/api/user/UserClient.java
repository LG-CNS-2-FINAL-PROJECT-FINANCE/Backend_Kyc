package com.ddiring.Backend_Kyc.api.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "userClient", url = "${user.base-url}")
public interface UserClient {

    @GetMapping("/api/user/{userSeq}")
    UserNameDto getUserName(@PathVariable("userSeq") String userSeq);
}
