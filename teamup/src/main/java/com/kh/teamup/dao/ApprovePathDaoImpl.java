package com.kh.teamup.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.teamup.dto.ApprovePathDto;

@Repository
public class ApprovePathDaoImpl implements ApprovePathDao{

	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public int sequence() {
		return sqlSession.selectOne("approvePath.sequence");
	}
	
	@Override
	public void insert(ApprovePathDto approvePathDto) {
		sqlSession.insert("approvePath.approvePathSave",approvePathDto);
	}
	
	@Override
	public List<ApprovePathDto> selectByApprNo(int apprNo) {
		return sqlSession.selectList("approvePath.findByApprNo", apprNo);
	}
	
}
