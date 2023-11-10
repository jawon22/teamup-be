package com.kh.teamup.dao;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.teamup.dto.AttachDto;
import com.kh.teamup.dto.ProfileDto;

@Repository
public class ProfileDaoImpl implements ProfileDao{
	
	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public int sequence() {
		return sqlSession.selectOne("emp_profile.sequence");
	}
	
	//프로필 정보 등록(+이미지)
	@Override
	public void addProfile(ProfileDto profileDto) {
		sqlSession.insert("emp_profile.addProfile", profileDto);
	}
	
	//프로필과 이미지 연결
	@Override
	public void connectProfile(int profileNo, int attachNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("profileNo", profileNo);
		params.put("attachNo", attachNo);
		sqlSession.insert("emp_profile.profileImage", params);
	}
	
	//프로필No로 이미지를 찾는 구문
	@Override
	public AttachDto findImage(int profileNo) {
		return sqlSession.selectOne("emp_profile.findImage", profileNo);
	}

}
