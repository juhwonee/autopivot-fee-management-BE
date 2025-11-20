package com.example.capstonedesign20252.groupMember.domain;

import com.example.capstonedesign20252.common.BaseEntity;
import com.example.capstonedesign20252.group.domain.Group;
import com.example.capstonedesign20252.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMember extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JoinColumn(name = "group_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Group group;

  @JoinColumn(name = "user_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  @Column(name = "is_admin", nullable = false)
  private Boolean isAdmin;

  @Builder
  public GroupMember(Group group, User user, Boolean isAdmin, Integer payment, Boolean isPaid){
    this.group = group;
    this.user = user;
    this.isAdmin = isAdmin != null ? isAdmin : false;
  }
}
