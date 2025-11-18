package com.example.capstonedesign20252.group.domain;

import com.example.capstonedesign20252.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "groups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Group {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JoinColumn(name = "user_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  @Column(name = "group_name", nullable = false)
  private String groupName;

  @Column(name = "description", nullable = false)
  private String description;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private GroupCategory groupCategory;

  @Column(name = "fee", nullable = false)
  private Integer fee;

  @Builder
  public Group(User user, String groupName, String description, GroupCategory groupCategory, Integer fee){
    this.user = user;
    this.groupName = groupName;
    this.description = description;
    this.groupCategory = groupCategory;
    this.fee = fee;
  }
}
