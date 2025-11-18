package com.dying.service;

import com.dying.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dying.domain.UserVo;
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

    User userLogin(String userAccount, String password, HttpServletRequest request,Double latitude, Double longitude);


    User getSaftyUser(User originUser);

    int userLogout(HttpServletRequest request);

    boolean userUpdate(User user);

    List<User> searchAllByTags(List<String> tagsList);

    boolean checkEmail(String email);

    List<User> backLike(User loginUser,Integer count);

    List<UserVo> getNearUser(Long userId);

    /**
     * 根据标签分页查询用户
     * @param tagsList 标签列表
     * @param currentPage 当前页码
     * @param pageSize 每页大小
     * @return 分页用户列表
     */
    IPage<User> searchUsersByTagsWithPagination(List<String> tagsList, long currentPage, long pageSize);
}
