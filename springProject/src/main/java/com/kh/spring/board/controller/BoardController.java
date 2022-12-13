package com.kh.spring.board.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.kh.spring.board.model.service.BoardService;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.Reply;
import com.kh.spring.common.model.vo.PageInfo;
import com.kh.spring.common.template.Pagination;

@Controller
public class BoardController {
	
	@Autowired
	private BoardService boardService;
	
	@RequestMapping("list.bo")
	public String selectList(@RequestParam(value="currentPage",defaultValue = "1") int currentPage,
							Model model) {
		
//		System.out.println(currentPage);
		
		//페이징처리를 위해 전체 게시글 개수 조회해오기
		//페이징처리 pageLimit 10
		//boardLimit 5
		int listCount = boardService.selectListCount();
		int pageLimit = 10;
		int boardLimit = 5;
		
		PageInfo pi = Pagination.getPageinfo(listCount, currentPage, pageLimit, boardLimit);
		
		ArrayList<Board> list = boardService.selectList(pi);
		
		model.addAttribute("list",list);
		model.addAttribute("pi",pi);
		
		
		return "board/boardListView";
	}
	
	@RequestMapping("detail.bo")
	public ModelAndView detailBoard(int bno,
							ModelAndView mv) {
		
//		System.out.println("글번호 : "+bno);
		
		//게시글 조회수 증가 
		int result = boardService.increaseCount(bno);
		
		//조회수 증가가 성공적으로 이루어지면 해당 게시글 정보 조회 
		if(result>0) {
			Board b = boardService.selectBoard(bno);
//			mv.addObject("b",b);
//			mv.setViewName("board/boardDetailView");
			//메소드 체이닝 - 뷰페이지 설정이 마지막에 와야한다(반환타입이 void이기 때문에)
			mv.addObject("b",b).setViewName("board/boardDetailView");
			
		}else {
//			mv.addObject("errorMsg", "게시글 조회 실패");
//			mv.setViewName("common/errorPage");
			mv.addObject("errorMsg", "게시글 조회 실패").setViewName("common/errorPage");
		}
		
		return mv;
	}
	
	//글작성 페이지로 이동시키는 메소드(포워딩)
	@GetMapping("insert.bo")
	public String insertBoard() {
		return "board/boardEnrollForm";
	}
	
	//글등록을 시키는 메소드 
	@PostMapping("insert.bo")
	public ModelAndView insertBoard(Board b,
									MultipartFile upfile,
									ModelAndView mv,
									HttpSession session) {
		
//		System.out.println(b);
//		System.out.println(upfile);
		
		//전달된 파일이 있을경우 - 파일명 수정후에 서버로 업로드 - 원본명,서버에 업로드된 경로를 이어서 다운로드 처리 
		
//		System.out.println(upfile.getOriginalFilename());//파일을 업로드 하지 않고 호출했을시 "" 처리가 된다.
		
		if(!upfile.getOriginalFilename().equals("")) {//파일 업로드가 되었다면
			//파일명 수정 후 서버로 업로드 
			// 짱구.jpg -> 20221202113413559593.jpg 
			/*
			// 1.원본파일명 뽑기
			String originName = upfile.getOriginalFilename(); 
			
			// 2.시간형식을 문자열로 뽑기
			String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			
			// 3.뒤에 붙일 랜덤값 뽑기
			int ranNum = (int)(Math.random()*90000 + 10000); //5자리 랜덤값
			
			// 4.원본 파일명으로부터 확장자명 뽑아오기
			String ext = originName.substring(originName.lastIndexOf("."));
			
			// 5. 뽑아놓은 값 전부 붙여서 파일명 만들기
			String changeName = currentTime + ranNum + ext;
			
			// 6. 업로드 하고자하는 실제 위치경로 지정해주기
			String savePath = session.getServletContext().getRealPath("/resources/uploadFiles/");
			
			
			// 7. 경로와 수정파일명 합쳐서 파일을 업로드해주기
			try {
				upfile.transferTo(new File(savePath+changeName));
			
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			*/
			//아래에서 모듈화 시킨 saveFile 메소드 활용 
			String changeName = saveFile(upfile,session);
			// 8. 원본명,서버에 업로드한 경로를 Board객체에 담아주기
			b.setOriginName(upfile.getOriginalFilename());
			b.setChangeName("resources/uploadFiles/"+changeName);
		}
		
		//만약 첨부파일이 없다면 - 작성자,내용,제목 
		//첨부파일이 있다면 - 작성자,제목,내용,원본이름,저장경로
		int result = boardService.insertBoard(b);
		
		if(result>0) { //성공시 - 게시판 리스트 띄워주기 (list.bo 재요청)
			session.setAttribute("alertMsg", "게시글이 등록되었습니다.");
			mv.setViewName("redirect:/list.bo");
		}else { //실패 - 에러페이지로 포워딩
			mv.addObject("errorMsg","게시글 등록 실패").setViewName("common/errorPage");
		}
		
		
		return mv;
	}
	
	//수정페이지로 이동
	@RequestMapping("updateForm.bo")
	public String boardUpdateForm(int bno,Model model) {
		
		//수정하기페이지에서 필요한 기존 게시글 정보 조회하여 보내주기
		
		Board b = boardService.selectBoard(bno);
		
		model.addAttribute("b",b);
		
		return "board/boardUpdateForm";
	}
	
	//수정 처리 
	@RequestMapping("update.bo")
	public ModelAndView boardUpdate(Board b
						   ,MultipartFile upfile
						   ,HttpSession session
						   ,ModelAndView mv) {
		
		//새로운 첨부파일이 있는지 확인 
		if(!upfile.getOriginalFilename().equals("")) {
			//기존에 첨부파일이 있었던 경우 -> 기존 첨부파일 삭제
			if(b.getOriginName() != null) {//기존 첨부파일의 이름이 담겨있는 경우
				new File(session.getServletContext().getRealPath(b.getChangeName())).delete();
			}
			//새로운 첨부파일 업로드 
			String changeName = saveFile(upfile,session);//아래에서 작업한 saveFile메소드 사용 
			
			//새 데이터 DB에 등록
			b.setOriginName(upfile.getOriginalFilename());
			b.setChangeName("resources/uploadFiles/"+changeName);
		}
		
		//업로드 세팅이 끝났으니 해당 데이터를 service에 전달 
		int result = boardService.updateBoard(b);
		
		if(result>0) {
			//상세보기 페이지로 재요청
			session.setAttribute("alertMsg", "게시글 수정 완료.");
			mv.setViewName("redirect:/detail.bo?bno="+b.getBoardNo());
		}else {
			//에러페이지로 포워딩
			mv.addObject("errorMsg", "게시글 수정 실패 뚜둔");
			mv.setViewName("common/errorPage");
		}
		
		return mv;
	}
	
	
	@RequestMapping("delete.bo")
	public String boardDelete(int bno
			                ,String filePath
			                ,HttpSession session
			                ,Model model) {
		
		int result = boardService.deleteBoard(bno);
		
		if(result>0) { //데이터 삭제에 성공했으면 - 서버에 저장된 파일도 삭제하여 자원 낭비하지 않기
			
			if(!filePath.equals("")) {//파일이 있는 경우
				
				//물리적인 경로 찾기 
				String realPath = session.getServletContext().getRealPath(filePath);
				
				//해당 경로와 연결시켜 파일객체 생성후 삭제 메소드(해당 파일 삭제)
				new File(realPath).delete();
				
			}
			
		}else {
			model.addAttribute("errorMsg","게시글 삭제 실패");
			return "common/errorPage";
		}
		return "redirect:/list.bo";
	}
	
	
	//현재 넘어온 첨부파일 그 자체를 서버의 폴더에 저장시키는 메소드(모듈)
	public String saveFile(MultipartFile upfile,HttpSession session) {
		
		//1.원본파일명 뽑기 
		String originName = upfile.getOriginalFilename();
		
		//2.시간 형식 뽑기
		//"202212050153533"
		String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		
		//3.확장자 추출하기
		String ext = originName.substring(originName.lastIndexOf("."));
		
		//4.랜덤숫자 추출하기 5자리
		int ranNum = (int)(Math.random() * 90000 + 10000); // 5자리 랜덤값
		
		//5. 모두 이어붙이기
		String changeName = currentTime+ranNum+ext;
		
		//6.파일을 업로드할 실질적인 위치(물리경로)찾기 
		String savePath = session.getServletContext().getRealPath("/resources/uploadFiles/");
		
		try {
		//7.물리경로+변경이름으로 파일 생성 및 업로드	
			upfile.transferTo(new File(savePath+changeName));
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return changeName;
	}
	
	
	//댓글리스트 조회
	@ResponseBody
	@RequestMapping(value="rlist.bo",produces="application/json; charset=UTF-8")
	public String selectReplyList(int bno) {
		
		ArrayList<Reply> list = boardService.selectReplyList(bno);
		
		return new Gson().toJson(list);
	}
	
	//댓글 등록
	@ResponseBody
	@RequestMapping(value="rinsert.bo",produces="text/html; charset=UTF-8")
	public String insertReply(Reply r) {
		
		int result = boardService.insertReply(r);
		
		
		return result>0? "yes" : "no";
	}
	
	//게시글 TOP5 조회
	@ResponseBody
	@RequestMapping(value="topboard.bo",produces="application/json; charset=UTF-8")
	public String topBoard() {
		
		ArrayList<Board> list = boardService.topBoard();
		
		
		return new Gson().toJson(list);
	}
	
	
}
