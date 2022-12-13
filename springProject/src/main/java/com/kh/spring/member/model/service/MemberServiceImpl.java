package com.kh.spring.member.model.service;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.spring.member.model.dao.MemberDao;
import com.kh.spring.member.model.vo.Member;

//@Component //bean으로 등록시키겠다.
@Service //Component보다 더 구체화 시켜서 Service로 사용할것을 명시
public class MemberServiceImpl implements MemberService{

//	private MemberDao memberDao = new MemberDao(); 기존방식
	
	@Autowired
	private MemberDao memberDao;
	
	@Autowired
	private SqlSessionTemplate sqlSession;
	
	@Override
	public Member loginMember(String userId) {
		
		Member loginUser = memberDao.loginMember(sqlSession,userId);
		
		//SqlSessionTemplate 객체를 bean등록 후 @Autowired 해줌으로써
		//스프링 컨테이너가 직접 생명주기를 관리하기 때문에 따로 close를 해줄 필요가 없다.
		
		return loginUser;
	}

	@Override
	public int insertMember(Member m) {
		
		int result = memberDao.insertMember(sqlSession,m);
		
		return result;
	}

	@Override
	public int updateMember(Member m) {
		
//		int result = memberDao.updateMember(sqlSession,m);
//		return result;
		return memberDao.updateMember(sqlSession,m);
	}

	@Override
	public int deleteMember(String userId) {
		return memberDao.deleteMember(sqlSession, userId);
	}
	
	@Override
	public int checkId(String checkId) {
		return memberDao.checkId(sqlSession,checkId);
	}
	
	
}
