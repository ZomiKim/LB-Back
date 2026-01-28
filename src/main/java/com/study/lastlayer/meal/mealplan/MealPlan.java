package com.study.lastlayer.meal.mealplan;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.study.lastlayer.meal.Meal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "meal_plan")
@Getter
@Setter
@NoArgsConstructor

public class MealPlan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private com.study.lastlayer.member.Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "meal_id", nullable = false)
	private Meal meal;

	@Column(name = "date_at", nullable = false)
	private LocalDate dateAt;

	@Column(name = "is_accepted", nullable = false)
	private Boolean isAccepted = false;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	public void onCreated() {
		this.createdAt = LocalDateTime.now();
	}
}