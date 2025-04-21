package com.itheima.mapper;

import com.itheima.pojo.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CommentMapper {
    // 添加评论
    @Insert("INSERT INTO comments (doctor, content, rating) VALUES (#{doctor}, #{content}, #{rating})")
    void addComment(@Param("doctor") String doctor, @Param("content") String content, @Param("rating") int rating);

    // 获取所有评论
    @Select("SELECT * FROM comments")
    List<Comment> getAllComments();

    // 获取某个医生的所有评论
    @Select("SELECT * FROM comments WHERE doctor = #{doctor}")
    List<Comment> getCommentsByDoctor(@Param("doctor") String doctor);

}
