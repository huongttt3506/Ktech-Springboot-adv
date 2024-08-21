package com.example.file;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class MultipartController {
    @PostMapping(
            value = "/multipart",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public String multipart(
            @RequestParam("file")
            MultipartFile multipartFile
    ) throws IOException {
        // // Đảm bảo thư mục media/ tồn tại
        Path directory = Paths.get("media");
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        System.out.println(multipartFile.getOriginalFilename());
        // Lưu trữ file vào thư mục media/
        Path downloadPath =
                Path.of("media/" + multipartFile.getOriginalFilename());
        multipartFile.transferTo(downloadPath);
        /*byte[] bytes = multipartFile.getBytes();
        System.out.println(new String(bytes));

        File file = new File("./" + multipartFile.getOriginalFilename());
        try (OutputStream outputStream = new FileOutputStream(file)){
            outputStream.write(bytes);
        }*/

        return "http://localhost:8080/static/" + multipartFile.getOriginalFilename();
    }
}