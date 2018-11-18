package com.nortal.treasurehunt.service;

import com.nortal.treasurehunt.dto.MemberDTO;
import com.nortal.treasurehunt.model.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
  private static final Logger LOG = LoggerFactory.getLogger(MemberService.class);

  private List<Member> members = new ArrayList<>();

  public boolean isValid(MemberDTO member) {
    if (member == null || member.getMemberId() == null) {
      return false;
    }
    return getMember(member.getMemberId()) != null;
  }

  public Member createMember() {
    LOG.info("Creating new member - having {} members so far", members.size());
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
