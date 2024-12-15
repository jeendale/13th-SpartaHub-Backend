package com.sparta.Hub.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_hub")
public class Hub extends Audit{

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID hubId;

    @Column(nullable = false)
    private String hubname;
    @Column(nullable = false)
    private double lati;
    @Column(nullable = false)
    private double longti;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private String username;


    public Hub(String hubname, double lati, double longti, String address) {
        this.hubname = hubname;
        this.lati = lati;
        this.longti = longti;
        this.address = address;
    }

    public void updateHubname(String hubname) {
        this.hubname = hubname;
    }

    public void updateAdress(String adress, double lati, double longti) {
        this.address = adress;
        this.lati = lati;
        this.longti = longti;
    }

    public void updateHubManger(String username) {
        this.username = username;
    }
}
