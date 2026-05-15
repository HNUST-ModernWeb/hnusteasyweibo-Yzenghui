package com.example.weibo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.weibo.common.Result;
import com.example.weibo.dto.CommentCreateRequest;
import com.example.weibo.dto.response.CommentResponse;
import com.example.weibo.entity.Comment;
import com.example.weibo.entity.Post;
import com.example.weibo.entity.User;
import com.example.weibo.exception.BusinessException;
import com.example.weibo.service.CommentService;
import com.example.weibo.service.PostService;
import com.example.weibo.service.UserService;
import com.example.weibo.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public Result<Integer> create(@RequestBody CommentCreateRequest request, HttpSession session) {
        Integer userId = SessionUtil.getUserId(session);
        
        Post post = postService.findById(request.getPostId());
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }
        
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new BusinessException("评论内容不能为空");
        }
        
        if (request.getContent().length() > 200) {
            throw new BusinessException("评论内容不能超过200个字符");
        }
        
        Comment comment = new Comment(request.getPostId(), userId, request.getContent().trim());
        commentService.create(comment);
        
        return Result.success("评论成功", comment.getId());
    }

    @GetMapping("/list")
    public Result<IPage<CommentResponse>> list(
            @RequestParam Integer postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Post post = postService.findById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }
        
        IPage<Comment> commentPage = commentService.findByPostId(postId, page, size);
        
        IPage<CommentResponse> responsePage = commentPage.convert(this::convertToCommentResponse);
        
        return Result.success(responsePage);
    }

    @DeleteMapping("/delete/{commentId}")
    public Result<Void> delete(@PathVariable Integer commentId, HttpSession session) {
        Integer userId = SessionUtil.getUserId(session);
        Comment comment = commentService.findById(commentId);
        
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException("无权删除该评论");
        }
        
        commentService.delete(commentId);
        
        return Result.success("删除成功");
    }

    private CommentResponse convertToCommentResponse(Comment comment) {
        User user = userService.findById(comment.getUserId());
        
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setUserId(comment.getUserId());
        response.setUsername(user != null ? user.getUsername() : "未知");
        response.setAvatar(user != null ? user.getAvatar() : null);
        response.setContent(comment.getContent());
        response.setCreateTime(comment.getCreateTime());
        
        return response;
    }
}