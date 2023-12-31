package com.kh.teamup.vo;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kh.teamup.dto.BoardDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@JsonIgnoreProperties(ignoreUnknown = true)
@Data @Builder @AllArgsConstructor @NoArgsConstructor 
public class BoardVO {//공지사항목록(+페이지네이션)
    private long boardNo;
    private int empNo;
    private int deptNo;
    private String comId;
    private String boardTitle;
    private String boardContent;
    private Timestamp boardUpdateDate, boardWriteDate;
    private int boardReadCount, boardReplyCount;

    private String empName; // emp 테이블의 emp_name 필드
    private String deptName; // dept 테이블의 dept_name 필드
    
    private String select;
    private String keyword;

    private int size;  // 페이지당 항목 수
    private int page;  // 현재 페이지 번호
    private long totalCount;  // 총 게시물 수
    
}
