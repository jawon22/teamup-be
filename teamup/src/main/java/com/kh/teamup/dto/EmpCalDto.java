package com.kh.teamup.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class EmpCalDto {
	
	private int calNo;
	private int empNo;
	private int deptNo;
	private String calStartDate;
	private String calEndDate;
	private String calTitle;
	private String calContent;
	private String calStatus;
	private String calColor;
	

}
