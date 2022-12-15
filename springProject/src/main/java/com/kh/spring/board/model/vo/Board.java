package com.kh.spring.board.model.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Board {
	
	private int boardNo;			//	BOARD_NO	NUMBER
	private String boardTitle;				//	BOARD_TITLE	VARCHAR2(100 BYTE)
	private String boardWriter;//	BOARD_WRITER	VARCHAR2(4000 BYTE)
	private String boardContent;				//	BOARD_CONTENT	VARCHAR2(4000 BYTE)
	private String originName;//	ORIGIN_NAME	VARCHAR2(100 BYTE)
	private String changeName;//	CHANGE_NAME	VARCHAR2(100 BYTE)
	private int count;//	COUNT	NUMBER
	private Date createDate;//	CREATE_DATE	DATE
	private String status;//	STATUS	VARCHAR2(1 BYTE)
	
}
