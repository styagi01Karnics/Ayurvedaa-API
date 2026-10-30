package com.ayurveda.therapist.dto.client;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthUserClientResponse {

    private UUID id;
    private String email;

}
