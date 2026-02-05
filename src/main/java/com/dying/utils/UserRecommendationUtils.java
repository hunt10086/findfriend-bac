package com.dying.utils;

import com.dying.domain.po.User;
import com.dying.domain.vo.UserVO;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 用户推荐算法工具类
 * @author daylight
 */
@Slf4j
public class UserRecommendationUtils {

    /**
     * 根据用户标签计算相似度并推荐用户列表
     *
     * 推荐策略：
     * 1. 优先返回标签匹配最多的用户
     * 2. 如果当前用户没有标签，则优先返回标签数量多的用户
     * 3. 按相似度降序排列
     *
     * @param loginUser 当前登录用户
     * @param candidateUsers 候选用户列表
     * @return 推荐的用户列表，按相似度排序
     */
    public static List<UserVO> recommendUsers(User loginUser, List<User> candidateUsers) {
        if (candidateUsers == null || candidateUsers.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取当前用户的标签
        List<String> myTags = parseTags(loginUser.getTags());

        // 计算每个候选用户的推荐得分
        List<UserWithScore> scoredUsers = new ArrayList<>();

        for (User candidateUser : candidateUsers) {
            if (candidateUser.getId().equals(loginUser.getId())) {
                // 排除当前用户自身
                continue;
            }

            int similarityScore = calculateSimilarity(myTags, parseTags(candidateUser.getTags()));
            scoredUsers.add(new UserWithScore(candidateUser, similarityScore));
        }

        // 排序
        scoredUsers.sort((u1, u2) -> {
            // 首先按相似度降序排列
            int scoreComparison = Integer.compare(u2.score, u1.score);
            if (scoreComparison != 0) {
                return scoreComparison;
            }

            // 如果相似度相同，则比较标签数量（标签数量多的优先）
            int tagsCount1 = parseTags(u1.user.getTags()).size();
            int tagsCount2 = parseTags(u2.user.getTags()).size();
            int tagsCountComparison = Integer.compare(tagsCount2, tagsCount1);
            if (tagsCountComparison != 0) {
                return tagsCountComparison;
            }

            // 如果标签数量也相同，则按创建时间倒序排列（最新的优先）
            return u2.user.getCreateTime().compareTo(u1.user.getCreateTime());
        });

        // 转换为UserVO列表并返回
        List<UserVO> result = new ArrayList<>();
        for (UserWithScore scoredUser : scoredUsers) {
            UserVO userVO = new UserVO();
            userVO.setId(scoredUser.user.getId());
            userVO.setUserName(scoredUser.user.getUserName());
            userVO.setAvatarUrl(scoredUser.user.getAvatarUrl());
            userVO.setGender(scoredUser.user.getGender());
            userVO.setTags(scoredUser.user.getTags());
            userVO.setPhone(scoredUser.user.getPhone());
            userVO.setEmail(scoredUser.user.getEmail());
            userVO.setCreateTime(scoredUser.user.getCreateTime());
            userVO.setProfile(scoredUser.user.getProfile());
            userVO.setLatitude(scoredUser.user.getLatitude());
            userVO.setLongitude(scoredUser.user.getLongitude());

            result.add(userVO);
        }

        return result;
    }

    /**
     * 计算两个用户之间的标签相似度
     *
     * @param tags1 用户1的标签列表
     * @param tags2 用户2的标签列表
     * @return 相似度得分（匹配的标签数量）
     */
    private static int calculateSimilarity(List<String> tags1, List<String> tags2) {
        if (tags1 == null || tags1.isEmpty() || tags2 == null || tags2.isEmpty()) {
            // 如果其中一个用户没有标签，根据另一个用户的标签数量来决定
            // 如果当前用户没标签，则其他用户标签越多越值得推荐
            // 如果被比较用户没标签，则相似度为0
            if (tags1 == null || tags1.isEmpty()) {
                // 当前用户没标签，其他用户标签越多越推荐
                return tags2.size();
            } else {
                // 被比较用户没标签，相似度为0
                return 0;
            }
        }

        // 计算交集大小
        int similarity = 0;
        for (String tag : tags1) {
            if (tags2.contains(tag)) {
                similarity++;
            }
        }

        return similarity;
    }

    /**
     * 解析用户标签字符串为标签列表
     *
     * @param tagsStr JSON格式的标签字符串
     * @return 标签列表
     */
    private static List<String> parseTags(String tagsStr) {
        if (tagsStr == null || tagsStr.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            return JSONUtil.toList(JSONUtil.parseArray(tagsStr), String.class);
        } catch (Exception e) {
            log.warn("解析用户标签失败: {}", tagsStr, e);
            return new ArrayList<>();
        }
    }

    /**
     * 用户与评分的组合类
     */
    private static class UserWithScore {
        User user;
        int score;

        UserWithScore(User user, int score) {
            this.user = user;
            this.score = score;
        }
    }
}