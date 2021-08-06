package com.nutritiondesigner.itemservice.model.dto.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {
    private String uuid;
    private String fileName;
    private String contentType;
    private String path;

    public FileDto(String fileName, String contentType) {
        uuid = UUID.randomUUID().toString();
        fileName = fileName;
        contentType = contentType;
        path = uuid + "_" + fileName;
    }
}
