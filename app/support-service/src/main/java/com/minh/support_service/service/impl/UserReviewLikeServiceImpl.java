package com.minh.support_service.service.impl;

import com.minh.common.constants.ResponseMessages;
import com.minh.support_service.DTO.UserReviewLikeDto;
import com.minh.support_service.entity.UserReviewLike;
import com.minh.support_service.repository.UserReviewLikeRepository;
import com.minh.support_service.service.UserReviewLikeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserReviewLikeServiceImpl implements UserReviewLikeService {
    private final UserReviewLikeRepository repository;
    private final ModelMapper modelMapper;

    @Override
    public UserReviewLikeDto findByReviewIdAndUsername(String reviewId, String username) {
        UserReviewLike entity = repository.findByReviewIdAndUsername(reviewId, username);
        if (entity == null) {
            return null;
        }
        UserReviewLikeDto des = new UserReviewLikeDto();
        modelMapper.map(entity, des);
        return des;
    }

    @Override
    @Transactional
    public void likeReviewWithUser(UserReviewLikeDto source) {
        if (Objects.isNull(source)) {
            throw new RuntimeException(ResponseMessages.BAD_REQUEST);
        }

        UserReviewLike des = new UserReviewLike();
        modelMapper.map(source, des);
        repository.save(des);
    }

    @Override
    public void dislikeReviewWithUserById(String id) {
        if(!StringUtils.hasText(id)) {
            throw new RuntimeException(ResponseMessages.BAD_REQUEST);
        }

        UserReviewLike entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException(ResponseMessages.NOT_FOUND));
        repository.delete(entity);
    }
}
