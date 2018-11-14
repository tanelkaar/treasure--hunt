package com.nortal.treasurehunt.service;

import com.nortal.treasurehunt.model.Member;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MemberService {
  private final Map<String, Member> members = new HashMap<>();

  public boolean hasMember(String memberId) {
    return members.get(memberId) != null;
  }

  public Member createMember() {
    Member member = new Member();
    member.setId(UUID.randomUUID().toString());
    addMember(member);
    return member;
  }

  private void addMember(Member member) {
    members.put(member.getId(), member);
  }

  public Member getMember(String memberId) {
    return members.get(memberId);
  }
}
