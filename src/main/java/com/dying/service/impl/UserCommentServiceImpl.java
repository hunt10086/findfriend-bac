package com.dying.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dying.common.ErrorCode;
import com.dying.domain.vo.CommentVO;
import com.dying.domain.po.User;
import com.dying.domain.po.UserComment;
import com.dying.exception.BusinessException;
import com.dying.mapper.UserMapper;
import com.dying.service.UserCommentService;
import com.dying.mapper.UserCommentMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* @author 666
* @description 针对表【user_comment(用户评论)】的数据库操作Service实现
* @createDate 2025-07-18 11:30:24
*/
@Service
public class UserCommentServiceImpl extends ServiceImpl<UserCommentMapper, UserComment>
    implements UserCommentService{

    @Resource
    private UserCommentMapper userCommentMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public UserComment createComment(String comment, Long blogId, Long userId){
        if(blogId==null||userId==null||userId<0||blogId<0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求失败");
        }
        if(StringUtils.isBlank(comment)) {
            return null;
        }
        UserComment userComment = new UserComment();
        userComment.setCreateTime(new Date());
        userComment.setUserId(userId);
        userComment.setBlogId(blogId);
        userComment.setContent(comment);
        userCommentMapper.insert(userComment);
        return userComment;
    }

    @Override
    public List<CommentVO> getAllComments(Long blogId){
        if(blogId==null||blogId<0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<UserComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("blog_id", blogId);
        queryWrapper.orderByDesc("create_time");
        List<CommentVO> list=new ArrayList<>();
        List<UserComment> userComments = userCommentMapper.selectList(queryWrapper);
        for(UserComment userComment : userComments) {
            User user=userMapper.selectById(userComment.getUserId());
            // 跳过已被删除的用户
            if (user == null) {
                continue;
            }
            CommentVO commentVO=new CommentVO();
            commentVO.setUserName(user.getUserName());
            commentVO.setAvatarUrl(user.getAvatarUrl());
            commentVO.setCreateTime(userComment.getCreateTime());
            commentVO.setContent(userComment.getContent());
            list.add(commentVO);
        }
        return list;
    }
}




