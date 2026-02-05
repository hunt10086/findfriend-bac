package com.dying.service;

import com.dying.domain.po.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dying.domain.request.UserUpdateRequest;
import com.dying.domain.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
* @author 666
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2025-05-10 13:59:54
*/
public interface UserService extends IService<User> {

    long userRegister(String userAccount, String password,String checkPassword,String email);

    UserVO userLogin(String userAccount, String password, HttpServletRequest request);

    UserVO getSafetyUser(User originUser);

    int userLogout(HttpServletRequest request);

    boolean userUpdate(Long userId,UserUpdateRequest userUpdateRequest);

    boolean checkEmail(String email);

    List<UserVO> backLike(UserVO loginUser,Integer count);

    List<UserVO> getNearUser(Long userId);

    /**
     * 根据标签分页查询用户
     * @param tagsList 标签列表
     * @param currentPage 当前页码
     * @param pageSize 每页大小
     * @return 分页用户列表
     */
    IPage<UserVO> searchUsersByTagsWithPagination(List<String> tagsList, long currentPage, long pageSize);
}
