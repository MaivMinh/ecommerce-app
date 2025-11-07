package com.minh.support_service.service.impl;

import com.minh.common.DTOs.AuthenticatedDetails;
import com.minh.common.constants.ErrorCode;
import com.minh.common.constants.ResponseMessages;
import com.minh.common.message.MessageCommon;
import com.minh.common.response.ResponseData;
import com.minh.common.utils.AppUtils;
import com.minh.support_service.DTO.UserDto;
import com.minh.support_service.entity.UserEntity;
import com.minh.support_service.repository.UserEntityRepository;
import com.minh.support_service.service.AddressService;
import com.minh.support_service.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserEntityRepository repository;
    private final MessageCommon messageCommon;
    private final AddressService addressService;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserEntityRepository repository, MessageCommon messageCommon, @Lazy AddressService addressService, @Qualifier("getMapper") ModelMapper modelMapper) {
        this.repository = repository;
        this.messageCommon = messageCommon;
        this.addressService = addressService;
        this.modelMapper = modelMapper;
    }

    @Override
    public ResponseData getProfile() {
        String username = AppUtils.getUsername();
        UserEntity user = repository.findByUsername(username).orElse(null);
        if (Objects.isNull(user)) {
            user = new UserEntity();
            user.setId(UUID.randomUUID().toString());
            user.setUsername(username);
            AuthenticatedDetails details = AppUtils.getUserDetails();
            if (Objects.nonNull(details)) {
                user.setEmails(details.getEmail());
                user.setFullName(details.getFamilyName() + " " + details.getGivenName());
            }
            UserEntity saved = repository.save(user);

            UserDto dto = modelMapper.map(saved,UserDto.class);

            return ResponseData.builder()
                    .status(200)
                    .message(ResponseMessages.SUCCESS)
                    .data(dto)
                    .build();
        }
        UserDto dto = modelMapper.map(user,UserDto.class);
        return ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(dto)
                .build();
    }

    /**
     * Update profile
     * */
    @Override
    public ResponseData updateProfile(UserDto dto) {
        if (!StringUtils.hasText(dto.getId()))  {
            return ResponseData.builder()
                    .status(400)
                    .message(ResponseMessages.BAD_REQUEST)
                    .data(null)
                    .build();
        }

        UserEntity user = repository.findById(dto.getId()).orElseThrow(
                () -> new RuntimeException(messageCommon.getMessage(ErrorCode.User.NOT_FOUND,dto.getId()))
        );
        modelMapper.map(dto,user);
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
        return modelMapper.map(user,UserDto.class);
    }
//
//    @Override
//    public GetShippingAddressResponse getShippingAddress(GetShippingAddressRequest request) {
//        AddressDto address = addressService.getAddressById(request.getShippingAddressId());
//        if (Objects.isNull(address)) {
//            return GetShippingAddressResponse.newBuilder()
//                    .setStatus(404)
//                    .setMessage("Không tìm thấy địa chỉ với id: " + request.getShippingAddressId())
//                    .build();
//        }
//
//        return GetShippingAddressResponse.newBuilder()
//                .setStatus(200)
//                .setMessage(ResponseMessages.SUCCESS)
//                .setId(address.getId())
//                .setFullName(address.getFullName())
//                .setPhone(address.getPhone())
//                .setAddress(address.getAddress())
//                .build();
//    }
}
