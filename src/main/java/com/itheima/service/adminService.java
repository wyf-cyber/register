package com.itheima.service;

import com.itheima.pojo.doctorSchedule;

import com.itheima.mapper.DoctorMapper;
import com.itheima.mapper.RegisterMapper;
import com.itheima.mapper.UserMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {
    @Autowired
    private DoctorMapper doctorMapper;
    @Autowired
    private RegisterMapper registerMapper;
    @Autowired
    private UserMapper userMapper;

    // 获取普通用户的数量
    public ResponseEntity<?> getUserCountService() {
        int userCount = userMapper.getUserCount();
        int adminCount = userMapper.getAdminCount();
        int res = userCount-adminCount;
        return ResponseEntity.ok(res);
    }

    // 添加一个医生的信息
    public ResponseEntity<?> addDoctorService(String department, String doctor, String detail, String day) {
        doctorMapper.addDoctor(department, doctor, detail, 0, day);
        return ResponseEntity.ok("Doctor added successfully");
    }

    // 修改指定医生的信息
    public ResponseEntity<?> updateDoctorService(String department, String doctor, String day, String newDepartment, 
                                               String newDoctor, String newDetail, String newDay) {
        doctorMapper.updateDoctor(department, doctor, day, newDepartment, newDoctor, newDetail, newDay);
        return ResponseEntity.ok("Doctor information updated successfully");
    }

    // 删除一个医生的信息
    public ResponseEntity<?> deleteDoctorService(String department, String doctor, String day) {
        doctorMapper.deleteDoctor(department, doctor, day);
        return ResponseEntity.ok("Doctor deleted successfully");
    }

    // 统计指定科室、医生、日期、时间段的预约人数
    public ResponseEntity<?> countAppointmentsService(String department, String doctor, String day, int begin_time, int end_time) {
        int count = registerMapper.countAppointments(department, doctor, day, begin_time, end_time);
        return ResponseEntity.ok(count);
    }

    // 当前系统的预约数量，返回一个整型列表，列表中包含接下来7天的预约数量
    public ResponseEntity<?> countSystemAppointmentsService(String day) {
        List<Integer> counts = new ArrayList<>();
        LocalDate startDate = LocalDate.parse(day);
        
        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            counts.add(registerMapper.countSystemAppointments(formattedDate));
        }
        return ResponseEntity.ok(counts);
    }

    // 获取指定科室的所有医生列表
    public ResponseEntity<?> getDoctorsByDepartmentService(String department) {
        List<doctorSchedule> doctors = doctorMapper.getDoctorsByDepartment(department);
        return ResponseEntity.ok(doctors);
    }

    // 获取指定日期的所有医生排班情况
    public ResponseEntity<?> getDoctorScheduleService(String day) {
        List<Map<String, Object>> schedule = doctorMapper.getDoctorSchedule(day);
        return ResponseEntity.ok(schedule);
    }

    // 获取各科室的预约统计数据
    public ResponseEntity<?> getDepartmentStatsService(String startDate, String endDate) {
        List<Map<String, Object>> stats = registerMapper.getDepartmentStats(startDate, endDate);
        return ResponseEntity.ok(stats);
    }

    // 获取指定时间段内的预约趋势数据
    public ResponseEntity<?> getAppointmentTrendService(String startDate, String endDate) {
        List<Map<String, Object>> trend = registerMapper.getAppointmentTrend(startDate, endDate);
        return ResponseEntity.ok(trend);
    }

    // 获取指定时间段内最热门的5位医生的名称和预约数量列表
    public ResponseEntity<?> getTopDoctorsService(String startDate, String endDate) {
        List<Map<String, Object>> topDoctors = registerMapper.getTopDoctors(startDate, endDate);
        return ResponseEntity.ok(topDoctors);
    }
}

