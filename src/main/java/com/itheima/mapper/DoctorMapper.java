package com.itheima.mapper;

import com.itheima.pojo.DoctorInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DoctorMapper {
    // 添加一个医生的信息
    @Insert("INSERT INTO doctors (department, doctor, detail, state, work_date) VALUES (#{department}, #{doctor}, #{detail}, 0, #{day})")
    void addDoctor(@Param("department") String department, 
                   @Param("doctor") String doctor, 
                   @Param("detail") String detail, 
                   @Param("state") int state,
                   @Param("day") String day);

    // 删除一个医生的信息
    @Delete("DELETE FROM doctors WHERE department = #{department} AND doctor = #{doctor} AND work_date = #{day}")
    void deleteDoctor(String department, String doctor, String day);

    // 返回所有医生的信息
    @Select("SELECT department, doctor, detail, state FROM doctors WHERE work_date = #{day}")
    List<DoctorInfo> getAll(String day);

    // 返回所有医生在某个科室的信息
    @Select("SELECT department, doctor, detail, state FROM doctors WHERE department = #{department} AND work_date = #{day}")
    List<DoctorInfo> searchAll(String department, String day);

    // 返回所有空闲医生的信息
    @Select("SELECT department, doctor, detail, state FROM doctors WHERE department = #{department} AND work_date = #{day} AND state = 0")
    List<DoctorInfo> searchFree(String department, String day);

    // 增加一个医生的预约人数状态
    @Update("UPDATE doctors SET state = state + 1 WHERE department = #{department} AND doctor = #{doctor} AND work_date = #{day}")
    void addState(String department, String doctor, String day);

    // 减少一个医生的预约人数状态
    @Update("UPDATE doctors SET state = state - 1 WHERE department = #{department} AND doctor = #{doctor} AND work_date = #{day}")
    void reduceState(String department, String doctor, String day);

    // 返回一个医生在某个科室的等待人数
    @Select("SELECT state FROM doctors WHERE department = #{department} AND doctor = #{doctor} AND work_date = #{day}")
    int getState(String department, String doctor, String day);

    // 修改医生的状态
    //@Update("UPDATE doctors SET state = #{newState} WHERE department = #{department} AND doctor = #{doctor}")
    //int setState(String department, String doctor, int newState);

    // 更新医生信息
    @Update("UPDATE doctors SET department = #{newDepartment}, doctor = #{newDoctor}, " +
            "detail = #{newDetail}, work_date = #{day} WHERE department = #{department} AND doctor = #{doctor} " +
            "AND work_date = #{day}")
    void updateDoctor(String department, String doctor, String newDepartment, 
                     String newDoctor, String newDetail, String day);

    // 获取指定科室的所有医生
    @Select("SELECT DISTINCT doctor, detail FROM doctors WHERE department = #{department}")
    List<Map<String, Object>> getDoctorsByDepartment(String department);

    // 获取指定日期的医生排班情况
    @Select("SELECT department, doctor, detail, state, work_date FROM doctors " +
            "WHERE work_date = #{day} ORDER BY department, doctor")
    List<Map<String, Object>> getDoctorSchedule(String day);
}
