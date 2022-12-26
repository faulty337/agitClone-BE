package com.hanghae99.agitclone.post.controller;

import com.hanghae99.agitclone.common.ResponseMessage;
import com.hanghae99.agitclone.post.dto.RequestPostDto;
import com.hanghae99.agitclone.post.dto.ResponsePostDto;
import com.hanghae99.agitclone.post.service.PostService;
import com.hanghae99.agitclone.user.entity.Users;
import lombok.RequiredArgsConstructor;
import com.hanghae99.agitclone.security.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    //게시글 등록
    //유저 정보 수정 필요

    @PostMapping("/agit/{agitId}/post")
    public ResponseEntity<ResponseMessage> createPost(@PathVariable Long agitId, @RequestBody RequestPostDto requestPostDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        ResponsePostDto responsePostDto = postService.createPost(agitId, requestPostDto, userDetails.getUser());
        ResponseMessage<ResponsePostDto> responseMessage = new ResponseMessage<>("Success", 200, responsePostDto);
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatusCode()));
    }



    @GetMapping("/agit/{agitId}")
    public ResponseEntity<ResponseMessage> getPost(@PathVariable Long agitId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Users users = userDetails.getUser();
        List<ResponsePostDto> responsePostDtoList = postService.getPostList(agitId, users);
        ResponseMessage<List<ResponsePostDto>> responseMessage = new ResponseMessage<>("Success", 200, responsePostDtoList);

        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatusCode()));
    }

    //게시글 수정
    @PutMapping("/agit/post/{postId}")
    public ResponseEntity<ResponseMessage> updatePost(@PathVariable Long postId, @RequestBody RequestPostDto requestPostDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        ResponsePostDto responsePostDto = postService.updatePost(postId, requestPostDto, userDetails.getUser());
        ResponseMessage<ResponsePostDto> responseMessage = new ResponseMessage<>("Success", 200, responsePostDto);
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatusCode()));
    }

    //게시글 삭제
    @DeleteMapping("/agit/post/{postId}")
    public ResponseEntity<ResponseMessage> deletePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        postService.deletePost(postId, userDetails.getUser());
        ResponseMessage<?> responseMessage = new ResponseMessage("Success", 200, null);
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatusCode()));
    }

}
