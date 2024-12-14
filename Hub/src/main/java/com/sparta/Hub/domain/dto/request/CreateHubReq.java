package com.sparta.Hub.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateHubReq {
    private String hubname;
    private String address;
    private double lati;
    private double longti;
    private boolean isCenterHub;
    private String username;
}
