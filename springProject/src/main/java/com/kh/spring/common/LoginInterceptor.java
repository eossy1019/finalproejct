package com.kh.spring.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kh.spring.member.model.vo.Member;

public class LoginInterceptor extends HandlerInterceptorAdapter{
	
	/*
	 *  Interceptor (정확히는 HandlerInterceptor)
	 *  -해당 Controller가 실행되기 전, 실행된 후에 요청을 가로채 실행할 내용을 작성할수있다.
	 *  -주로 로그인 유무판단, 권한 확인등에 사용된다.
	 *  
	 *  -Intercepotr 메소드 종류 (오버라이딩하여 사용할것)
	 *  preHandler (전처리) : DispatcherServlet에서 컨트롤러를 호출하기 전에 실행되는 영역
	 *  postHandler (후처리) : 컨트롤러에서 요청 처리 후 DispatcherServlet으로 뷰 정보가 리턴 되는 순간 실행되는 영역
	 * 
	 * */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		//true를 리턴하면 기존 요청했던 요청이 그대로 실행된다
		//false를 리턴하면 Controller가 실행되지 않는다. 
		
		//로그인 되어있는지 아닌지 확인하는 작업 
		HttpSession session = request.getSession();
		
		Member loginUser = (Member)session.getAttribute("loginUser");
		
		if(loginUser != null) {//로그인되어있는 정보가 있다면 Controller요청 실행 
			return true;
		}else {
			//로그인되어있지 않은 경우 - Controller 요청 처리 하지 않음 
			session.setAttribute("alertMsg", "로그인 후 가능한 서비스입니다.");
			response.sendRedirect(request.getContextPath());
			return false;
		}
		
		
	}
	
	
	
	

}
