package com.kh.spring.member.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kh.spring.member.model.service.MemberService;
import com.kh.spring.member.model.vo.Member;

@Controller // Controller  타입의 어노테이션을 붙여주면 빈 스캐닝을 통해 자동으로 bean을 등록한다.
public class MemberController {
	
	/*
	기존 방법
	private MemberService memberService = new MemberServiceImpl();
	
	기존객체 생성 방식은 객체 간의 결합도가 높아진다(소스코드의 수정이 일어날 경우 하나하나 다 바꿔야하는 단점이 있음)
	서비스가 동시에 많은 횟수가 요청될 경우 그만큼 많은 객체가 생성된다.
	
	-----Spring-----
	Spring의 DI (Dependency Injection)을 이용한 방식으로 객체를 생성하여 주입한다(객체간의 결합도를 낮춤)
	new라는 키워드 없이 선언문만 써줘도 되지만 @Autowired 라는 어노테이션을 *필수로 작성하여야함.
	*
	*/
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private BCryptPasswordEncoder bcryptpasswordEncoder;
	
//	@RequestMapping(value="login.me")
//	public void loginMember() {
//		System.out.println("들어왔나요");
//	}
	
	/*
	 * @RequestMapping(value="매핑값") - Request타입의 어노테이션을 붙여줌으로서 HandlerMapping을 등록한다.
	 * 
	 * *Spring에서 파라미터(요청시 전달값) 를 받는 방법
	 * 
	 * 1.HttpServletRequest 를 이용하여 전달받기 (기존의 jsp/servlet 때의 방식)
	 * 	해당 메소드의 매개변수로 HttpServletRequest를 작성해두면
	 * 	스프링 컨테이너가 해당 메소드를 호출 시 (실행 시) 자동으로 해당 객체를 생성해서 매개변수로 주입해준다.
	 * 
	 * */
	//value=  생략가능
	/*
	@RequestMapping("login.me")
	public String loginMember(HttpServletRequest request) {
		
		String userId = request.getParameter("userId");
		String userPwd = request.getParameter("userPwd");
		
		System.out.println(userId);
		System.out.println(userPwd);
		
//		return "main"; //forward 방식
		return "redirect:/"; //redirect 방식
	}
	*/
	
	/*
	 * 2. @RequestParam 어노테이션을 이용하는 방법
	 * 	- request.getParameter("키")로 벨류를 뽑아오는 역할을 대신해주는 어노테이션
	 * 	- value 속성의 벨류로 jsp에서 작성했던 name 속성값을 담으면 알아서 해당 매개변수로 담아올 수 있다.
	 * 	- 만약, 넘어온 값이 비어있는 형태라면 defaultValue 속성으로 기본값을 설정해줄 수도 있다.
	 * 
	 * */
	
	/*
	@RequestMapping("login.me")
	public String loginMember(@RequestParam(value="userId",defaultValue="admin") String userId,
							  @RequestParam(value="userPwd") String userPwd,
							  @RequestParam(value="email",defaultValue="sss@gmail.com") String email) {
		
		System.out.println("RequestParam을 이용한 값 : "+userId);
		System.out.println("RequestParam을 이용한 값 : "+userPwd);
		System.out.println("RequestParam을 이용한 값 : "+email);
		
		return "main";
	}
	*/
	
	/*
	 * 3.@RequestParam 어노테이션을 생략하는 방법
	 * 	-단 매개변수명을 jsp의 name 속성값(요청시 전달한 키값)과 동일하게 작성하여야 자동 주입이 된다.
	 *  -또한 위에서 사용했던 defaultValue같은 추가 속성은 작성할 수 없다.
	 * */
	
	/*
	@RequestMapping("login.me")
	public String loginMember(String userId,String userPwd,String email,
								@RequestParam(value="userId") String userId2) {
		
		System.out.println("RequestParam을 생략한 방법 : "+userId);
		System.out.println("RequestParam을 생략한 방법 : "+userPwd);
		System.out.println("RequestParam을 생략한 방법 : "+email); //없는 키값과 같은 변수를 작성하면 null값 반환
		System.out.println("RequestParam을 같이 써보기 : "+userId2); //@RequestParam 어노테이션과 같이 사용할수 있음.
		
		return "redirect:/";
	}
	*/
	
	/*
	 * 4. 커맨드 객체 방식
	 * -해당 메소드의 매개변수로 요청시 전달값을 담고자 하는 VO클래스 타입을 세팅하여
	 * -요청시 전달값의 키값(jsp의 name) 을 VO 클래스에 담고자 하는 필드명으로 작성한다.
	 * 
	 * 스프링 컨테이너가 해당 객체를 기본생성자로 생성하여 내부적으로 
	 * setter메소드를 찾아 요청시 전달값을 해당 필드에 담아주는 원리.
	 * 
	 * 이또한 마찬가지로 반드시 name명과 필드명이 일치하여야한다.
	 * 
	 * 
	 * --요청처리 후 응답페이지로 응답 데이터를 가지고 포워딩 또는 url 재요청 하는 방법
	 * 
	 * 1.스프링에서 제공하는 Model 객체를 이용하는 방법
	 * -포워딩할 응답뷰로 전달하고자 하는 데이터를 맵 형식(key-value)로 담을 수 있는 영역
	 * -Model 객체는 requestScope이다. 단,setAttribute가 아닌 addAttribute 메소드를 이용하여야 한다.
	 * 
	 * */
	
	/*
	//비밀번호 암호화 전 로그인 처리
	@RequestMapping("login.me")
	public String loginMember(Member m
							 ,HttpSession session
							 ,Model model) {
		
//		System.out.println("VO클래스로 전달 받기 : "+m.getUserId());
//		System.out.println("VO클래스로 전달 받기 : "+m.getUserPwd());
		
		Member loginUser = memberService.loginMember(m);
		
		if(loginUser==null) { //로그인 실패 - requestScope에 에러메세지 담아서 에러페이지로 포워딩
			
//			request.setAttribute("errorMsg","로그인 실패");
			model.addAttribute("errorMsg","로그인 실패");
			//WEB-INF/views/common/errorPage.jsp";
			//WEB-INF/views 와 .jsp는 servlet-context.xml에 등록되어있기 때문에 
			return "common/errorPage"; //중간 경로만 작성하면 된다.
		}else { //로그인 성공 loginUser를 SessionScope에 담아서 메인페이지로 재요청
			
			session.setAttribute("loginUser", loginUser);
			
			//url 재요청 방식 - sendRedirect 방식 (url주소를 제시하여 재요청)
			//redirect:요청url
			return "redirect:/"; //contextPath뒤에 붙는 / 를 의미하여 메인페이지(index)
		}
	}
	
	*/
	
	//비밀번호 암호화 후 로그인 처리 
	/*
	 * 2.스프링에서 제공하는 ModelAndView 클래스 
	 * model은 데이터를 key-value 세트로 담을 수 있는 공간 
	 * view는 응답뷰에 대한 정보를 담을수 있는 공간
	 * 이 경우에는 반환타입이 String이 아니라 ModelAndView 형태로 반환하여야한다.
	 * Model과 View 가 결합된 형태 단, Model은 위에서 사용했듯이 단독사용이 가능하지만.
	 * View는 단독 사용을 할 수 없다.
	 * 
	 * */
	@RequestMapping("login.me")
	public ModelAndView loginMember(Member m,
							 HttpSession session,
							 ModelAndView mv) {
		Member loginUser = memberService.loginMember(m.getUserId());
		//loginUser : 아이디만으로 조회해온 회원정보
		//loginUser의 userPwd 필드에는 암호화되어서 DB에 저장된 암호비밀번호가 들어있다.
		//그 암호화된 비밀번호와 사용자가 입력한 비밀번호가 암호화되었을시에 일치하게 되는지 
		//확인해주는 메소드를 사용하여 해당 정보가 일치하는지 구분한다.
		//이때 사용하는 메소드는 BCryptPasswordEncoder 객체의 matches 메소드이다.
		//matches(평문,암호문) 을 작성하면 내부적으로 복호화 작업이 이루어져 
		//두 데이터가 일치하는지 확인하여 true/false로 반환다
		//사용자가 입력한 로그인폼에서 넘어온 비밀번호 : m.getUserPwd(); 
		//데이터베이스에서 조회해온 암호화된 비밀번호 : loginUser.getUserPwd();
		
		if(loginUser != null && bcryptpasswordEncoder.matches(m.getUserPwd(), loginUser.getUserPwd())) {
			session.setAttribute("loginUser", loginUser);
			
			//setViewName : 요청 주소 
			mv.setViewName("redirect:/");//메인화면 재요청
		}else {//실패
			//addObject : setAttribute나 addAttribute와 같은 용도(requestScope에 등록)
			mv.addObject("errorMsg", "로그인 실패");
			mv.setViewName("common/errorPage");
		}
		
		return mv;
	}
	
	
	//로그아웃
	@RequestMapping("logout.me")
	public String logoutMember(HttpSession session) {
		
		session.removeAttribute("loginUser");
		
		return "redirect:/";
	}
	
	//회원가입 폼으로 이동
	@GetMapping("insert.me")
	public String enrollForm() {
		
		//WEB-INF/views/member/memberEnrollForm.jsp 로 포워딩
		return "member/memberEnrollForm";
	}
	
	//회원가입 등록
	@PostMapping("insert.me")
	public String insertMember(Member m,
							HttpSession session,
							Model model) {
		
		// 1. 한글깨짐 - web.xml에서 encodingFilter를 통해 해결
		// 2. 나이에 빈값이 들어오면 typeMismatchException 발생 
		//   -Member VO에 age필드를 String자료형으로 변경하여 해결 (lombok활용)
		// 3. 비밀번호가 사용자가 입력한 그대로 저장되는 문제
		// -Bcypt 방식의 암호화를 통해 암호문으로 변경
		// -spring security module에서 제공하는 라이브러리 (pom.xml에 추가)
		// -BcryptPasswordEncoding 클래스를 사용하기 위해 xml파일에 등록
		// -web.xml에 spring-security.xml 파일을 로딩할수있게 작업
		
		System.out.println("원본 패스워드 : "+m.getUserPwd());
		String encPwd = bcryptpasswordEncoder.encode(m.getUserPwd());
		System.out.println("암호화 패스워드 : "+encPwd);
		//$2a$10$RvnG9AIohDOtbnx9lwjIBeYDxaPtbC3SLPIpiU1h..2ESdcHABLvW
		
		m.setUserPwd(encPwd);
		
		int result = memberService.insertMember(m);
		
		if(result>0) {
			session.setAttribute("alertMsg", "회원가입이 완료되었습니다.");
			return "redirect:/";
		}else {
			model.addAttribute("errorMsg","회원가입 실패");
			
			return "common/errorPage";
		}
	
	}
	
	@RequestMapping("mypage.me")
	public String myPage() {
		
		return "member/mypage";
	}
	
	@RequestMapping("update.me")
	public String updateMember(Member m,
							HttpSession session,
							Model model) {
		
		int result = memberService.updateMember(m);
		//성공시 session에 있던 기존 loginUser를 지우고 변경된 loginUser를 넣어주고
		//마이페이지로 재요청 보내기(alertMsg도 보내보기)-header에서 작성해둘것
		if(result>0) {
			//db에 있는 회원 정보 조회(수정후)
			Member updateMember = memberService.loginMember(m.getUserId());
			session.setAttribute("loginUser", updateMember);
			session.setAttribute("alertMsg", "회원정보 수정 완료!");
			
			return "redirect:/mypage.me";
			
		}else {
			//실패시 에러페이지에 에러메세지 담아서 보내기 
			
			model.addAttribute("errorMsg","회원 정보 수정 실패!!");
			
			return "common/errorPage";
		}
		
	}
	
	@RequestMapping("delete.me")
	public ModelAndView deleteMember(String userPwd,
									HttpSession session,
									ModelAndView mv) {
		
		//로그인 되어있는 회원정보의 비밀번호(암호화되어있는)를 
		//사용자에게 전달받은 비밀번호(평문) 과 일치하는지 matches 메소드를 이용하여 확인
		
		Member loginUser = (Member)session.getAttribute("loginUser"); //로그인 회원정보 
		String userId = loginUser.getUserId(); //로그인 정보 아이디
		String loginUserPwd = loginUser.getUserPwd();//로그인정보 비밀번호
		
		if(bcryptpasswordEncoder.matches(userPwd, loginUserPwd)) { //입력받은 비밀번호와 암호화된 비밀번호가 일치하다면
			//탈퇴처리 후 로그인유저 정보 삭제 - 메인페이지 보내기 - 알림메세지 추가
			int result = memberService.deleteMember(userId);
			
			if(result>0) {//최종적으로 탈퇴처리 성공
				session.removeAttribute("loginUser");
				session.setAttribute("alertMsg", "그동안 서비스를 이용해주셔서 감사합니다.");
				mv.setViewName("redirect:/");
			}else {//탈퇴 처리 실패
				mv.addObject("errorMsg", "탈퇴 처리 실패");
				mv.setViewName("common/errorPage");
			}
		}else { //비밀번호가 일치하지 않을때
			//탈퇴 실패 메세지 보내고 마이페이지 재요청 
			session.setAttribute("alertMsg", "비밀번호가 일치하지 않습니다.");
			mv.setViewName("redirect:/");
		}
		return mv;
	}
	/*
	//아이디 중복체크
	@ResponseBody
//	@RequestMapping("idCheck.me")
	@RequestMapping(value="idCheck.me",produces="text/html; charset=UTF-8")
	public String idCheck(String checkId) {
	
		Member m = memberService.loginMember(checkId);
		
//		if(m!=null) { //조회된 회원이 있음(아이디 중복)
//
//			return "NNNNN";
//		}else {//조회된 회원이 없음(아이디 사용 가능) 
//		
//			return "NNNNY";
//		}
		
		return (m!=null) ? "하하" : "호호";
	}
	*/
	
	@ResponseBody
	@RequestMapping("idCheck.me")
	public String idCheck(String checkId) {
		
		int count = memberService.checkId(checkId);
		
//		String str = "";
//		
//		if(count>0) { // 사용불가
//			str="NNNNN";
//		}else { // 사용가능 
//			str="NNNNY";
//		}
//		
//		return str;
		
		return count>0 ? "NNNNN" : "NNNNY";
		
	}
	
	
	
	
	
}
