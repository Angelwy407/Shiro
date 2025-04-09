package com.shiro.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileData {
    private String fileName;
    private String author;
    private String fileHash;
    private String time;
}
