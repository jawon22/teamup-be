package com.kh.teamup.dao;

import com.kh.teamup.dto.ProfileDto;

public interface ProfileDao {

	void addProfile(ProfileDto profileDto);//프로필 등록할 때 이미지 함께 등록

	void connectProfile(int profileNo, int attachNo);//프로필과 이미지 연결

	
}
