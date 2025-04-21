package com.itheima.controller;

import com.itheima.pojo.Appointment;
import com.itheima.pojo.Comment;
import com.itheima.pojo.DoctorInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/register")
@CrossOrigin // Support cross-origin requests
public class registerController {

    @Autowired
    private com.itheima.service.registerService registerService;


    @GetMapping("/addApp")
    public String addAppointment(@RequestParam String department, @RequestParam String doctor, @RequestParam String username, @RequestParam String day) {
        return registerService.addAppointmentService(department, doctor, username, day);
    }

    @GetMapping("/cancelApp")
    public String deleteAppointment(@RequestParam String department, @RequestParam String doctor, @RequestParam String username, @RequestParam String day) {
        return registerService.deleteAppointmentService(department, doctor, username, day);
    }

    @GetMapping("/getMy")
    public List<Appointment> getMy(@RequestParam String username) {
        return registerService.getMyService(username);
    }

    @GetMapping("/getAllDoctors")
    public List<DoctorInfo> getAllDoctors(@RequestParam String username, @RequestParam String day) {
        return registerService.getAllDoctorsService(username, day);
    }

    @GetMapping("/searchDoctors")
    public List<DoctorInfo> searchDoctors(@RequestParam String department, @RequestParam String state, @RequestParam String username, @RequestParam String day) {
        return registerService.searchDoctorsService(department, state, username, day);
    }

    @GetMapping("/addComment")
    public String addComment(@RequestParam String doctor, @RequestParam String content, @RequestParam int rating) {
        return registerService.addCommentService(doctor, content, rating);
    }

    @GetMapping("/getAllComments")
    public List<Comment> getAllComments() {
        return registerService.getAllCommentsService();
    }

    @GetMapping("/getCommentsByDoctor")
    public List<Comment> getCommentsByDoctor(@RequestParam String doctor) {
        return registerService.getCommentsByDoctorService(doctor);
    }
}