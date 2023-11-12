package com.kh.teamup.restcontroller;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.teamup.dao.AttachDao;
import com.kh.teamup.dao.ProfileDao;
import com.kh.teamup.dto.AttachDto;
import com.kh.teamup.dto.ProfileDto;
import com.kh.teamup.vo.ProfileImageVO;
import com.kh.teamup.vo.ProfileUpdateVO;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
@Tag(name = "회원프로필" , description = "회원프로필CRUD")

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/profile")
public class ProfileRestController {
	
	@Autowired
	private ProfileDao profileDao;
	
	@Autowired
	private AttachDao attachDao;
	
	//프로필 등록(+파일 업로드)
	@PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void addProfile(@ModelAttribute ProfileImageVO vo) throws IllegalStateException, IOException {
//		log.debug("dto={}", vo);
		
		int profileNo = profileDao.sequence();//profileNo를 가져옴
//		log.debug("profileNo={}", profileNo);
		
		
		
		ProfileDto profileDto = vo.getProfileDto();
		profileDto.setProfileNo(profileNo);
		profileDao.addProfile(profileDto);
		
		
		MultipartFile attach = vo.getAttach();
		
		int attachNo = attachDao.sequence();
		
		String home = System.getProperty("user.home");
		File dir = new File(home, "upload");
		dir.mkdirs();
		File target = new File(dir, String.valueOf(attachNo));
		attach.transferTo(target);
		
		AttachDto attachDto = new AttachDto();
		attachDto.setAttachNo(attachNo);
		attachDto.setAttachName(attach.getOriginalFilename());
		attachDto.setAttachSize(attach.getSize());
		attachDto.setAttachType(attach.getContentType());
		attachDao.insert(attachDto);
		log.debug("attach={}", attachDto);
		log.debug("f={}", profileDto);
		
		profileDao.connectProfile(profileDto.getProfileNo(), attachNo);
	}
	
	//프로필 이미지 수정
	@PutMapping(value = "/{profileNo}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> editProfile(@ModelAttribute ProfileUpdateVO vo) throws IllegalStateException, IOException{
		
		MultipartFile attach = vo.getAttach();
		
		ProfileDto profileDto = vo.getProfileDto();
		profileDao.update(vo);
		
		if(!attach.isEmpty()) {//파일이 있으면
			//파일 삭제 - 기존 파일이 있을 경우에만 처리
			AttachDto attachDto = profileDao.findImage(profileDto.getProfileNo());
			String home = System.getProperty("user.home");
			File dir = new File(home, "upload");
			
			if(attachDto != null) {
				attachDao.delete(attachDto.getAttachNo());
				
				File target = new File(dir, String.valueOf(attachDto.getAttachNo()));
				target.delete();
			}
				
				//파일 추가 및 연결
				//파일번호 생성
				int attachNo = attachDao.sequence();
				log.debug("attachNo=", attachNo);
				
				//신규파일 저장
				File insertTarget = new File(dir, String.valueOf(attachNo));
				attach.transferTo(insertTarget);
				
				//신규파일정보 저장
				AttachDto insertDto = new AttachDto();
				insertDto.setAttachNo(attachNo);
				insertDto.setAttachName(attach.getOriginalFilename());
				insertDto.setAttachSize(attach.getSize());
				insertDto.setAttachType(attach.getContentType());
				attachDao.insert(insertDto);
				
				//프로필 + 파일 연결
				profileDao.connectProfile(profileDto.getProfileNo(), attachNo);
		}
		
		return ResponseEntity.ok().body("프로필 이미지 수정성공");
	}
	

}
