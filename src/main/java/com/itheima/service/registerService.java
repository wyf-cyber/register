package com.itheima.service;

import com.itheima.mapper.RegisterMapper;
import com.itheima.mapper.DoctorMapper;
import com.itheima.pojo.Appointment;
import com.itheima.pojo.DoctorInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class RegisterService {

    @Autowired
    private RegisterMapper registerMapper;
    @Autowired
    private DoctorMapper doctorMapper;

    // 判断本人是否已经预约该医生
    private boolean inAppointment(String department, String doctor, String username) {
        List<Appointment> appointments = registerMapper.searchMyAppointments(department, doctor, username);
        return !appointments.isEmpty();
    }

    // 添加预约
    public String addAppointmentService(String department, String doctor, String username, String day) {
        // 先刷新医生记录
        doctorMapper.addState(department, doctor, day);
        // 添加预约关系
        // 为了确定挂号的优先级，使用先到先得的原则，即先预约的在前面
        // 将今天已经过去多少秒赋值给time这个整型
        int time = (int) (System.currentTimeMillis() / 1000);
        // 将今天日期使用字符串格式 YYYY-MM-DD 进行存储
        String register_day = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        registerMapper.insertAppointment(department, doctor, username, day, register_day, time);
        return "预约信息已成功添加";
    }

    // 删除预约
    public String deleteAppointmentService(String department, String doctor, String username, String day) {
        // 先刷新医生记录
        doctorMapper.reduceState(department, doctor, day);
        // 删除预约关系
        registerMapper.deleteAppointment(department, doctor, username, day);
        return "预约信息已成功删除";
    }

    // 获取本人预约信息
    public List<Appointment> getMyService(String username) {
        // 获取本人预约的所有医生记录
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
        List<DoctorInfo> tmp = doctorMapper.getAll(day);

        for (DoctorInfo d : tmp) {
            // flag 表示是否已经预约，state 表示医生状态
            boolean flag = inAppointment(d.getDepartment(), d.getDoctor(), username);
            ans.add(new DoctorInfo(d.getDepartment(), d.getDoctor(), d.getDetail(), d.getState(), flag));
        }
        return ans; // Fetch data from database
    }

    public List<DoctorInfo> searchDoctorsService(String department, String state, String username, String day) {
        List<DoctorInfo> tmp;
        if (Objects.equals(state, "全部")){
            tmp = doctorMapper.searchAll(department, day);
        } else {
            tmp = doctorMapper.searchFree(department, day);
        }
        List<DoctorInfo> ans = new ArrayList<>();
        for (DoctorInfo d : tmp) {
            // flag 表示是否已经预约，state 表示医生状态
            boolean flag = inAppointment(d.getDepartment(), d.getDoctor(), username);
            ans.add(new DoctorInfo(d.getDepartment(), d.getDoctor(), d.getDetail(), d.getState(), flag));
        }
        return ans; // Filter data from database
    }
}
