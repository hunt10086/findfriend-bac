package com.dying.service.impl;

import com.dying.domain.po.User;
import com.dying.domain.vo.UserVO;
import com.dying.mapper.UserMapper;
import com.dying.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * @author daylight
 */
@Component
public class RecommendCachePreheatTask {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;

    // 每天凌晨2点预热所有用户的推荐缓存
    @Scheduled(cron = "0 0 2 * * ?")
    public void preheatRecommendCache() {
        List<User> allUsers = userMapper.selectList(null);
        for (User user : allUsers) {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            userService.backLike(userVO, 1);
            userService.backLike(userVO, 2);
        }
    }
} 