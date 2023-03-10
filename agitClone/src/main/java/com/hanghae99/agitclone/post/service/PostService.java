package com.hanghae99.agitclone.post.service;

import com.hanghae99.agitclone.agit.entity.Agit;
import com.hanghae99.agitclone.agit.entity.AgitMember;
import com.hanghae99.agitclone.agit.repository.AgitMemberRepository;
import com.hanghae99.agitclone.agit.repository.AgitRepository;
import com.hanghae99.agitclone.comment.entity.Comment;
import com.hanghae99.agitclone.comment.repository.CommentRepository;
import com.hanghae99.agitclone.common.exception.CustomException;
import com.hanghae99.agitclone.post.dto.MainResponseDto;
import com.hanghae99.agitclone.post.dto.PostRequestDto;
import com.hanghae99.agitclone.post.dto.PostResponseDto;
import com.hanghae99.agitclone.post.entity.Post;
import com.hanghae99.agitclone.post.mapper.PostMapper;
import com.hanghae99.agitclone.post.repository.PostLikeRepository;
import com.hanghae99.agitclone.post.repository.PostRepository;
import com.hanghae99.agitclone.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.hanghae99.agitclone.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final AgitRepository agitRepository;
    private final PostMapper postMapper;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final AgitMemberRepository agitMemberRepository;

    public MainResponseDto getPostList(Long agitId, Long userId) {
        Agit agit = agitRepository.findById(agitId).orElseThrow(
                ()->new CustomException(AGIT_NOT_FOUND)
        );
        if(agit.getAgitMemberList().stream().noneMatch(agitMember -> agitMember.getUserId().equals(userId))){
            throw new CustomException(AUTHORIZATION_AGIT_FAIL);
        }

        List<PostResponseDto> postDtoList = new ArrayList<>();
        List<Post> postList = agit.getPostList();

        for(Post post : postList){
            postDtoList.add(postMapper.toResponsePostDto(post, userId));
        }
        MainResponseDto mainResponseDto = new MainResponseDto(agit.getAgitName(), agit.getAgitInfo(), postDtoList);
        return mainResponseDto;
    }

    //????????? ??????
    @Transactional
    public PostResponseDto createPost(Long agitId, PostRequestDto requestPostDto, Users users) {
        //???????????? ????????? ?????? ?????? ???????????? ??????
        List<AgitMember> agitMemberList = agitMemberRepository.findAllByAgitId(agitId);
        if(agitMemberList.stream().noneMatch(agitMember -> agitMember.getUserId().equals(users.getId()))){
            throw new CustomException(AUTHORIZATION_AGIT_FAIL);
        }
        //???????????? ????????? ?????? ??????
        Agit agit = agitRepository.findById(agitId).orElseThrow(
                () -> new CustomException(AGIT_NOT_FOUND)
        );
        //????????? ??????
        Post post = postRepository.save(postMapper.toEntity(requestPostDto, users, agitId));

        agit.addPostList(post);
        return postMapper.toResponsePostDto(post);
    }

    //????????? ??????
    @Transactional
    public PostResponseDto updatePost(Long postId, PostRequestDto requestPostDto, Long userId){
        //????????? ?????? ??????.
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(CONTENT_NOT_FOUND)
        );

        //????????? ???????????? ?????? ????????? ?????? ???????????? ??????.
        if(!post.getUsers().getId().equals(userId)){
            throw new CustomException(AUTHORIZATION_UPDATE_FAIL);
        }

        post.change(requestPostDto.getContent());
        return postMapper.toResponsePostDto(post);
    }

    //????????? ??????
    @Transactional
    public void deletePost(Long postId, Long userId){
        //????????? ?????? ??????.
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(CONTENT_NOT_FOUND)
        );
        //????????? ???????????? ?????? ????????? ?????? ???????????? ??????.
        if(!post.getUsers().getId().equals(userId)){
            throw new CustomException(AUTHORIZATION_DELETE_FAIL);
        }


        //???????????? ???????????? ?????? ?????? ??????
        List<Long> commentList = new ArrayList<>();
        for(Comment comment : post.getCommentList()){
            commentList.add(comment.getId());
        }

        if(!commentList.isEmpty()){
            commentRepository.deleteAllByIdInQuery(commentList);
        }
        postLikeRepository.deleteAllByPostId(post.getId());

        postRepository.delete(post);
    }
}
