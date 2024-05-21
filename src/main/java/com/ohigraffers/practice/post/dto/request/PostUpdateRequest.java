package com.ohigraffers.practice.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/* Swagger 문서화 시 설명 어노테이션 작성 */
public class PostUpdateRequest {

    /* Swagger 문서화 시 설명 어노테이션 작성 */
    /* 필수 값이므로 유효성 검사 어노테이션 작성 */
    @NotBlank(message = "수정한 제목입니다.")
    private String title;

    /* Swagger 문서화 시 설명 어노테이션 작성 */
    /* 필수 값이므로 유효성 검사 어노테이션 작성 */
    @NotBlank(message = "수정한 내용입니다.")
    private String content;

}
