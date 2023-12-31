package com.kh.teamup.restcontroller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.teamup.dao.AttendDao;
import com.kh.teamup.dao.EmpDao;
import com.kh.teamup.dao.SalDao;
import com.kh.teamup.dao.SalListDao;
import com.kh.teamup.dao.TaxDao;
import com.kh.teamup.dto.SalDto;
import com.kh.teamup.dto.SalListDto;
import com.kh.teamup.dto.TaxDto;
import com.kh.teamup.vo.SalListDetailYearMonthVO;
import com.kh.teamup.vo.TotalWorkingTimeByMonthVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name="급여내역", description = "급여내역 CRUD")
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/salList")
public class SalListRestController {
	
	@Autowired
	private SalListDao salListDao;

	@Autowired
	private SalDao salDao;
	
	@Autowired
	private TaxDao taxDao;
	
	@Autowired
	private AttendDao attendDao;
	
	@Autowired
	private EmpDao empDao;
	
	
	@Operation(description = "사원별 연월지정하여 급여내역저장")
	@PostMapping("/")
	public void calculateSalList( @RequestBody TotalWorkingTimeByMonthVO vo) {

					// attendDao를 통해 근무 시간을 가져옴
			//	    AttendWorkingTimesVO workingTimesVO = new AttendWorkingTimesVO();
			//	    workingTimesVO.setEmpNo(empNo);
			//	    log.debug("근무시간 = {}", workingTimesVO);
			//	    List<AttendWorkingTimesVO> workingTimesList = attendDao.selectListByEmpNo(workingTimesVO);

					SalDto salDto = salDao.selectOne(vo.getEmpNo());
					
					int annualPay = (int) salDto.getSalAnnual();
					int timePay = (int)salDto.getSalTime();//해당 사원의 통상시급 
					
					// 근무 시간 계산 로직 추가
					int totalWorkingHours = attendDao.totalWorkingTimeByMonth(vo);
			//		int salMonth = timePay * totalWorkingHours;//통상 시급 * 한달근무시간 (근무시간은 갖고와야함)
					int salMonth = timePay * totalWorkingHours;
					List<TaxDto> list = taxDao.selectList();
					Map<String, Float> map = new HashMap<>();
					for(TaxDto dto : list) {//taxDto에서 세금명과 세율만 갖고옴
						map.put(dto.getTaxName(), dto.getTaxRate());
					}
					
					//세금 종류별 금액 계산
					//[1]건강보험	
					int health = (int)(salMonth * map.get("건강보험")/100);
					//[2]고용보험
					int emp = (int)(salMonth * map.get("고용보험")/100);
					//[3]국민연금
					int national = (int)(salMonth * map.get("국민연금")/100);
					//[4]장기요양보험료 = 건보료 * 장기요양보험료율
					int ltcare = (int)( health * map.get("장기요양보험")/100 );
					//[5]근로소득세 = 연봉에 따라 적용이 다름
					int work;
				    if (annualPay < 4000000) {
				        work = (int) (salMonth * map.get("소득세1") / 100);
				    }
				    else if (annualPay < 6000000) {
				        work = (int) (salMonth * map.get("소득세2") / 100);
				    }
				    else {
				        work = (int) (salMonth * map.get("소득세3") / 100);
				    }
				    //[6]지방소득세 = 근로소득세 * 지방소득세율
				    int local = (int)( work * map.get("지방소득세")/100);
					
				    SalListDto salListDto = new SalListDto();
				    
					salListDto.setEmpNo(vo.getEmpNo());
					salListDto.setSalListTotal(salMonth);
					salListDto.setSalListHealth(health);
					salListDto.setSalListEmp(emp);
					salListDto.setSalListNational(national);
					salListDto.setSalListLtcare(ltcare);
					salListDto.setSalListLocal(local);
					salListDto.setSalListWork(work);
					salListDto.setSalListDate(vo.getYearMonth());
					
					salListDao.insert( salListDto);
	}

	
	@Operation(description = "사원별 급여내역 목록")
	@GetMapping("/empNo/{empNo}")
	public ResponseEntity<List<SalListDto>>findByEmpNo(@PathVariable int empNo){
		List<SalListDto> list = salListDao.findByEmpNo(empNo);
		return !list.isEmpty() ? ResponseEntity.ok(list) : ResponseEntity.notFound().build();
	}
	
	@Operation(description = "사원별 급여내역 상세(salListDate기준)")
	//@PostMapping("/salListDate")
	@GetMapping("/salListDate/empNo/{empNo}/salListDate/{salListDate}")
	public SalListDto findByEmpSalList(@ParameterObject @ModelAttribute SalListDetailYearMonthVO vo){
//	public SalListDto findByEmpSalList(@RequestBody SalListDetailYearMonthVO vo){
		return salListDao.selectOne(vo);
		}
	
	@Operation(description = "사원별 급여내역 최신 상세(salListDate기준)")
	@GetMapping("/salListDate/empNo/{empNo}")
	public SalListDto findByEmpSalListRecent(@PathVariable int empNo) {
		return salListDao.selectOne(empNo);
	}
	
	
	@Operation(description = "급여내역 삭제")
	@DeleteMapping("/{empNo}")
	public ResponseEntity<String> delete(@PathVariable int empNo){
		return salListDao.delete(empNo) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
	}
	
	
	
	
	
	
	
	
}
















