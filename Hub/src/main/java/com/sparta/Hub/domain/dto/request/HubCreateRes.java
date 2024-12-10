package com.sparta.Hub.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HubCreateRes {
    private String hubname;
    private String adress;
    private double lati;
    private double longti;
    private boolean isCenterHub;
}
