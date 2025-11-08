package com.shuttleverse.connect.client;

import com.shuttleverse.connect.dto.external.SVApiResponse;
import com.shuttleverse.connect.dto.external.SVUser;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "shuttleverse-community", path = "/api/community/v1")
public interface UserServiceClient {

  @GetMapping("/user/me")
  SVApiResponse<SVUser> getCurrentUser();

  @GetMapping("/user/{userId}")
  SVApiResponse<SVUser> getUserById(@PathVariable UUID userId);

  @PostMapping("/user/batch")
  SVApiResponse<List<SVUser>> getUsersByIds(@RequestBody List<UUID> userIds);
}
