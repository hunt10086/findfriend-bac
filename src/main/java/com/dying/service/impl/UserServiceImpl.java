package com.dying.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dying.common.ErrorCode;
import com.dying.domain.po.User;
import com.dying.domain.request.UserUpdateRequest;
import com.dying.domain.vo.UserVO;
import com.dying.exception.BusinessException;
import com.dying.service.GeoService;
import com.dying.service.UserService;
import com.dying.mapper.UserMapper;
import com.dying.utils.MD5Utils;
import com.dying.utils.RegexUtils;
import com.dying.utils.UserRecommendationUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.dying.constant.UserConstant.*;

/**
 * @author 666
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2025-05-10 13:59:54
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private GeoService geoService;

    private static final String SALT = "Dying";

    @Override
    public long userRegister(String userAccount, String password, String checkPassword, String email) {
        //账号密码不能为空
        if (StringUtils.isBlank(userAccount) || StringUtils.isBlank(password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号密码为空");
        }
        if (StringUtils.isBlank(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "第二次输入密码为空");
        }
        if (StringUtils.isBlank(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱");
        }
        if (!RegexUtils.isEmailInvalid(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式错误");
        }
        //账号大于四位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度小于4");
        }
        //密码不小于八位
        if (password.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不小于八位");
        }
        //账号名，邮箱不能重复
        User one = this.lambdaQuery().eq(User::getUserAccount, userAccount).one();
        if (one != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号名被注册");
        }
        User two = this.lambdaQuery().eq(User::getEmail, email).one();
        if (two != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该邮箱已注册");
        }

        //不允许出现特殊字符
        String regEx = "\\pP|\\pS|\\s+";
        String str = Pattern.compile(regEx).matcher(userAccount).replaceAll("").trim();
        if (!userAccount.equals(str)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不允许出现特殊字符");
        }

        if (!checkPassword.equals(password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不相同");
        }

        String newPassword = MD5Utils.string2MD5(SALT + password + SALT);
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(newPassword);
        user.setEmail(email);
        user.setUserStatus(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        userMapper.insert(user);
        log.info("用户创建成功");

        return 0;
    }

    @Override
    public UserVO userLogin(String userAccount, String password, HttpServletRequest request) {
        //账号密码不能为空
        if (StringUtils.isBlank(userAccount) || StringUtils.isBlank(password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号密码为空");
        }
        //账号大于四位z
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号大于四位");
        }
        //密码不小于八位
        if (password.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码小于八位");
        }
        //不允许出现特殊字符
        String regEx = "\\pP|\\pS|\\s+";
        String str = Pattern.compile(regEx).matcher(userAccount).replaceAll("").trim();
        if (!userAccount.equals(str)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不允许出现特殊字符");
        }
        //检验用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount).eq("user_password", MD5Utils.string2MD5(SALT + password + SALT));
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("用户或密码错误");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户或密码错误");
        }
        //用户脱敏
        UserVO safetyUser = getSafetyUser(user);
        //记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);

        // 缓存预热：预先生成第一页推荐
        backLike(safetyUser, 1);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public UserVO getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        UserVO safetyUser = new UserVO();
        // 只复制安全的字段，排除敏感信息
        safetyUser.setId(originUser.getId());
        safetyUser.setUserName(originUser.getUserName());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setTags(originUser.getTags());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setProfile(originUser.getProfile());
        safetyUser.setLatitude(originUser.getLatitude());
        safetyUser.setLongitude(originUser.getLongitude());
        // 注意：不设置userPassword、userAccount、userRole等敏感字段

        return safetyUser;
    }

    @Override
    public boolean userUpdate(Long userId, UserUpdateRequest userUpdateRequest) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        User user = new User();
        user.setId(userId);
        BeanUtils.copyProperties(userUpdateRequest, user);
        userMapper.updateById(user);
        UserVO safetyUser = getSafetyUser(user);
        // 缓存预热：预先生成第一页推荐
        backLike(safetyUser, 1);
        return true;
    }

    @Override
    public int userLogout(HttpServletRequest request, HttpServletResponse response) {
        if (request == null) {
            return 0;
        }
        // 移除登录态
        request.getSession().invalidate();
        // 清除 JSESSIONID cookie
        Cookie cookie = new Cookie("SESSION", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return 1;
    }

    @Override
    public boolean checkEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return false;
        }
        return RegexUtils.isEmailInvalid(email);
    }

    @Override
    public List<UserVO> backLike(UserVO loginUser, Integer count) {
        // 验证参数
        if (loginUser == null || loginUser.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户信息无效");
        }

        if (count == null || count < 1) {
            // 设置默认值
            count = 1;
        }

        // 限制最大页码，防止恶意请求
        // 限制最大页码为100
        if (count > 100) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "页码过大");
        }

        // 从UserVO创建User对象以匹配工具类要求
        User user = new User();
        user.setId(loginUser.getId());
        user.setTags(loginUser.getTags());

        // 1. Redis缓存优先
        String tagsKey = loginUser.getTags() != null ? loginUser.getTags() : "";
        String cacheKey = USER_LIKE_STATE + ":" + loginUser.getId() + ":" + tagsKey + ":" + count;
        String cacheValue = stringRedisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.isNotBlank(cacheValue)) {
            return JSONUtil.toList(cacheValue, UserVO.class);
        }

        // 2. 查询所有其他用户，且tags不为空
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("id", loginUser.getId());
        queryWrapper.isNotNull("tags");
        queryWrapper.ne("tags", "");
        queryWrapper.eq("user_status", 0);
        List<User> allUsers = userMapper.selectList(queryWrapper);

        // 3. 使用推荐算法进行用户推荐
        List<UserVO> recommendedUsers = UserRecommendationUtils.recommendUsers(user, allUsers);

        // 4. 分页处理
        int pageSize = USER_PAGE_SIZE;
        int start = (count - 1) * pageSize;
        int end = Math.min(start + pageSize, recommendedUsers.size());
        if (start >= end) {
            return new ArrayList<>();
        }
        List<UserVO> result = recommendedUsers.subList(start, end);

        // 5. 写入缓存
        stringRedisTemplate.opsForValue().set(cacheKey, JSONUtil.toJsonStr(result), USER_REDIS_EXPIRE, TimeUnit.MINUTES);

        return result;
    }

    @Override
    public List<UserVO> getNearUser(Long userId) {
        User loginUser = userMapper.selectById(userId);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Double latitude = loginUser.getLatitude();
        Double longitude = loginUser.getLongitude();
        if (latitude == null || longitude == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "填写经纬度后开启附近用户搜索");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("latitude");
        queryWrapper.isNotNull("longitude");
        queryWrapper.ne("id", userId);
        List<User> userList = userMapper.selectList(queryWrapper);
        List<UserVO> list = new ArrayList<>();
        if (userList.isEmpty()) {
            return list;
        }
        int i = 0;
        for (User user : userList) {
            if (i > 15) {
                break;
            }
            double longitude2 = user.getLongitude();
            double latitude2 = user.getLatitude();
            double distance = getDistance(longitude, latitude, longitude2, latitude2);
            if (distance < 1000) {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                userVO.setDistance(distance);
                i++;
                list.add(userVO);
            }
        }
        list.sort(Comparator.comparingDouble(UserVO::getDistance));
        return list;

    }


    public double getDistance(Double longitude1, Double latitude1, Double longitude2, Double latitude2) {
        // 使用Haversine公式计算两点之间的距离
        final double R = 6371; // 地球半径，单位km
        double lat1 = Math.toRadians(latitude1);
        double lat2 = Math.toRadians(latitude2);
        double lon1 = Math.toRadians(longitude1);
        double lon2 = Math.toRadians(longitude2);

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    @Override
    public IPage<UserVO> searchUsersByTagsWithPagination(List<String> tagsList, long currentPage, long pageSize) {
        // 参数校验
        if (CollectionUtils.isEmpty(tagsList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签列表不能为空");
        }
        if (currentPage < 1) {
            currentPage = 1;
        }
        if (pageSize < 1) {
            // 默认每页15条
            pageSize = 15;
        }

        // 生成缓存键：基于标签列表、页码和页面大小
        String tagsKey = String.join(",", tagsList);
        String cacheKey = USER_SEARCH + ":tags:" + tagsKey + ":page:" + currentPage + ":size:" + pageSize;

        // 先从Redis缓存中查询
        String cachedResult = stringRedisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.isNotBlank(cachedResult)) {
            try {
                // 从缓存中获取分页信息
                IPage<UserVO> cachedPage = JSONUtil.toBean(cachedResult, IPage.class);
                log.info("从Redis缓存中获取标签查询结果，标签：{}，页码：{}", tagsKey, currentPage);
                return cachedPage;
            } catch (Exception e) {
                log.warn("缓存数据解析失败，将重新查询数据库", e);
            }
        }

        // 缓存未命中，查询数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        // 构建查询条件：匹配所有标签
        for (String tag : tagsList) {
            queryWrapper.like("tags", tag);
        }

        // 按创建时间降序排列
        queryWrapper.orderByDesc("create_time");

        // 创建分页对象
        Page<User> page = new Page<>(currentPage, pageSize);

        // 执行分页查询
        IPage<User> userPage = userMapper.selectPage(page, queryWrapper);

        // 对查询结果进行脱敏处理并创建新的分页对象
        List<UserVO> safeUsers = userPage.getRecords().stream()
                .map(this::getSafetyUser)
                .collect(Collectors.toList());

        // 创建一个新的IPage<UserVO>实例
        Page<UserVO> safeUserPage = new Page<>();
        safeUserPage.setCurrent(userPage.getCurrent());
        safeUserPage.setSize(userPage.getSize());
        safeUserPage.setTotal(userPage.getTotal());
        safeUserPage.setPages(userPage.getPages());
        safeUserPage.setRecords(safeUsers);

        // 将查询结果存入Redis缓存，设置25分钟过期时间
        try {
            String pageJson = JSONUtil.toJsonStr(safeUserPage);
            stringRedisTemplate.opsForValue().set(cacheKey, pageJson, 25, TimeUnit.MINUTES);
            log.info("标签查询结果已缓存，标签：{}，页码：{}，缓存过期时间：25分钟", tagsKey, currentPage);
        } catch (Exception e) {
            log.warn("缓存写入失败", e);
        }

        return safeUserPage;
    }

}




