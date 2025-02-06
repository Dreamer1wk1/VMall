package com.hmall.comment.mapper;

import com.hmall.comment.domain.po.Content;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ContentMapper {
    @Insert("insert into content(comment_id, content) values(#{commentId}, #{content})")
    void insert(Content content);
    @Select("SELECT * FROM content WHERE comment_id = #{commentId}")
    Content getById(Long commentId);
}
