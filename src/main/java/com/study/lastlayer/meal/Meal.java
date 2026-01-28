package com.study.lastlayer.meal;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;

import com.study.lastlayer.file.File;

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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "meal")
@Getter
@Setter
@NoArgsConstructor
public class Meal {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 식사 유형 (B: 아침, L: 점심, D: 저녁)
	 */
	@Column(name = "meal_type", length = 1, nullable = false)
	private char mealType;

	@Column(name = "menu", nullable = false)
	@ColumnDefault("''")
	private String menu = "";

	@Column(name = "total_calories", nullable = false)
	@ColumnDefault("0")
	private Integer totalCalories;

	// @OneToOne이 맞지만 그렇게 되면 file_id를 중복 해서 사용할 수 없기 떄문에 테스트 할 때 불편 함.
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "file_id", nullable = true, unique = false, foreignKey = @ForeignKey(name = "fk_meal__file_id"))
	private File imageFile;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	public void onCreated() {
		this.createdAt = LocalDateTime.now();
	}
}
