package com.dying.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dying.domain.po.Team;
import com.dying.domain.po.User;
import com.dying.manager.CosManager;
import com.dying.mapper.TeamMapper;
import com.dying.mapper.UserMapper;
import com.dying.utils.URLUtils;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

/**
 * @author daylight
 */
@Component
@EnableScheduling
public class SpringScheduledExample {

    @Resource
    private CosManager cosManager;

    @Resource
    private UserMapper userMapper;

    @Resource
    private TeamMapper teamMapper;

    @Scheduled(cron = "0 0 3 1 * ?")  // 每月1号执行一次
    public void taskWithCron() {
        List<String> keys = cosManager.listObjectKeys();
//        for(String key : keys) {
//            System.out.println(key);
//        }
        LambdaQueryWrapper<User> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.select(User::getAvatarUrl);

        LambdaQueryWrapper<Team> lambdaQueryWrapper2 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper2.select(Team::getIcon);


        List<String> userUrl = userMapper.selectList(lambdaQueryWrapper1).
                stream().map(User::getAvatarUrl).map(URLUtils::extractPathAfterTop).toList();

        List<String> teamUrl = teamMapper.selectList(lambdaQueryWrapper2).
                stream().map(Team::getIcon).map(URLUtils::extractPathAfterTop).toList();

        HashSet<String> urls = new HashSet<>();
        urls.addAll(userUrl);
        urls.addAll(teamUrl);

//        System.out.println("===========================");
        for (String key : keys) {
            if (!urls.contains(key)) {
                System.out.println(key);
            }
        }

    }


}