package com.kh.spring.member.model.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.member.model.vo.Member;


//@Component
//DAO : DB(저장소)와 관련된 작업(영속성 작업) 
@Repository //Dao bean으로 등록하겠다 (저장소)
public class MemberDao {

	public Member loginMember(SqlSessionTemplate sqlSession, String userId) {
		
		return sqlSession.selectOne("memberMapper.loginMember",userId);
	}

	public int insertMember(SqlSessionTemplate sqlSession, Member m) {
		return sqlSession.insert("memberMapper.insertMember",m);
	}

	public int updateMember(SqlSessionTemplate sqlSession, Member m) {
		return sqlSession.update("memberMapper.updateMember",m);
	}

	public int deleteMember(SqlSessionTemplate sqlSession, String userId) {
		return sqlSession.update("memberMapper.deleteMember",userId);
	}

	public int checkId(SqlSessionTemplate sqlSession, String checkId) {
		return sqlSession.selectOne("memberMapper.checkId",checkId);
	}
	
	
	
	

}
