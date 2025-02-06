package com.hmall.comment.mapper;

import com.hmall.comment.domain.po.Index;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface IndexMapper {
    @Insert("INSERT INTO `index` (id, root, parent, summary, `like`, is_full) " +
            "VALUES (#{id}, #{root}, #{parent}, #{summary}, #{like}, #{isFull})")
    void insert(Index index);

    @Select("SELECT * FROM `index` WHERE root = false AND parent = #{commentId} Order By `like` DESC")
    List<Index> getByParent(Long commentId);

    @Select("SELECT * FROM `index` WHERE root = false AND parent = #{commentId} Order By `like` DESC Limit 1")
    List<Index>  getByParentLimit1(Long commentId);

    // 这里评论表忘记加materialId字段了
    @Select("SELECT * FROM `index` WHERE root = true Order By `like` DESC")
    List<Index> getRoot(Long materialId);


}
