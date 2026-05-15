
package com.example.weibo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.weibo.entity.Comment;
import com.example.weibo.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    public Comment create(Comment comment) {
        commentMapper.insert(comment);
        return comment;
    }

    public Comment findById(Integer id) {
        return commentMapper.selectById(id);
    }

    public IPage<Comment> findByPostId(Integer postId, int page, int size) {
        Page<Comment> pageParam = new Page<>(page, size);
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("post_id", postId).orderByDesc("create_time");
        return commentMapper.selectPage(pageParam, wrapper);
    }

    public boolean update(Comment comment) {
        return commentMapper.updateById(comment) > 0;
    }

    public boolean delete(Integer id) {
        return commentMapper.deleteById(id) > 0;
    }

    public long countByPostId(Integer postId) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("post_id", postId);
        return commentMapper.selectCount(wrapper);
    }
}
