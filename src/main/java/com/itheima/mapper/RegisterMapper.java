package com.itheima.mapper;

import com.itheima.pojo.Appointment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RegisterMapper {

    // 插入预约
    @Insert("INSERT INTO appointment (department, doctor, username, day, time) VALUES (#{department}, #{doctor}, #{username}, #{day}, #{time})")
    int insertAppointment(String department, String doctor, String username, String day, int time);

    // 获取指定用户的所有预约
    @Select("SELECT * FROM appointment WHERE username = #{username}")
    List<Appointment> getMyAppointments(String username);

    // 获取满足指定科室、医生、日期、时间段的预约
    @Select("SELECT * FROM appointment WHERE department = #{department} AND doctor = #{doctor} AND day = #{day} AND time < #{time}")
    List<Appointment> getAppointmentsLength(String department, String doctor, String day, int time);

    // 删除指定预约
    @Delete("DELETE FROM appointment WHERE username = #{username} AND department = #{department} AND doctor = #{doctor} AND day = #{day}")
    void deleteAppointment(String department, String doctor, String username, String day);

    // 删除所有预约
    @Delete("DELETE FROM appointment WHERE username = #{username}")
    void deleteAppointments(String username);

    // 获取指定用户的指定科室、医生、日期的预约
    @Select("SELECT * FROM appointment WHERE username = #{username} AND department = #{department} AND doctor = #{doctor}")
    List<Appointment> searchMyAppointments(String department, String doctor, String username);

    // 更新指定用户的预约
    @Update("UPDATE appointment SET username = #{new_username} WHERE username = #{username}")
    void updateUsername(String username, String new_username);
}