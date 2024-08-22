package com.example.file;

import com.example.file.dto.UserDto;
import com.example.file.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    // CREATE
    public UserDto create(UserDto dto) {
//        if (repository.existsByUsername(dto.getUsername())) {
//            System.out.println("username already exists");
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);}
        if (repository.existsByUsername(dto.getUsername()))
            throw new IllegalArgumentException("username already exists");

        User newUser = new User();
        newUser.setUsername(dto.getUsername());
        newUser.setPassword(dto.getPassword());
        return UserDto.fromEntity(repository.save(newUser));
    }

    // READ (BY username)
    public UserDto readByUsername(String username) {
        Optional<User> optionalUser =
                repository.findByUsername(username);
//        if (optionalUser.isEmpty()) {
//            System.out.println("username not exists");
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND);}
        if (optionalUser.isEmpty())
            throw new IllegalArgumentException("not found");

        return UserDto.fromEntity(optionalUser.get());
    }

    // UPDATE
    public UserDto updateProfileImg(Long id, MultipartFile image)  {
        // 1. 유저가 존재하는지 확인한다.
        Optional<User> optionalUser = repository.findById(id);
        if (optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        // 2. 파일의 업로드 위치를 결정한다.
        // 추천: media/{userId}/profile.png|jpeg
            //2.1 profile image folder 확인 및 생선
        String profileDirectory = "media/" + id + "/"; //media/{userId}
        try {Files.createDirectories(Path.of(profileDirectory));}
        catch (IOException e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
            //2.2. 업로드란 파일의 확장자를 추출한다
        String originalFilenam = image.getOriginalFilename();
        String[] filenameSplit = originalFilenam.split("\\.");
            // fish.pag -> filenameSplit = {"fish", "png"}
            // blue.whale.png -> filenameSplit = {"blue", "whale", "png"}
        String extension = filenameSplit[filenameSplit.length - 1];
            //2.3 실제 위치에 파일을 저장한다
             // media/1/profile.png
             // media/2/profile.png
        String uploadPath = profileDirectory + "profile." + extension;
        try {
            image.transferTo(Path.of(uploadPath));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 3. 업로드에 성공하면, 이미지 URL을 Entity에 저장한다.
        // http://localhost:8080/static/{userId}/profile.png|jpeg
        String reqPath = "/static/" + id + "/profile." + extension;
        User target = optionalUser.get();
        target.setProfileImgUrl(reqPath);

        // 4. User Entity를 DTO로 변환해서 반환한다.
        return UserDto.fromEntity(repository.save(target));

    }
}
