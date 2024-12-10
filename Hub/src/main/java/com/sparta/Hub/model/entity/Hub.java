package com.sparta.Hub.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

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
    private boolean isCenterHub;

    @ManyToOne
    @JoinColumn(name="center_hub_id",referencedColumnName = "hubId")
    private Hub centerHub;

    @OneToMany(mappedBy = "centerHub")
    private List<Hub> hubs;

    public Hub(String hubname, double lati, double longti, String address, boolean isCenterHub) {
        this.hubname = hubname;
        this.lati = lati;
        this.longti = longti;
        this.address = address;
        this.isCenterHub = isCenterHub;
    }


}
