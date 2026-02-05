package com.dying.controller;


import cn.hutool.core.util.StrUtil;
import com.dying.common.BaseResponse;
import com.dying.common.ErrorCode;
import com.dying.common.ResultUtils;
import com.dying.domain.vo.UserVO;
import com.dying.exception.BusinessException;
import com.dying.service.impl.FilePictureUpload;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.dying.constant.CosConstant.*;
import static com.dying.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author daylight
 */
@Slf4j
@RestController
public class UploadController {
    @Resource
    private FilePictureUpload filePictureUpload;

    /**
     * 上传图片（可重新上传）
     */
    @PostMapping("/upload")
    @Operation(summary = "上传图片", description = "上传图片文件到指定空间")
    public BaseResponse<String> uploadPicture(
            @RequestPart("file") MultipartFile multipartFile,
            String type,
            HttpServletRequest request) {
        UserVO user = (UserVO) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
        }
        if (StrUtil.isEmpty(type)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String url;
        try {
            url = switch (type) {
                case "User" -> filePictureUpload.uploadPicture(multipartFile, USER_ICON_PATH);
                case "Blog" -> filePictureUpload.uploadPicture(multipartFile, BLOG_PIC_PATH + "/"+user.getId());
                case "Team" -> filePictureUpload.uploadPicture(multipartFile, TEAM_ICON_PATH);
                default -> throw new BusinessException(ErrorCode.PARAMS_ERROR);
            };
        } catch (Exception e) {
            log.error(e.getMessage());
            log.info("图片上传失败");
            throw new RuntimeException(e);
        }
        return ResultUtils.success(url);
    }
}
