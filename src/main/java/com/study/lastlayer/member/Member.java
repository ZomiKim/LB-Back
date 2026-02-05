package com.study.lastlayer.member;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import com.study.lastlayer.authuser.AuthUser;
import com.study.lastlayer.file.File;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor // default Constructor
@AllArgsConstructor
@Builder
public class Member {
	@Id
	private Long member_id;

	@OneToOne
	@MapsId // Member.member_id를 AuthUser.id와 매핑 (공유 PK 방식)
	@JoinColumn(name = "member_id")
	private AuthUser authUser;

	@Column(nullable = false)
	@ColumnDefault("0")
	private int notificationCount;

	@Column(nullable = false)
	private String gender; // "M", "W" 문자열로 저장
	@Column(nullable = false)
	private LocalDate birthday;
	@Column(nullable = false)
	private Float height;
	@Column(nullable = false)
	private Float weight;

	@Column(nullable = false)
	@Comment("목표 날짜. 몇 일 동안에 빼고 싶은가")
	private Integer target_date;

	@Column(nullable = false)
	@Comment("목표 체중을 달성 하기 위한 하루 섭취 칼로리 (kcal)")
	private Integer daily_calories;

	@Column(nullable = false)
	@Comment("1개만 선택 : 중 감량, 건강 유지, 근육량 증가, 혈당 관리, 콜레스테롤 관리")
	private String goal;

	@Column(nullable = false)
	private Float goal_weight;

	@Column(nullable = false)
	@ColumnDefault("''")
	@Comment("식단에서 피해야 할 음식 리스트. 예)갑각류,콩류")
	@Builder.Default
	private String allergies = "";

	@Column(nullable = false)
	@ColumnDefault("''")
	@Comment("특이사항. 예) 고기 안먹음.")
	@Builder.Default
	private String special_notes = "";

	@Column(nullable = false)
	@ColumnDefault("0")
	@Builder.Default
	private Long point = 0L;

	// @OneToOne이 맞지만 그렇게 되면 file_id를 중복 해서 사용할 수 없기 떄문에 테스트 할 때 불편 함.
	@ManyToOne
	@JoinColumn(name = "file_id", nullable = true, unique = false, foreignKey = @ForeignKey(name = "fk_member__file_id"))
	private File profileImage;

	@Column(nullable = false)
	private LocalDateTime createdAt;
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	public void onCreated() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = this.createdAt;
	}

	@PreUpdate
	public void onUpdatedd() {
		this.updatedAt = LocalDateTime.now();
	}

}
