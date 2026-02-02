package com.dying.service.impl;

import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/**
 * @Author daylight
 */
@Service
public class EmailServiceImpl {
    @Resource
    private JavaMailSender mailSender;

    public String sendEmailBackCode(String to) throws MessagingException, UnsupportedEncodingException {
        String code = RandomUtil.randomNumbers(6);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("trueseestars@qq.com", "验证码提醒");
        helper.setSubject("编程匹配安全验证码");
        helper.setTo(to);
        helper.setText("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <style>\n" +
                "        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; }\n" +
                "        .container { background-color: #f8f9fa; border-radius: 8px; padding: 30px; border: 1px solid #e9ecef; }\n" +
                "        .header { text-align: center; margin-bottom: 25px; }\n" +
                "        .logo { color: #0d6efd; font-size: 24px; font-weight: 700; }\n" +
                "        .code-box { background: #ffffff; border-radius: 6px; padding: 15px; text-align: center; margin: 25px 0; border: 1px dashed #0d6efd; }\n" +
                "        .verification-code { font-size: 32px; letter-spacing: 3px; color: #0d6efd; font-weight: 700; }\n" +
                "        .note { background-color: #e7f1ff; padding: 12px; border-radius: 4px; font-size: 13px; margin: 20px 0; color: #084298; }\n" +
                "        .footer { text-align: center; margin-top: 30px; color: #6c757d; font-size: 12px; }\n" +
                "        .divider { height: 1px; background-color: #dee2e6; margin: 25px 0; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <div class=\"logo\">编程匹配</div>\n" +
                "            <h2>您的验证码</h2>\n" +
                "        </div>\n" +
                "        \n" +
                "        <p>您好！您正在执行账户安全操作，请使用以下验证码：</p>\n" +
                "        \n" +
                "        <div class=\"code-box\">\n" +
                "            <div class=\"verification-code\">" + code + "</div>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"note\">\n" +
                "            ⚠️ 安全提示：该验证码 <strong>5分钟内</strong> 有效，请勿泄露给他人。如非本人操作，请忽略此邮件。\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"divider\"></div>\n" +
                "        \n" +
                "        <p>感谢使用我们的服务！<br>\n" +
                "        \n" +
                "        <div class=\"footer\">\n" +
                "            <p>© 2025 编程匹配. 保留所有权利</p>\n" +
                "            <p>此为系统邮件，请勿直接回复</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>", true);
        mailSender.send(message);
        return code;
    }
}