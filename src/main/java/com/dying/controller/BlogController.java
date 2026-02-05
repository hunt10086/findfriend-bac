package com.dying.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dying.common.BaseResponse;
import com.dying.common.ErrorCode;
import com.dying.common.ResultUtils;
import com.dying.domain.po.Blog;
import com.dying.domain.vo.BlogVO;
import com.dying.domain.vo.UserVO;
import com.dying.domain.request.BlogRequest;
import com.dying.exception.BusinessException;
import com.dying.service.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.dying.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @Author daylight
 * @Date 2025/7/17 15:34
 */

@Slf4j
@RequestMapping("/blog")
@RestController()
public class BlogController {
    @Resource
    private BlogService blogService;

    @Operation(summary = "创建博客")
    @PostMapping("/create")
    public BaseResponse<String> createBlog(@RequestBody BlogRequest blog, HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO currentUser = (UserVO) attribute;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        if (blog == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "发表内容为空");
        }
        Integer status=blog.getStatus();
        if(status==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数错误");
        }
        if(status!=0&&status!=1){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数错误");
        }
        if (blogService.createBlog(blog, currentUser)) {
            return ResultUtils.success("success");
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建失败");
        }
    }

    @Operation(summary = "更新博客")
    @PostMapping("/update")
    public BaseResponse<String> updateBlog(@RequestBody BlogRequest blog, HttpServletRequest request, Long id) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO currentUser = (UserVO) attribute;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        if (blog == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新不合法");
        }
        if (blogService.updateBlog(blog, currentUser, id)) {
            return ResultUtils.success("success");
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新失败");
        }
    }

    @Operation(summary = "获取博客列表")
    @GetMapping("/list")
    public BaseResponse<IPage<BlogVO>> getBlogList(HttpServletRequest request, Long currentPage) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO currentUser = (UserVO) attribute;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        if(currentPage == null || currentPage < 1) {
            // 默认第一页
            currentPage = 1L;
        }
        return ResultUtils.success(blogService.getBlogList(currentUser, currentPage));
    }

    @Operation(summary = "删除博客")
    @GetMapping("/delete")
    public BaseResponse<Boolean> deleteBlog(HttpServletRequest request, Long id) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO currentUser = (UserVO) attribute;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        if (blogService.deleteBlog(currentUser, id)) {
            return ResultUtils.success(true);
        } else {
            return ResultUtils.error(ErrorCode.NO_AUTO);
        }
    }

    @Operation(summary = "点赞博客")
    @GetMapping("like")
    public BaseResponse<Boolean> getBlogLike(Long blogId, HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO currentUser = (UserVO) attribute;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        Long userId = currentUser.getId();
        if (blogId == null || userId == null || userId < 0 || blogId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不正确");
        }
        return ResultUtils.success(blogService.like(blogId, userId));
    }

    @Operation(summary = "判断博客是否点赞")
    @GetMapping("isLike")
    public BaseResponse<Boolean> getBlogIsLike(Long blogId, HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO currentUser = (UserVO) attribute;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        Long userId = currentUser.getId();
        if (blogId == null || userId == null || userId < 0 || blogId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不正确");
        }
        return ResultUtils.success(blogService.isLike(blogId, userId));
    }


    @Operation(summary = "获取单个博客")
    @GetMapping("/getOne")
    public BaseResponse<Blog> getBlog(Long blogId, HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO currentUser = (UserVO) attribute;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        Long userId = currentUser.getId();
        if (blogId == null || userId == null || userId < 0 || blogId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不正确");
        }
        return ResultUtils.success(blogService.getBlog(blogId, userId));
    }

    @Operation(summary = "获取我的博客列表")
    @GetMapping("/my/list")
    public BaseResponse<IPage<BlogVO>> getMyBlogList(HttpServletRequest request, Long currentPage) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO currentUser = (UserVO) attribute;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        if(currentPage == null || currentPage < 1) {
            currentPage = 1L; // 默认第一页
        }
        com.baomidou.mybatisplus.core.metadata.IPage<BlogVO> list = blogService.getMyBlog(currentUser.getId(), currentPage);
        if(list == null || list.getRecords().isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"没有发布过任何内容");
        }
        return ResultUtils.success(list);
    }

}
