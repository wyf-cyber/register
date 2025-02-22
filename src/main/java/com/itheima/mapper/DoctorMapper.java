package com.itheima.mapper;

import com.itheima.pojo.DoctorInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DoctorMapper {
    // 添加一个医生的信息
    @Insert("INSERT INTO doctors (department, doctor, detail, state) VALUES (#{department}, #{doctor}, #{detail}, #{state})")
    void addDoctor(String department, String doctor, String detail, String day);

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
}
