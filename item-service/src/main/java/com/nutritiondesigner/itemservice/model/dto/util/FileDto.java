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
        this.uuid = UUID.randomUUID().toString();
        this.fileName = fileName;
        this.contentType = contentType;
        this.path = uuid + "_" + fileName;
    }
}
