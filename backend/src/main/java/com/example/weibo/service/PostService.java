
package com.example.weibo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.weibo.entity.Post;
import com.example.weibo.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    @Autowired
    private PostMapper postMapper;

    public Post create(Post post) {
        postMapper.insert(post);
        return post;
    }

    public Post findById(Integer id) {
        return postMapper.selectById(id);
    }

    public IPage<Post> findAllByPage(int page, int size) {
        Page<Post> pageParam = new Page<>(page, size);
        QueryWrapper<Post> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        return postMapper.selectPage(pageParam, wrapper);
    }

    public IPage<Post> findByUserId(Integer userId, int page, int size) {
        Page<Post> pageParam = new Page<>(page, size);
        QueryWrapper<Post> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).orderByDesc("create_time");
        return postMapper.selectPage(pageParam, wrapper);
    }

    public boolean update(Post post) {
        return postMapper.updateById(post) > 0;
    }

    public boolean updateLikeCount(Integer postId, int delta) {
        Post post = findById(postId);
        if (post != null) {
            post.setLikeCount(post.getLikeCount() + delta);
            return update(post);
        }
        return false;
    }

    public boolean delete(Integer id) {
        return postMapper.deleteById(id) > 0;
    }
}
