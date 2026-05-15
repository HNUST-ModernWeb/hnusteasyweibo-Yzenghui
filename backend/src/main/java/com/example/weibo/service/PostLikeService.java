
package com.example.weibo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.weibo.entity.PostLike;
import com.example.weibo.mapper.PostLikeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostLikeService {

    @Autowired
    private PostLikeMapper postLikeMapper;

    @Autowired
    private PostService postService;

    public boolean isLiked(Integer postId, Integer userId) {
        QueryWrapper<PostLike> wrapper = new QueryWrapper<>();
        wrapper.eq("post_id", postId).eq("user_id", userId);
        return postLikeMapper.selectOne(wrapper) != null;
    }

    @Transactional
    public boolean toggleLike(Integer postId, Integer userId) {
        QueryWrapper<PostLike> wrapper = new QueryWrapper<>();
        wrapper.eq("post_id", postId).eq("user_id", userId);
        PostLike existing = postLikeMapper.selectOne(wrapper);

        if (existing != null) {
            postLikeMapper.deleteById(existing.getId());
            postService.updateLikeCount(postId, -1);
            return false;
        } else {
            PostLike postLike = new PostLike(postId, userId);
            postLikeMapper.insert(postLike);
            postService.updateLikeCount(postId, 1);
            return true;
        }
    }
}
