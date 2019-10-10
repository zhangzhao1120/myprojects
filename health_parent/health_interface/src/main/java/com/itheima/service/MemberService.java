package com.itheima.service;

import com.itheima.pojo.Member;

import java.util.List;

public interface MemberService {
    public Member isMember (String telephone);
    public void AutoregisterMember(Member member);
    public List<Integer> findMemberCountByMonths(List<String> months);
}
