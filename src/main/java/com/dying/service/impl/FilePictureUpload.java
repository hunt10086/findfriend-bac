package com.dying.service.impl;

import cn.hutool.core.io.FileUtil;
import com.dying.common.ErrorCode;
import com.dying.exception.ThrowUtils;
import com.dying.manager.upload.PictureUploadTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 文件图片上传实现类
 * 支持MultipartFile文件上传
 * @author daylight
 */
@Service
public class FilePictureUpload extends PictureUploadTemplate {

    /**
     * 校验文件图片参数
     *
     * @param inputSource 图片输入源（MultipartFile）
     */
    @Override
    protected void validPicture(Object inputSource) {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "文件不能为空");
        // 1. 校验文件大小  
        long fileSize = multipartFile.getSize();
        final long ONE_M = 1024 * 1024L;
        ThrowUtils.throwIf(fileSize > 4 * ONE_M, ErrorCode.PARAMS_ERROR, "文件大小不能超过 4M");
        // 2. 校验文件后缀  
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        // 允许上传的文件后缀  
        final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpeg", "jpg", "png", "webp");
        ThrowUtils.throwIf(!ALLOW_FORMAT_LIST.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "文件类型错误");
    }

    /**
     * 获取原始文件名
     *
     * @param inputSource 图片输入源（MultipartFile）
     * @return 原始文件名
     */
    @Override
    protected String getOriginFilename(Object inputSource) {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        return multipartFile.getOriginalFilename();
    }

    /**
     * 处理文件图片（保存到本地）
     *
     * @param inputSource 图片输入源（MultipartFile）
     * @param file        目标文件
     * @throws Exception 文件处理异常
     */
    @Override
    protected void processFile(Object inputSource, File file) throws Exception {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        multipartFile.transferTo(file);
    }
}
