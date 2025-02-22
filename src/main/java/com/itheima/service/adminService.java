package com.itheima.service;

import com.itheima.mapper.DoctorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class adminService {
    @Autowired
    private DoctorMapper doctorMapper;

    // 添加一个医生的信息
    public ResponseEntity<?> addDoctorService(String department, String doctor, String detail, String day) {
        doctorMapper.addDoctor(department, doctor, detail, day);
        return ResponseEntity.ok("Doctor added successfully");
    }

    // 删除一个医生的信息
    public ResponseEntity<?> deleteDoctorService(String department, String doctor, String day) {
        doctorMapper.deleteDoctor(department, doctor, day);
        return ResponseEntity.ok("Doctor deleted successfully");
    }

}

