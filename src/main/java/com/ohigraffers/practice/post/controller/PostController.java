package com.ohigraffers.practice.post.controller;

import com.ohigraffers.practice.post.dto.response.ResponseMessage;
import com.ohigraffers.practice.post.dto.response.PostResponse;
import com.ohigraffers.practice.post.model.Post;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/posts")
public class PostController {

    private List<Post> posts;

    public PostController(){
        posts = new ArrayList<>();
        posts.add(new Post(1L, "제목1", "내용1", "홍길동"));
        posts.add(new Post(2L, "제목2", "내용2", "유관순"));
        posts.add(new Post(3L, "제목3", "내용3", "신사임당"));
        posts.add(new Post(4L, "제목4", "내용4", "이순신"));
        posts.add(new Post(5L, "제목5", "내용5", "장보고"));
    }

    @Operation(summary = "전체 포스트 조회", description = "전체 포스트를 조회한다.")
    @GetMapping
    public ResponseEntity<ResponseMessage> findAllPosts() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        List<EntityModel<PostResponse>> postResponses = new ArrayList<>();
        for (Post post : posts) {
            PostResponse postResponse = PostResponse.from(post);
            EntityModel<PostResponse> entityModel = EntityModel.of(postResponse);
            Link selfLink = linkTo(methodOn(PostController.class).findPostByCode(post.getCode())).withSelfRel();
            entityModel.add(selfLink);
            postResponses.add(entityModel);
        }

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("posts", postResponses);
        ResponseMessage responseMessage = new ResponseMessage(200, "조회 성공", responseMap);

        return new ResponseEntity<>(responseMessage, headers, HttpStatus.OK);
    }

    @Operation(summary = "특정 코드로 포스트 조회", description = "특정 코드로 포스트를 조회한다.")
    @GetMapping("/{code}")
    public ResponseEntity<ResponseMessage> findPostByCode(@PathVariable Long code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        Post foundPost = posts.stream()
                .filter(post -> post.getCode().equals(code))
                .findFirst()
                .orElse(null);

        if (foundPost == null) {
            return new ResponseEntity<>(new ResponseMessage(404, "포스트를 찾을 수 없습니다.", null), headers, HttpStatus.NOT_FOUND);
        }

        PostResponse postResponse = PostResponse.from(foundPost);
        EntityModel<PostResponse> entityModel = EntityModel.of(postResponse);
        Link selfLink = linkTo(methodOn(PostController.class).findPostByCode(foundPost.getCode())).withSelfRel();
        entityModel.add(selfLink);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("post", entityModel);
        ResponseMessage responseMessage = new ResponseMessage(200, "조회 성공", responseMap);

        return new ResponseEntity<>(responseMessage, headers, HttpStatus.OK);
    }

    @Operation(summary = "신규 포스트 등록", description = "신규 포스트를 등록한다.")
    @PostMapping
    public ResponseEntity<Void> registPost(@Valid @RequestBody Post newPost) {
        posts.add(new Post((long) (posts.size() + 1), newPost.getTitle(), newPost.getContent(), newPost.getWriter()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        headers.setLocation(linkTo(methodOn(PostController.class).findAllPosts()).toUri());

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Operation(summary = "포스트 제목과 내용 수정", description = "포스트의 제목과 내용을 수정한다.")
    @PutMapping("/{code}")
    public ResponseEntity<Void> modifyPost(@Valid @PathVariable Long code, @RequestBody Post modifiedPost) {
        Post existingPost = posts.stream()
                .filter(post -> post.getCode().equals(code))
                .findFirst()
                .orElse(null);

        if (existingPost != null) {
            existingPost.modifyTitleAndContent(modifiedPost.getTitle(), modifiedPost.getContent());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "포스트 삭제", description = "포스트를 삭제한다.")
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> removePost(@PathVariable Long code) {
        boolean removed = posts.removeIf(post -> post.getCode().equals(code));

        if (removed) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
