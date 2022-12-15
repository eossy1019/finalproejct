<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<jsp:include page="common/header.jsp"/>

	<div class="content">
		<br><br>
		<div class="innerOuter">
			<h4>게시글 Top5</h4>
			<br>
			<a href="list.bo">게시글 더보기</a>
		
			<table id="boardList" class="table table-hover" align="center">
				<thead>
					<tr>
					  <th>글번호</th>
                      <th>제목</th>
                      <th>작성자</th>
                      <th>조회수</th>
                      <th>작성일</th>
                      <th>첨부파일</th>
					</tr>
				</thead>
				<tbody>
					<!-- 조회수 높은 순으로 조회하여 5개 게시글 데이터 뿌리기 -->
				</tbody>
			
			</table>
		</div>
	</div>
	<script>
		$(function(){
			topBoardList();
			
			setInterval(topBoardList,30000);
			
// 			동적으로 생성된 요소에 이벤트를 걸어주려면 상위요소 선택자 function을 작성해야한다
// 			$("#boardList tr").click(function(){
// 				console.log("클릭됨");
// 			})

		//동적요소 이벤트 걸기
		$(document).on("click","#boardList>tbody>tr",function(){
			var bno = $(this).children().eq(0).text();
			location.href="detail.bo?bno="+bno;
		})
			
		})
		
		function topBoardList(){
			$.ajax({
				url : "topboard.bo",
				success : function(result){
					var resultStr ="";
					for(var i=0; i<result.length; i++){
						resultStr+="<tr>"
								  +"<td>"+result[i].boardNo+"</td>"
								  +"<td>"+result[i].boardTitle+"</td>"
								  +"<td>"+result[i].boardWriter+"</td>"
								  +"<td>"+result[i].count+"</td>"
								  +"<td>"+result[i].createDate+"</td>"
								  +"<td>";//첨부파일 처리할 td 열어놓고 
						if(result[i].originName != null){//첨부파일이 있을경우
						  	resultStr += "★"; //있으면 별표 
						}
						resultStr +="</td></tr>"; //td닫고 tr닫고
						
						$("#boardList>tbody").html(resultStr);
					}
				},
				error : function(){
					console.log("통신 실패");
				}
			})
		}
	
	
	</script>

	
<%-- 	<jsp:include page="common/footer.jsp"/> --%>
	<%@ include file="common/footer.jsp" %>
</body>
</html>