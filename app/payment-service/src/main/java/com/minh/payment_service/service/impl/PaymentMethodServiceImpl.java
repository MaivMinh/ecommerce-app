package com.minh.payment_service.service.impl;

import com.minh.common.constants.ErrorCode;
import com.minh.common.constants.ResponseMessages;
import com.minh.common.message.MessageCommon;
import com.minh.common.utils.AppUtils;
import com.minh.payment_service.command.events.PaymentMethodCreatedEvent;
import com.minh.payment_service.command.events.PaymentMethodDeletedEvent;
import com.minh.payment_service.command.events.PaymentMethodUpdatedEvent;
import com.minh.payment_service.enums.PaymentMethodCurrency;
import com.minh.payment_service.enums.PaymentMethodType;
import com.minh.payment_service.payload.response.ResponseData;
import com.minh.payment_service.query.DTOs.PaymentMethodDto;
import com.minh.payment_service.query.entity.PaymentMethod;
import com.minh.payment_service.query.queries.SearchPaymentMethodsQuery;
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
    public void createPaymentMethod(PaymentMethodCreatedEvent event) {
        PaymentMethod method = PaymentMethod.builder()
                .id(event.getId())
                .code(event.getCode())
                .name(event.getName())
                .description(event.getDescription())
                .type(PaymentMethodType.valueOf(event.getType()))
                .provider(event.getProvider())
                .currency(PaymentMethodCurrency.valueOf(event.getCurrency()))
                .iconUrl(event.getIconUrl())
                .isActive(event.getIsActive())
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
    public void updatePaymentMethod(PaymentMethodUpdatedEvent event) {
        PaymentMethod method = paymentMethodRepository.findById(event.getId()).orElse(null);
        if (Objects.isNull(method)) {
            throw new RuntimeException(messageCommon.getMessage(ErrorCode.PaymentMethod.NOT_FOUND,event.getId()));
        }

        method.setName(event.getName());
        method.setCurrency(PaymentMethodCurrency.valueOf(event.getCurrency()));
        method.setType(PaymentMethodType.valueOf(event.getType()));
        method.setProvider(event.getProvider());
        method.setDescription(event.getDescription());
        method.setIsActive(event.getIsActive());
        method.setIconUrl(event.getIconUrl());
        method.setCode(event.getCode());
        paymentMethodRepository.save(method);
    }

    @Override
    public void deletePaymentMethod(PaymentMethodDeletedEvent event) {
        PaymentMethod method = paymentMethodRepository.findById(event.getId()).orElse(null);
        if (Objects.isNull(method)) {
            throw new RuntimeException(messageCommon.getMessage(ErrorCode.PaymentMethod.NOT_FOUND,event.getId()));
        }
        paymentMethodRepository.delete(method);
    }

    @Override
    public PaymentMethodDto findById(String paymentMethodId) {
        PaymentMethod method = paymentMethodRepository.findById(paymentMethodId).orElse(null);
        if (Objects.isNull(method)) {
            return null;
        }
        return modelMapper.map(method, PaymentMethodDto.class);
    }
}
