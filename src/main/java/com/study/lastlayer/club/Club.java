package com.study.lastlayer.club;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "club")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 
    @Column
    private String name; 

    @Column
    private String description; 

    @Column
    private Long managerId; 

    @Column
    private Long bgFileId; 

    @Column
    private String keywords; 
}