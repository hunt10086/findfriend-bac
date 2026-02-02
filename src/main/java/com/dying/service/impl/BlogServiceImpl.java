package com.dying.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dying.common.ErrorCode;
import com.dying.domain.po.Blog;
import com.dying.domain.vo.BlogVO;
import com.dying.domain.po.User;
import com.dying.domain.request.BlogRequest;
import com.dying.exception.BusinessException;
import com.dying.exception.ThrowUtils;
import com.dying.mapper.UserMapper;
import com.dying.service.BlogService;
import com.dying.mapper.BlogMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.dying.constant.BlogConstant.*;
import static com.dying.constant.RedisConstant.BLOG_LIKE;

/**
 * @author daylight
 * @description 针对表【blog(博客文章)】的数据库操作Service实现
 * @createDate 2025-07-17 15:21:54
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog>
        implements BlogService {

    @Resource
    private BlogMapper blogMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserMapper userMapper;

    @Override
    public boolean createBlog(BlogRequest blogRequest, User loginUser) {
        // 1.检验返回值为空  是否登录
        if (loginUser == null || loginUser.getId() == null || blogRequest == null || loginUser.getId() < 0) {
            return false;
        }
        // 2. 标题，文章不能为空
        if (StringUtils.isBlank(blogRequest.getTitle()) || StringUtils.isBlank(blogRequest.getPassage())) {
            return false;
        }
        // 3.文章 标题 种类 符合规范
        if (!StringUtils.isBlank(blogRequest.getKind()) && blogRequest.getKind().length() > MAX_KIND_LENGTH) {
            return false;
        }
        if (blogRequest.getTitle().length() > MAX_TITLE_LENGTH || blogRequest.getPassage().length() > MAX_PASSAGE_LENGTH) {
            return false;
        }
        int status = blogRequest.getStatus();
        Blog blog1 = new Blog();
        //1为草稿暂存
        if (status == 1) {
            blog1.setStatus(1);
        } else {
            blog1.setStatus(0);
        }
        if(blogRequest.getId()!=null){
            Blog updateBlog = blogMapper.selectById(blogRequest.getId());
            ThrowUtils.throwIf(updateBlog==null,ErrorCode.PARAMS_ERROR,"参数错误");
            updateBlog.setTitle(blogRequest.getTitle());
            updateBlog.setPassage(blogRequest.getPassage());
            updateBlog.setStatus(status);
            updateBlog.setKind(blogRequest.getKind());
            updateBlog.setUpdateTime(new Date());
            blogMapper.updateById(updateBlog);
            return true;
        }
        blog1.setTitle(blogRequest.getTitle());
        blog1.setKind(blogRequest.getKind());
        blog1.setPassage(blogRequest.getPassage());
        blog1.setUserId(loginUser.getId());
        blog1.setCreateTime(new Date());
        blog1.setUpdateTime(new Date());
        blog1.setPraise(0);
        blogMapper.insert(blog1);
        return true;
    }

    @Override
    public boolean updateBlog(BlogRequest blogRequest, User loginUser, Long id) {
        Blog blog1 = blogMapper.selectById(id);
        if (blog1 == null) {
            return false;
        }
        // 1.检验返回值为空  是否登录
        if (loginUser == null || loginUser.getId() == null || blogRequest == null || loginUser.getId() < 0) {
            return false;
        }
        // 2. 标题，文章不能为空
        if (StringUtils.isBlank(blogRequest.getTitle()) || StringUtils.isBlank(blogRequest.getPassage())) {
            return false;
        }
        // 3.文章 标题 种类 符合规范
        if (!StringUtils.isBlank(blogRequest.getKind()) && blogRequest.getKind().length() > MAX_KIND_LENGTH) {
            return false;
        }
        blog1.setTitle(blogRequest.getTitle());
        blog1.setKind(blogRequest.getKind());
        blog1.setPassage(blogRequest.getPassage());
        blog1.setUpdateTime(new Date());
        blogMapper.updateById(blog1);
        return true;
    }

    @Override
    public IPage<BlogVO> getBlogList(User loginUser, long currentPage) {
        if (loginUser == null || loginUser.getId() == null || loginUser.getId() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未登录");
        }
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status",0).orderByDesc("praise");

        // 创建分页对象，每页固定8篇
        Page<Blog> page = new Page<>(currentPage, 8);
        IPage<Blog> blogPage = blogMapper.selectPage(page, queryWrapper);

        // 转换为BlogVO并添加头像信息
        List<BlogVO> blogVOList = new ArrayList<>();
        for (Blog blog : blogPage.getRecords()) {
            User user = userMapper.selectById(blog.getUserId());
            BlogVO blogVO = new BlogVO();
            BeanUtils.copyProperties(blog, blogVO);
            blogVO.setAvatarUrl(user.getAvatarUrl());
            blogVOList.add(blogVO);
        }

        // 创建返回的分页对象
        Page<BlogVO> resultPage = new Page<>();
        resultPage.setCurrent(blogPage.getCurrent());
        resultPage.setSize(blogPage.getSize());
        resultPage.setTotal(blogPage.getTotal());
        resultPage.setRecords(blogVOList);

        return resultPage;
    }

    @Override
    public boolean deleteBlog(User loginUser, Long id) {
        if (loginUser == null || loginUser.getId() == null || loginUser.getId() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未登录");
        }
        if (id == null || id < 0) {
            return false;
        }
        Blog blog = blogMapper.selectById(id);
        if (!Objects.equals(blog.getUserId(), loginUser.getId())) {
            return false;
        }
        blogMapper.deleteById(id);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean like(Long blogId, Long userId) {
        if (blogId == null || userId == null || userId < 0 || blogId < 0) {
            return false;
        }
        Blog blog = blogMapper.selectById(blogId);
        if (blog == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "博客不存在或被删除");
        }
        String key = BLOG_LIKE + blogId + ":";
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(key, userId + "");
        if (BooleanUtil.isTrue(isMember)) {
            stringRedisTemplate.opsForSet().remove(key, userId + "");
            blog.setPraise(blog.getPraise() - 1);
        } else {
            stringRedisTemplate.opsForSet().add(key, userId + "");
            blog.setPraise(blog.getPraise() + 1);
        }
        return blogMapper.updateById(blog) > 0;
    }

    @Override
    public boolean isLike(Long blogId, Long userId) {
        if (blogId == null || userId == null || userId < 0 || blogId < 0) {
            return false;
        }
        Blog blog = blogMapper.selectById(blogId);
        if (blog == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "博客不存在或被删除");
        }
        String key = BLOG_LIKE + blogId + ":";
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(key, userId + "");
        return BooleanUtil.isTrue(isMember);

    }

    @Override
    public Blog getBlog(Long blogId, Long userId) {
        if (blogId == null || userId == null || userId < 0 || blogId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求失败");
        }

        Blog one = this.lambdaQuery().eq(Blog::getId, blogId).eq(Blog::getStatus, 0).one();
        ThrowUtils.throwIf(one==null,ErrorCode.PARAMS_ERROR,"错误,未找到资源");
        return one;
    }

    @Override
    public IPage<BlogVO> getMyBlog(Long userId, long currentPage) {
        if (userId == null || userId < 0) {
            return null;
        }
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);

        // 创建分页对象，每页固定8篇
        Page<Blog> page = new Page<>(currentPage, 8);
        IPage<Blog> blogPage = blogMapper.selectPage(page, queryWrapper);

        // 转换为BlogVO并添加头像信息
        List<BlogVO> blogVOList = new ArrayList<>();
        for (Blog blog : blogPage.getRecords()) {
            User user = userMapper.selectById(blog.getUserId());
            BlogVO blogVO = new BlogVO();
            BeanUtils.copyProperties(blog, blogVO);
            blogVO.setAvatarUrl(user.getAvatarUrl());
            blogVOList.add(blogVO);
        }

        // 创建返回的分页对象
        Page<BlogVO> resultPage = new Page<>();
        resultPage.setCurrent(blogPage.getCurrent());
        resultPage.setSize(blogPage.getSize());
        resultPage.setTotal(blogPage.getTotal());
        resultPage.setRecords(blogVOList);

        return resultPage;
    }

}




