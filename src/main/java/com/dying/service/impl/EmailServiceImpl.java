package com.dying.service.impl;

import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.mail.username}")
    private String EMAIL_SENDER;

    // ===================== 模板常量定义 =====================

    // CSS样式常量（可复用）
    private static final String EMAIL_CSS = """
            body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; }
            .container { background-color: #f8f9fa; border-radius: 8px; padding: 30px; border: 1px solid #e9ecef; }
            .header { text-align: center; margin-bottom: 25px; }
            .logo { color: #0d6efd; font-size: 24px; font-weight: 700; }
            .code-box { background: #ffffff; border-radius: 6px; padding: 15px; text-align: center; margin: 25px 0; border: 1px dashed #0d6efd; }
            .verification-code { font-size: 32px; letter-spacing: 3px; color: #0d6efd; font-weight: 700; }
            .note { background-color: #e7f1ff; padding: 12px; border-radius: 4px; font-size: 13px; margin: 20px 0; color: #084298; }
            .footer { text-align: center; margin-top: 30px; color: #6c757d; font-size: 12px; }
            .divider { height: 1px; background-color: #dee2e6; margin: 25px 0; }
            """;

    // HTML文档框架
    private static final String EMAIL_TEMPLATE_FRAME = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                %s
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">编程匹配</div>
                        <h2>%s</h2>
                    </div>
            
                    <p>%s</p>
            
                    <div class="code-box">
                        <div class="verification-code">%s</div>
                    </div>
            
                    <div class="note">
                        ⚠️ 安全提示：该验证码 <strong>5分钟内</strong> 有效，请勿泄露给他人。如非本人操作，请忽略此邮件。
                    </div>
            
                    <div class="divider"></div>
            
                    <p>感谢使用%s！<br>
            
                    <div class="footer">
                        <p>© 2025 编程匹配. 保留所有权利</p>
                        <p>此为系统邮件，请勿直接回复</p>
                    </div>
                </div>
            </body>
            </html>
            """;

    // 预编译的完整模板
    private static final String COMPILED_VERIFICATION_TEMPLATE;
    private static final String COMPILED_PASSWORD_RESET_TEMPLATE;

    static {
        // 编译时初始化，运行时直接使用
        COMPILED_VERIFICATION_TEMPLATE = String.format(EMAIL_TEMPLATE_FRAME,
                EMAIL_CSS,
                // 标题占位符
                "%s",
                // 描述占位符
                "%s",
                // 验证码占位符
                "%s",
                // 服务名称占位符
                "%s"
        );

        COMPILED_PASSWORD_RESET_TEMPLATE = String.format(EMAIL_TEMPLATE_FRAME,
                EMAIL_CSS,
                // 标题占位符
                "%s",
                // 描述占位符
                "%s",
                // 验证码占位符
                "%s",
                // 服务名称占位符
                "%s"
        );
    }

    // 邮件内容常量
    private static final String VERIFICATION_TITLE = "您的验证码";
    private static final String VERIFICATION_DESC = "您好！您正在执行账户安全操作，请使用以下验证码：";
    private static final String VERIFICATION_SERVICE = "我们的服务";

    private static final String PASSWORD_RESET_TITLE = "密码找回验证码";
    private static final String PASSWORD_RESET_DESC = "您好！您正在进行密码找回操作，请使用以下验证码：";
    private static final String PASSWORD_RESET_SERVICE = "编程匹配服务";

    // ===================== 业务方法 =====================

    /**
     * 发送验证码邮件
     */
    public String sendEmailBackCode(String to) throws MessagingException, UnsupportedEncodingException {
        String code = RandomUtil.randomNumbers(6);

        // 直接使用预编译模板填充内容
        String htmlContent = String.format(COMPILED_VERIFICATION_TEMPLATE,
                VERIFICATION_TITLE,
                VERIFICATION_DESC,
                code,
                VERIFICATION_SERVICE
        );

        sendEmail(to, "编程匹配安全验证码", htmlContent);
        return code;
    }

    /**
     * 发送密码找回邮件
     */
    public String sendEmailToBackPassword(String email) throws MessagingException, UnsupportedEncodingException {
        String code = RandomUtil.randomNumbers(6);

        // 直接使用预编译模板填充内容
        String htmlContent = String.format(COMPILED_PASSWORD_RESET_TEMPLATE,
                PASSWORD_RESET_TITLE,
                PASSWORD_RESET_DESC,
                code,
                PASSWORD_RESET_SERVICE
        );

        sendEmail(email, "编程匹配密码找回验证码", htmlContent);
        return code;
    }

    /**
     * 通用邮件发送方法（私有）
     */
    private void sendEmail(String to, String subject, String htmlContent)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(EMAIL_SENDER, "验证码提醒");
        helper.setSubject(subject);
        helper.setTo(to);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    /**
     * 扩展：发送自定义内容的邮件（可选）
     */
    public void sendCustomEmail(String to, String subject, String title,
                                String description, String content) throws MessagingException, UnsupportedEncodingException {
        String htmlContent = String.format(COMPILED_VERIFICATION_TEMPLATE,
                title,
                description,
                content,
                VERIFICATION_SERVICE
        );

        sendEmail(to, subject, htmlContent);
    }
}