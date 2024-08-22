package com.example.validation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotSeriesDto {
    // null만 아니면 된다
    @NotNull
    private String notNull;
    //size, length가 0이 아니어야 한다
    @NotEmpty
    private String notEmpty;
    // 문자열(String) 대상으로, 공백을 제외한 문자가 존재해야 한다.
    @NotBlank
    private String notBlank;


}
