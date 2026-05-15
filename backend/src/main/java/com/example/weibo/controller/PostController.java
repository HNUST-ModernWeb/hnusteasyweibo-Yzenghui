package com.example.weibo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.weibo.common.Result;
import com.example.weibo.dto.PostCreateRequest;
import com.example.weibo.dto.response.PostResponse;
import com.example.weibo.entity.Post;
import com.example.weibo.entity.User;
import com.example.weibo.exception.BusinessException;
import com.example.weibo.service.CommentService;
import com.example.weibo.service.PostLikeService;
import com.example.weibo.service.PostService;
import com.example.weibo.service.UserService;
import com.example.weibo.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostLikeService postLikeService;

    @PostMapping("/create")
    public Result<Integer> create(@RequestBody PostCreateRequest request, HttpSession session) {
        Integer userId = SessionUtil.getUserId(session);
        
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new BusinessException("内容不能为空");
        }
        
        if (request.getContent().length() > 200) {
            throw new BusinessException("内容不能超过200个字符");
        }
        
        Post post = new Post(userId, request.getContent().trim());
        postService.create(post);
        
        return Result.success("发布成功", post.getId());
    }

    @GetMapping("/list")
    public Result<IPage<PostResponse>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session) {
        
        Integer userId = SessionUtil.getUserId(session);
        IPage<Post> postPage = postService.findAllByPage(page, size);
        
        IPage<PostResponse> responsePage = postPage.convert(post -> convertToPostResponse(post, userId));
        
        return Result.success(responsePage);
    }

    @GetMapping("/detail/{postId}")
    public Result<PostResponse> detail(@PathVariable Integer postId, HttpSession session) {
        Post post = postService.findById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }
        
        Integer userId = SessionUtil.getUserId(session);
        PostResponse response = convertToPostResponse(post, userId);
        
        return Result.success(response);
    }

    @DeleteMapping("/delete/{postId}")
    public Result<Void> delete(@PathVariable Integer postId, HttpSession session) {
        Integer userId = SessionUtil.getUserId(session);
        Post post = postService.findById(postId);
        
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }
        
        if (!post.getUserId().equals(userId)) {
            throw new BusinessException("无权删除该帖子");
        }
        
        postService.delete(postId);
        
        return Result.success("删除成功");
    }

    @PostMapping("/like/{postId}")
    public Result<Map<String, Object>> like(@PathVariable Integer postId, HttpSession session) {
        Integer userId = SessionUtil.getUserId(session);
        
        Post post = postService.findById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }
        
        boolean liked = postLikeService.toggleLike(postId, userId);
        Post updatedPost = postService.findById(postId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        result.put("likeCount", updatedPost.getLikeCount());
        
        return Result.success(result);
    }

    @GetMapping("/user/{userId}")
    public Result<IPage<PostResponse>> getUserPosts(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session) {
        
        User user = userService.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        Integer currentUserId = SessionUtil.getUserId(session);
        IPage<Post> postPage = postService.findByUserId(userId, page, size);
        
        IPage<PostResponse> responsePage = postPage.convert(post -> convertToPostResponse(post, currentUserId));
        
        return Result.success(responsePage);
    }

    private PostResponse convertToPostResponse(Post post, Integer userId) {
        User user = userService.findById(post.getUserId());
        Integer commentCount = (int) commentService.countByPostId(post.getId());
        Boolean liked = userId != null ? postLikeService.isLiked(post.getId(), userId) : false;
        
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setUserId(post.getUserId());
        response.setUsername(user != null ? user.getUsername() : "未知");
        response.setAvatar(user != null ? user.getAvatar() : null);
        response.setContent(post.getContent());
        response.setLikeCount(post.getLikeCount());
        response.setCommentCount(commentCount);
        response.setLiked(liked);
        response.setCreateTime(post.getCreateTime());
        
        return response;
    }
}