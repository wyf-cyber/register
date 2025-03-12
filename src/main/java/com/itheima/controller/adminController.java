package com.itheima.controller;

import com.itheima.service.AdminService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdminController {
    @Autowired
    private AdminService adminService;

    // 添加一个医生的信息
    @GetMapping("/addDoctor")
    public ResponseEntity<?> addDoctor(@RequestParam String department, @RequestParam String doctor, @RequestParam String detail, @RequestParam String day) {
        return adminService.addDoctorService(department, doctor, detail, day);
    }

    // 修改一个医生的信息
    @GetMapping("/updateDoctor")
    public ResponseEntity<?> updateDoctor(@RequestParam String department, @RequestParam String doctor, @RequestParam String newDepartment, @RequestParam String newDoctor, @RequestParam String newDetail, @RequestParam String day) {
        return adminService.updateDoctorService(department, doctor, newDepartment, newDoctor, newDetail, day);
    }

    // 删除一个医生的信息
    @GetMapping("/deleteDoctor")
    public ResponseEntity<?> deleteDoctor(@RequestParam String department, @RequestParam String doctor, @RequestParam String day) {
        return adminService.deleteDoctorService(department, doctor, day);
    }

    // 统计指定科室、医生、日期、时间段的预约人数
    @GetMapping("/countAppointments")
    public ResponseEntity<?> countAppointments(@RequestParam String department, @RequestParam String doctor, @RequestParam String day, @RequestParam int begin_time, @RequestParam int end_time) {
        return adminService.countAppointmentsService(department, doctor, day, begin_time, end_time);
    }

    // 获取指定科室的所有医生
    @GetMapping("/getDoctorsByDepartment")
    public ResponseEntity<?> getDoctorsByDepartment(@RequestParam String department) {
        return adminService.getDoctorsByDepartmentService(department);
    }

    // 获取指定日期的医生排班情况
    @GetMapping("/getDoctorSchedule")
    public ResponseEntity<?> getDoctorSchedule(@RequestParam String day) {
        return adminService.getDoctorScheduleService(day);
    }

    // 获取各科室的预约统计数据
    @GetMapping("/getDepartmentStats")
    public ResponseEntity<?> getDepartmentStats(@RequestParam String startDate, @RequestParam String endDate) {
        return adminService.getDepartmentStatsService(startDate, endDate);
    }

    // 获取指定时间段内的预约趋势数据
    @GetMapping("/getAppointmentTrend")
    public ResponseEntity<?> getAppointmentTrend(@RequestParam String startDate, @RequestParam String endDate) {
        return adminService.getAppointmentTrendService(startDate, endDate);
    }
}
