package com.example.weibo.util;

import com.example.weibo.config.FileUploadConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class FileUploadUtil {

    @Autowired
    private FileUploadConfig fileUploadConfig;

    public String uploadAvatar(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : ".png";
        
        String fileName = UUID.randomUUID().toString() + extension;
        
        File uploadDir = new File(fileUploadConfig.getUploadDir());
        if (!uploadDir.isAbsolute()) {
            uploadDir = new File(System.getProperty("user.dir"), fileUploadConfig.getUploadDir());
        }
        
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        File dest = new File(uploadDir.getAbsolutePath(), fileName);
        file.transferTo(dest);
        
        return "/uploads/avatars/" + fileName;
    }
}