package com.itheima.service;

import com.itheima.Mapper.RegisterMapper;
import com.itheima.pojo.Appointment;
import com.itheima.pojo.DoctorInfo;
import com.itheima.pojo.RegisterInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class registerService {

    @Autowired
    private RegisterMapper registerMapper;
    @Autowired
    private com.itheima.Mapper.doctorMapper doctorMapper;

    private boolean inAppointment(String department, String doctor, String username) {
        List<Appointment> appointments = registerMapper.searchMyAppointments(department, doctor, username);
        return !appointments.isEmpty();
    }

    public String addAppointmentService(String department, String doctor, String username, String day) {
        // 先刷新医生记录
        doctorMapper.addState(department, doctor, day);
        // 添加预约关系
        // 将今天已经过去多少秒赋值给time这个整型
        int time = (int) (System.currentTimeMillis() / 1000);
        registerMapper.insertAppointment(department, doctor, username, day, time);
        return "预约信息已成功添加";
    }

    public String deleteAppointmentService(String department, String doctor, String username, String day) {
        doctorMapper.reduceState(department, doctor, day);
        registerMapper.deleteAppointment(department, doctor, username, day);
        return "预约信息已成功删除";
    }

    public List<Appointment> getMyService(String username) {
        List<Appointment> appointments = registerMapper.getMyAppointments(username);
        for (Appointment appointment : appointments) {
            // 获取预约列表
            List<Appointment> apps = registerMapper.getAppointmentsLength(
                    appointment.getDepartment(),
                    appointment.getDoctor(),
                    appointment.getDay(),
                    appointment.getTime()
            );
            System.out.println(apps);
            // 获取列表长度并设置到 appointment 的 time 属性
            int length = (apps != null) ? apps.size() : 0;
            appointment.setTime(length);
        }
        System.out.println(appointments);
        return appointments != null ? appointments : new ArrayList<>();
    }

    public List<DoctorInfo> getAllDoctorsService(String username, String day) {
        // System.out.println("Fetched appointments: " + doctorMapper.getAll());
        List<DoctorInfo> ans = new ArrayList<>();
        List<RegisterInfo> tmp = doctorMapper.getAll(day);

        for (RegisterInfo d : tmp) {
            boolean flag = inAppointment(d.getDepartment(), d.getDoctor(), username);
            ans.add(new DoctorInfo(d.getDepartment(), d.getDoctor(), d.getDetail(), d.getState(), flag));
        }
        return ans; // Fetch data from database
    }

    public List<DoctorInfo> searchDoctorsService(String department, String state, String username, String day) {
        List<RegisterInfo> tmp;
        if (Objects.equals(state, "全部")){
            tmp = doctorMapper.searchAll(department, day);
        } else {
            tmp = doctorMapper.searchFree(department, day);
        }
        List<DoctorInfo> ans = new ArrayList<>();
        for (RegisterInfo d : tmp) {
            boolean flag = inAppointment(d.getDepartment(), d.getDoctor(), username);
            ans.add(new DoctorInfo(d.getDepartment(), d.getDoctor(), d.getDetail(), d.getState(), flag));
        }
        return ans; // Filter data from database
    }
}
