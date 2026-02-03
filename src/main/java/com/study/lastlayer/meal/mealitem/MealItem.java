package com.study.lastlayer.meal.mealitem;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import com.study.lastlayer.meal.Meal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "meal_item")
@Getter
@NoArgsConstructor
public class MealItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "meal_id", nullable = true, unique = false, foreignKey = @ForeignKey(name = "fk_meal_item__meal"))
	private Meal meal;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	@Comment("섭취량 (g)")
	private Integer amount;

	@Column(nullable = false)
	@Comment("해당 음식의 칼로리 (kcal)")
	private Integer calories;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	public void onCreated() {
		this.createdAt = LocalDateTime.now();
	}

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@PreUpdate
	public void onUpdatedd() {
		this.updatedAt = LocalDateTime.now();
	}

	@ColumnDefault("0")
	private Integer carbohydrate;
	@ColumnDefault("0")
	private Integer fat;
	@ColumnDefault("0")
	private Integer protein;
}