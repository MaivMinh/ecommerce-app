package com.minh.payment_service.service.impl;

import com.minh.common.constants.ErrorCode;
import com.minh.common.constants.ResponseMessages;
import com.minh.common.message.MessageCommon;
import com.minh.common.utils.AppUtils;
import com.minh.payment_service.DTOs.PaymentMethodDto;
import com.minh.payment_service.entity.PaymentMethod;
import com.minh.payment_service.enums.PaymentMethodCurrency;
import com.minh.payment_service.enums.PaymentMethodType;
import com.minh.payment_service.payload.request.CreatePaymentMethodRequest;
import com.minh.payment_service.payload.request.SearchPaymentMethodsQuery;
import com.minh.payment_service.payload.request.UpdatePaymentMethodRequest;
import com.minh.payment_service.payload.response.ResponseData;
import com.minh.payment_service.repository.PaymentMethodRepository;
import com.minh.payment_service.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImpl implements PaymentMethodService {
    private final PaymentMethodRepository paymentMethodRepository;
    private final ModelMapper modelMapper;
    private final MessageCommon messageCommon;

    @Override
    @Transactional
    public void createPaymentMethod(CreatePaymentMethodRequest request) {
        PaymentMethod method = PaymentMethod.builder()
                .id(AppUtils.generateUUIDv7())
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .type(PaymentMethodType.valueOf(request.getType()))
                .provider(request.getProvider())
                .currency(PaymentMethodCurrency.valueOf(request.getCurrency()))
                .iconUrl(request.getIconUrl())
                .isActive(request.getIsActive())
                .build();

        paymentMethodRepository.save(method);
    }

    @Override
    public ResponseData getPaymentMethods(SearchPaymentMethodsQuery query) {
        Pageable pageable = AppUtils.toPageable(query);
        Page<PaymentMethod> methods = paymentMethodRepository.findAll(pageable);

        Map<String, Object> payload = new HashMap<>();
        payload.put("totalElements", methods.getTotalElements());
        payload.put("totalPages", methods.getTotalPages());
        payload.put("page", methods.getNumber() + 1);
        payload.put("size", methods.getSize());
        List<PaymentMethodDto> methodDtos = methods.getContent().stream()
                .map(method -> modelMapper.map(method, PaymentMethodDto.class))
                .collect(Collectors.toList());
        payload.put("paymentMethods", methodDtos);

        return ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(payload)
                .build();
    }

    @Override
    public void updatePaymentMethod(UpdatePaymentMethodRequest request) {
        PaymentMethod method = paymentMethodRepository.findById(request.getId()).orElse(null);
        if (Objects.isNull(method)) {
            throw new RuntimeException(messageCommon.getMessage(ErrorCode.PaymentMethod.NOT_FOUND,request.getId()));
        }

        method.setName(request.getName());
        method.setCurrency(PaymentMethodCurrency.valueOf(request.getCurrency()));
        method.setType(PaymentMethodType.valueOf(request.getType()));
        method.setProvider(request.getProvider());
        method.setDescription(request.getDescription());
        method.setIsActive(request.getIsActive());
        method.setIconUrl(request.getIconUrl());
        method.setCode(request.getCode());
        paymentMethodRepository.save(method);
    }

    @Override
    public void deletePaymentMethod(String id) {
        PaymentMethod method = paymentMethodRepository.findById(id).orElse(null);
        if (Objects.isNull(method)) {
            throw new RuntimeException(messageCommon.getMessage(ErrorCode.PaymentMethod.NOT_FOUND,id));
        }
        paymentMethodRepository.delete(method);
    }

    @Override
    public PaymentMethodDto findByCode(String paymentMethod) {
        PaymentMethod method = paymentMethodRepository.findByCode(paymentMethod);
        if (Objects.isNull(method)) {
            return null;
        }
        return modelMapper.map(method, PaymentMethodDto.class);
    }
}
