package com.nortal.treasurehunt.service;

import com.nortal.treasurehunt.model.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MemeberService {
  private List<Member> members = new ArrayList<>();

  public boolean hasMember(String memberId) {
    return members.stream().filter(m -> m.getId().equals(memberId)).findFirst().isPresent();
  }

  public Member createMember() {
    Member member = new Member();
    member.setId(UUID.randomUUID().toString());
    members.add(member);
    return member;
  }
}
