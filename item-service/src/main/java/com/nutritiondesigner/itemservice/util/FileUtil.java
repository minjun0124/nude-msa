package com.nutritiondesigner.itemservice.util;

import com.nutritiondesigner.itemservice.model.dto.util.FileDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class FileUtil {

    public static String uploadImage(MultipartFile image) throws IOException {
        if (!image.isEmpty()) {
            FileDto fileDto = new FileDto(image.getOriginalFilename()
                    , image.getContentType());

            File newFile = new File(fileDto.getPath());
            //전달된 내용을 실제 물리적인 파일로 저장
            image.transferTo(newFile);

            return fileDto.getPath();
        }

        return null;
    }
}
