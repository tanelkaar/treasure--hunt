package com.nortal.treasurehunt.service;

import com.nortal.treasurehunt.dto.MemberDTO;
import com.nortal.treasurehunt.model.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
  private List<Member> members = new ArrayList<>();

  public boolean isValid(MemberDTO member) {
    if (member == null || member.getMemberId() == null) {
      return false;
    }
    System.out.println(String.format("is valid: %s %d", member.getMemberId(), members.size()));
    return getMember(member.getMemberId()) != null;
  }

  public Member createMember() {
    System.out.println("create: " + members.size());
    Member member = new Member();
    member.setId(UUID.randomUUID().toString());
    synchronized (members) {
      members.add(member);
    }
    return member;
  }

  public Member getMember(String memberId) {
    return members.stream().filter(m -> m.getId().equals(memberId)).findFirst().orElse(null);
  }
}
