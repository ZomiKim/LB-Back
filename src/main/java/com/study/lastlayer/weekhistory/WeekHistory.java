package com.study.lastlayer.weekhistory;

import java.time.LocalDate;

import com.study.lastlayer.member.Member;

import jakarta.persistence.*;

@Entity
@Table(name = "week_history")
public class WeekHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Double weight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public WeekHistory() {}

    public WeekHistory(LocalDate date, Double weight, Member member) {
        this.date = date;
        this.weight = weight;
        this.member = member;
    }

    public Long getId() { return id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public Member getMember() { return member; }
}