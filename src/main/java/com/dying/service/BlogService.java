package com.dying.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dying.domain.po.Blog;
import com.dying.domain.vo.BlogVO;
import com.dying.domain.vo.UserVO;
import com.dying.domain.request.BlogRequest;

import java.util.List;

/**
* @author 666
* @description 针对表【blog(博客文章)】的数据库操作Service
* @createDate 2025-07-17 15:21:54
*/
public interface BlogService extends IService<Blog> {

    boolean createBlog(BlogRequest blog, UserVO login);

    boolean updateBlog(BlogRequest blog, UserVO loginUser, Long id);

    IPage<BlogVO> getBlogList(UserVO loginUser, long currentPage);

    boolean deleteBlog(UserVO loginUser, Long id);

    boolean like(Long blogId, Long userId);

    boolean isLike(Long blogId, Long userId);

    Blog getBlog(Long blogId, Long userId);

    IPage<BlogVO> getMyBlog(Long userId, long currentPage);
}
