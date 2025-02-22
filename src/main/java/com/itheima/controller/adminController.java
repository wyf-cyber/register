package com.itheima.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/admin")
@CrossOrigin
public class adminController {
    @Autowired
    private com.itheima.service.adminService adminService;

    // 添加一个医生的信息
    @GetMapping("/addDoctor")
    public ResponseEntity<?> addDoctor(@RequestParam String department, @RequestParam String doctor, @RequestParam String detail, @RequestParam String day) {
        return adminService.addDoctorService(department, doctor, detail, day);
    }

    // 删除一个医生的信息
    @GetMapping("/deleteDoctor")
    public ResponseEntity<?> deleteDoctor(@RequestParam String department, @RequestParam String doctor, @RequestParam String day) {
        return adminService.deleteDoctorService(department, doctor, day);
    }
}
