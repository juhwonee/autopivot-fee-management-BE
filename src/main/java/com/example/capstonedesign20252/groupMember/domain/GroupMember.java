package com.example.capstonedesign20252.groupMember.domain;

import com.example.capstonedesign20252.common.BaseEntity;
import com.example.capstonedesign20252.group.domain.Group;
import com.example.capstonedesign20252.groupMember.dto.AddGroupMemberDto;
import com.example.capstonedesign20252.groupMember.dto.UpdateGroupMemberDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Objects;
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

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "email")
  private String email;

  @Column(name = "phone")
  private String phone;

  @Column(name = "is_admin", nullable = false)
  private Boolean isAdmin;

  @Builder
  public GroupMember(Group group, String name, String email, String phone, Boolean isAdmin){
    this.group = group;
    this.name = name;
    this.email = email;
    this.phone = phone;
    this.isAdmin = isAdmin != null ? isAdmin : false;
  }

  public void updateGroupMember(UpdateGroupMemberDto updateGroupMemberDto){
    if(updateGroupMemberDto.name() != null) this.name = updateGroupMemberDto.name();
    if(updateGroupMemberDto.email() != null) this.email = updateGroupMemberDto.email();
    if(updateGroupMemberDto.phone() != null) this.phone = updateGroupMemberDto.phone();
  }
}
