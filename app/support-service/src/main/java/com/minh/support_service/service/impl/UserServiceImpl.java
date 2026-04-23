package com.minh.support_service.service.impl;

import com.minh.common.constants.ErrorCode;
import com.minh.common.constants.ResponseMessages;
import com.minh.common.message.MessageCommon;
import com.minh.common.response.ResponseData;
import com.minh.common.utils.AppUtils;
import com.minh.support_service.DTO.UserDto;
import com.minh.support_service.entity.UserEntity;
import com.minh.support_service.repository.UserEntityRepository;
import com.minh.support_service.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserEntityRepository repository;
    private final MessageCommon messageCommon;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserEntityRepository repository, MessageCommon messageCommon, @Qualifier("getMapper") ModelMapper modelMapper) {
        this.repository = repository;
        this.messageCommon = messageCommon;
        this.modelMapper = modelMapper;
    }

    @Override
    public ResponseData getProfile() {
        String username = AppUtils.getUsername();
        UserEntity user = repository.findByUsername(username).orElse(null);
        if (Objects.isNull(user)) {
            return ResponseData.builder()
                    .status(404)
                    .message(ResponseMessages.NOT_FOUND)
                    .data(null)
                    .build();
        }
        UserDto dto = modelMapper.map(user, UserDto.class);
        return ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(dto)
                .build();
    }

    /**
     * Update profile
     *
     */
    @Override
    public ResponseData updateProfile(UserDto dto) {
        if (!StringUtils.hasText(dto.getId())) {
            return ResponseData.builder()
                    .status(400)
                    .message(ResponseMessages.BAD_REQUEST)
                    .data(null)
                    .build();
        }

        UserEntity user = repository.findById(dto.getId()).orElseThrow(
                () -> new RuntimeException(messageCommon.getMessage(ErrorCode.User.NOT_FOUND, dto.getId()))
        );
        modelMapper.map(dto, user);
        repository.save(user);
        return ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(dto)
                .build();
    }

    @Override
    public UserDto findByUsername(String username) {
        UserEntity user = repository.findByUsername(username).orElse(null);
        if (Objects.isNull(user)) {
            return null;
        }
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public ResponseData createProfile(UserDto dto) {
        Optional<UserEntity> optional = repository.findByUsername(dto.getUsername());
        if (optional.isPresent()) {
            return ResponseData.builder()
                    .status(400)
                    .message(messageCommon.getMessage(ErrorCode.User.USERNAME_EXISTED, dto.getUsername()))
                    .build();
        }

        UserEntity user = modelMapper.map(dto, UserEntity.class);
        user.setId(AppUtils.generateUUIDv7());
        repository.save(user);
        return ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .build();
    }
}
