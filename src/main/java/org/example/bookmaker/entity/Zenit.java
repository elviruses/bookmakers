package org.example.bookmaker.entity;

import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@Entity
@Table(name = "game_zenit")
@AllArgsConstructor
@NoArgsConstructor
public class Zenit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private ZonedDateTime opdt;

    private ZonedDateTime oddt;

    private Long idmt;

    private String cone;

    private String ctwo;

    private String idlg;

    private Double cfw1;

    private Double cfdw;

    private Double cfw2;

    private Double cftmin;

    private Double tota;

    private Double cftmax;

    private Double cf1x;

    private Double cf12;

    private Double cfx2;

    private Double for1;

    private Double cff1;

    private Double for2;

    private Double cff2;

    private Long scde;

    private Long tone1;

    private Long tone2;

    private Long ttwo1;

    private Long ttwo2;

    private String canc;
}
