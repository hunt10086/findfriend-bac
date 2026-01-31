package com.dying.controller;


import com.dying.common.BaseResponse;
import com.dying.common.ErrorCode;
import com.dying.common.ResultUtils;
import com.dying.domain.po.User;
import com.dying.exception.BusinessException;
import com.dying.manager.CosManager;
import com.dying.service.impl.FilePictureUpload;
import com.qcloud.cos.COSClient;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.dying.constant.UserConstant.USER_LOGIN_STATE;

@Slf4j
@RestController
public class uploadController {


    @Resource
    private COSClient cosClient;

    @Resource
    private CosManager cosManager;

    @Resource
    private FilePictureUpload filePictureUpload;

    /**
     * 上传图片（可重新上传）
     */
    @PostMapping("/upload")
    @Operation(summary = "上传图片", description = "上传图片文件到指定空间")
    public BaseResponse<String> uploadPicture(
            @RequestPart("file") MultipartFile multipartFile,
            HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
        }
        String path = "public/";
        String url=filePictureUpload.uploadPicture(multipartFile, path);
        return ResultUtils.success(url);
    }

}
