package com.study.spring.board.service;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${file.thumbnail-dir:thumbnails}")
    private String thumbnailDir;

    /**
     * 파일을 저장하고 저장된 파일 경로를 반환
     * 
     * @param file 업로드할 파일
     * @return 저장된 파일 경로
     */
    public String saveFile(MultipartFile file) throws IOException {
        // 업로드 디렉토리 생성
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // UUID를 사용한 고유 파일명 생성
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uuidFilename = UUID.randomUUID().toString() + extension;

        // 파일 저장
        Path filePath = uploadPath.resolve(uuidFilename);
        Files.copy(file.getInputStream(), filePath);

        return filePath.toString();
    }

    /**
     * 이미지 파일을 저장하고 썸네일을 생성
     * 
     * @param file 업로드할 이미지 파일
     * @return 저장된 파일 경로
     */
    public String saveImageWithThumbnail(MultipartFile file) throws IOException {
        // 원본 파일 저장
        String filePath = saveFile(file);

        // 이미지 파일인 경우 썸네일 생성
        String contentType = file.getContentType();
        if (contentType != null && contentType.startsWith("image/")) {
            createThumbnail(filePath);
        }

        return filePath;
    }

    /**
     * 썸네일 생성
     * 
     * @param originalFilePath 원본 파일 경로
     */
    private void createThumbnail(String originalFilePath) throws IOException {
        // 썸네일 디렉토리 생성
        Path thumbnailPath = Paths.get(thumbnailDir);
        if (!Files.exists(thumbnailPath)) {
            Files.createDirectories(thumbnailPath);
        }

        // 원본 파일명 추출
        File originalFile = new File(originalFilePath);
        String filename = originalFile.getName();
        String thumbnailFilePath = thumbnailPath.resolve("thumb_" + filename).toString();

        // Thumbnailator를 사용하여 썸네일 생성 (200x200 크기)
        Thumbnails.of(originalFile)
                .size(200, 200)
                .toFile(thumbnailFilePath);
    }

    /**
     * 파일 삭제
     * 
     * @param filePath 삭제할 파일 경로
     */
    public void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            Files.delete(path);
        }

        // 썸네일도 삭제
        File file = new File(filePath);
        String filename = file.getName();
        Path thumbnailPath = Paths.get(thumbnailDir, "thumb_" + filename);
        if (Files.exists(thumbnailPath)) {
            Files.delete(thumbnailPath);
        }
    }
}
