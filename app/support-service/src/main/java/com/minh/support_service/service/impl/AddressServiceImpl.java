package com.minh.support_service.service.impl;

import com.minh.common.constants.ErrorCode;
import com.minh.common.constants.ResponseMessages;
import com.minh.common.message.MessageCommon;
import com.minh.common.response.ResponseData;
import com.minh.common.utils.AppUtils;
import com.minh.support_service.DTO.AddressDto;
import com.minh.support_service.DTO.UserDto;
import com.minh.support_service.entity.Address;
import com.minh.support_service.repository.AddressRepository;
import com.minh.support_service.service.AddressService;
import com.minh.support_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final UserService userService;
    private final AddressRepository repository;
    private final MessageCommon messageCommon;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ResponseData createAddress(AddressDto addressDto) {
        String username = AppUtils.getUsername();
        if (username == null || username.isEmpty()) {
            throw new RuntimeException(messageCommon.getMessage(ErrorCode.Auth.UNAUTHORIZED));
        }
        UserDto userDto = userService.findByUsername(username);
        if (userDto == null) {
            throw new RuntimeException(messageCommon.getMessage(ErrorCode.User.USERNAME_NOT_FOUND, username));
        }
        Address address = modelMapper.map(addressDto, Address.class);
        address.setId(UUID.randomUUID().toString());
        address.setUsername(userDto.getUsername());
        if (address.getIsDefault()) {
            repository.updateAllAddressToNonDefault(userDto.getUsername());
        }
        repository.save(address);

        return ResponseData.builder()
                .status(201)
                .message(ResponseMessages.CREATED)
                .data(address)
                .build();
    }

    @Override
    public ResponseData updateAddress(AddressDto addressDto) {
        Address address = modelMapper.map(addressDto, Address.class);
        repository.save(address);
        return ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(addressDto)
                .build();
    }

    @Override
    public ResponseData deleteAddress(String id) {
        Address address = repository.findById(id).orElse(null);
        if (address == null) {
            throw new RuntimeException(messageCommon.getMessage(ErrorCode.Address.NOT_FOUND, id));
        }

        repository.delete(address);
        return ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(null)
                .build();
    }

    @Override
    public ResponseData getAddressesByUsername() {
        String username = AppUtils.getUsername();
        List<Address> addresses = repository.getAddressByUsername(username);
        List<AddressDto> addressDtos = addresses.stream()
                .map(address -> modelMapper.map(address, AddressDto.class))
                .toList();

        return ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(addressDtos)
                .build();
    }

    @Override
    @Transactional
    public ResponseData setDefaultAddress(String id) {
        Address address = repository.findById(id).orElseThrow(
                () -> new RuntimeException(messageCommon.getMessage(ErrorCode.Address.NOT_FOUND, id))
        );

        if (!StringUtils.hasText(address.getUsername()) || !address.getUsername().equals(AppUtils.getUsername())) {
            throw new RuntimeException(messageCommon.getMessage(ErrorCode.FORBIDDEN));
        }
        repository.updateAllAddressToNonDefault(address.getUsername());
        address.setIsDefault(true);

        repository.save(address);
        return ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(null)
                .build();
    }

    @Override
    public AddressDto getAddressById(String shippingAddressId) {
        System.out.println("Fetching address with ID: " + shippingAddressId);
        Address address = repository.findById(shippingAddressId).orElse(null);
        if (address == null) {
            return null;
        }
        return modelMapper.map(address, AddressDto.class);
    }

    @Override
    public AddressDto findById(String addressId) {
        return repository.findById(addressId)
                .map(
                address -> modelMapper.map(address, AddressDto.class)
        )
                .orElse(null);
    }
}
