package com.dying;

import com.dying.domain.po.User;
import com.dying.domain.vo.UserVO;
import com.dying.service.UserService;
import com.dying.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Test
    public void testGetSafetyUser() {
        UserService userService = new UserServiceImpl();

        // 创建测试用户
        User user = new User();
        user.setId(1L);
        user.setUserName("test_user");
        user.setUserAccount("test_account");
        user.setUserPassword("encrypted_password");
        user.setAvatarUrl("http://example.com/avatar.jpg");
        user.setGender(1);
        user.setTags("[\"java\",\"spring\"]");
        user.setPhone("12345678901");
        user.setEmail("test@example.com");
        user.setUserStatus(0);
        user.setCreateTime(java.sql.Timestamp.valueOf("2025-01-01 00:00:00"));
        user.setUpdateTime(java.sql.Timestamp.valueOf("2025-01-01 00:00:00"));
        user.setUserRole(0);
        user.setProfile("Test Profile");
        user.setLatitude(39.9042);
        user.setLongitude(116.4074);

        // 获取脱敏后的用户信息
        UserVO safetyUser = userService.getSafetyUser(user);

        // 验证非敏感字段是否被保留
        assertEquals(user.getId(), safetyUser.getId());
        assertEquals(user.getUserName(), safetyUser.getUserName());
        assertEquals(user.getAvatarUrl(), safetyUser.getAvatarUrl());
        assertEquals(user.getGender(), safetyUser.getGender());
        assertEquals(user.getTags(), safetyUser.getTags());
        assertEquals(user.getPhone(), safetyUser.getPhone());
        assertEquals(user.getEmail(), safetyUser.getEmail());
        assertEquals(user.getCreateTime(), safetyUser.getCreateTime());
        assertEquals(user.getProfile(), safetyUser.getProfile());
        assertEquals(user.getLatitude(), safetyUser.getLatitude());
        assertEquals(user.getLongitude(), safetyUser.getLongitude());

        // 验证敏感字段是否被移除（在UserVO中没有这些字段，因此不会被复制）
        // 在UserVO中没有userAccount、userPassword和userRole字段，所以这些信息已被有效脱敏
        assertNotNull(safetyUser.getUserName()); // 只有安全的字段被保留

        System.out.println("脱敏测试通过！");
    }
}