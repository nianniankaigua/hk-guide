package com.hkguide.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hkguide.dto.CommentView;
import com.hkguide.dto.CreateCommentRequest;
import com.hkguide.dto.CreatePostRequest;
import com.hkguide.dto.PostDetail;
import com.hkguide.dto.PostSummary;
import com.hkguide.dto.PostView;
import com.hkguide.entity.Comment;
import com.hkguide.entity.Post;
import com.hkguide.entity.User;
import com.hkguide.exception.BizException;
import com.hkguide.exception.NotFoundException;
import com.hkguide.mapper.CommentMapper;
import com.hkguide.mapper.PostMapper;
import com.hkguide.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ForumService {

    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final UserMapper userMapper;

    public ForumService(PostMapper postMapper, CommentMapper commentMapper, UserMapper userMapper) {
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
        this.userMapper = userMapper;
    }

    public List<PostSummary> listPosts() {
        List<Post> posts = postMapper.selectList(new LambdaQueryWrapper<Post>()
                .orderByDesc(Post::getCreatedAt));
        Map<Long, User> userMap = userMapOf(posts.stream().map(Post::getUserId).toList());
        return posts.stream()
                .map(post -> new PostSummary(
                        post.getId(),
                        post.getTitle(),
                        excerpt(post.getContent()),
                        authorOf(userMap.get(post.getUserId())),
                        post.getCreatedAt(),
                        commentCount(post.getId())))
                .toList();
    }

    public PostDetail getPost(Long id) {
        Post post = requirePost(id);
        List<Comment> comments = commentMapper.selectList(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getPostId, id)
                .orderByAsc(Comment::getCreatedAt));
        Map<Long, User> userMap = userMapOf(comments.stream().map(Comment::getUserId).toList());
        List<CommentView> commentViews = comments.stream()
                .map(c -> new CommentView(c.getId(), authorOf(userMap.get(c.getUserId())), c.getContent(), c.getCreatedAt()))
                .toList();
        User author = userMapper.selectById(post.getUserId());
        return new PostDetail(post.getId(), post.getTitle(), post.getContent(),
                authorOf(author), post.getCreatedAt(), commentViews);
    }

    public PostView createPost(String username, CreatePostRequest request) {
        User user = requireUser(username);
        Post post = new Post();
        post.setUserId(user.getId());
        post.setTitle(request.title());
        post.setContent(request.content());
        post.setCreatedAt(LocalDateTime.now());
        postMapper.insert(post);
        return new PostView(post.getId(), post.getTitle(), post.getContent(),
                authorOf(user), post.getCreatedAt());
    }

    public CommentView addComment(String username, Long postId, CreateCommentRequest request) {
        User user = requireUser(username);
        requirePost(postId);
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(user.getId());
        comment.setContent(request.content());
        comment.setCreatedAt(LocalDateTime.now());
        commentMapper.insert(comment);
        return new CommentView(comment.getId(), authorOf(user), comment.getContent(), comment.getCreatedAt());
    }

    private Post requirePost(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new NotFoundException("帖子不存在");
        }
        return post;
    }

    private User requireUser(String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        if (user == null) {
            throw new BizException("用户不存在");
        }
        return user;
    }

    private long commentCount(Long postId) {
        Long count = commentMapper.selectCount(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getPostId, postId));
        return count == null ? 0 : count;
    }

    private Map<Long, User> userMapOf(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return userMapper.selectByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }

    private String authorOf(User user) {
        if (user == null) {
            return "未知用户";
        }
        return user.getNickname() == null || user.getNickname().isBlank()
                ? user.getUsername() : user.getNickname();
    }

    private String excerpt(String content) {
        if (content == null) {
            return "";
        }
        return content.length() <= 80 ? content : content.substring(0, 80);
    }
}
