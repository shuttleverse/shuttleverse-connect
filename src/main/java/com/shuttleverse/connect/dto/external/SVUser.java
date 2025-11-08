package com.shuttleverse.connect.dto.external;

import lombok.Data;
import java.util.UUID;

@Data
public class SVUser {
    private UUID id;
    private String username;
    private String email;
    private String bio;
}
