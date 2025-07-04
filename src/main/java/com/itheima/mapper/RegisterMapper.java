package com.itheima.mapper;

import com.itheima.pojo.Appointment;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;
@Mapper
public interface RegisterMapper {
    /*
     * CREATE TABLE appointment (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,  
        department VARCHAR(255) NOT NULL,         
        doctor VARCHAR(255) NOT NULL,
        username VARCHAR(100) NOT NULL,          -- 用户名，长度限制为100字符 
        day VARCHAR(100) NOT NULL,               -- 预约问诊的日期
        register_day VARCHAR(100) NOT NULL,      -- 预约挂号的日期
        time INT NOT NULL                        -- 预约挂号的时间段，使用整数表示，标识优先级
      );
     */

    // 插入预约
    @Insert("INSERT INTO appointment (department, doctor, username, day, register_day, time) VALUES (#{department}, #{doctor}, #{username}, #{day}, #{register_day}, #{time})")
    int insertAppointment(String department, String doctor, String username, String day, String register_day, int time);

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
    @Select("SELECT * FROM appointment WHERE username = #{username} AND department = #{department} AND doctor = #{doctor} AND day = #{day}")
    List<Appointment> searchMyAppointments(String department, String doctor, String username, String day);

    // 更新指定用户的预约
    @Update("UPDATE appointment SET username = #{new_username} WHERE username = #{username}")
    void updateUsername(String username, String new_username);

    // 获取用户过往所有的预约记录
    @Select("SELECT * FROM appointment WHERE username = #{username}")
    List<Appointment> getUserAppointmentHistory(String username);

    // 统计指定条件下的预约人数
    @Select("SELECT COUNT(*) FROM appointment WHERE department = #{department} " +
        "AND doctor = #{doctor} AND day = #{day} " +
        "AND time BETWEEN #{begin_time} AND #{end_time}")
    int countAppointments(String department, String doctor, String day, 
                         int begin_time, int end_time);

    // 统计指定日期的系统总预约数
    @Select("SELECT COUNT(*) FROM appointment WHERE day = #{day}")
    int countSystemAppointments(String day);

    // 获取各科室在指定时间段内的预约统计
    @Select("SELECT department, COUNT(*) as count FROM appointment " +
        "WHERE day BETWEEN #{startDate} AND #{endDate} " +
        "GROUP BY department ORDER BY count DESC")
    List<Map<String, Object>> getDepartmentStats(String startDate, String endDate);

    // 获取指定时间段内的每日预约趋势
    @Select("SELECT day, COUNT(*) as count FROM appointment " +
        "WHERE day BETWEEN #{startDate} AND #{endDate} " +
        "GROUP BY day ORDER BY day")
    List<Map<String, Object>> getAppointmentTrend(String startDate, String endDate);

    // 获取指定时间段内预约人数最多的5位医生
    @Select("SELECT doctor, COUNT(*) as count FROM appointment " +
        "WHERE day BETWEEN #{startDate} AND #{endDate} " +
        "GROUP BY doctor ORDER BY count DESC LIMIT 5")
    List<Map<String, Object>> getTopDoctors(String startDate, String endDate);
}