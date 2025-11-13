package com.backend.api.payment.service;


import com.backend.api.payment.dto.response.AdminPaymentResponse;
import com.backend.api.payment.dto.response.AdminPaymentSummaryResponse;
import com.backend.domain.payment.entity.Payment;
import com.backend.domain.payment.entity.PaymentStatus;
import com.backend.domain.payment.repository.PaymentRepository;
import com.backend.global.exception.ErrorCode;
import com.backend.global.exception.ErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminPaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public List<AdminPaymentResponse> getAllByOrderByApprovedAtDesc() {
        List<Payment> payments = paymentRepository.findAllByOrderByApprovedAtDesc();

        if (payments.isEmpty()) {
            throw new ErrorException(ErrorCode.PAYMENT_NOT_FOUND);
        }

        return payments.stream()
                .map(AdminPaymentResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AdminPaymentSummaryResponse getPaymentSummary(){
        long total = paymentRepository.count();

        if (total == 0) {
            throw new ErrorException(ErrorCode.PAYMENT_NOT_FOUND);
        }

        long successCount = paymentRepository.countByStatus(PaymentStatus.DONE);
        long totalRevenue = paymentRepository.findAll().stream()
                .filter(p->p.getStatus() == PaymentStatus.DONE)
                .mapToLong(Payment::getTotalAmount)
                .sum();

        return new AdminPaymentSummaryResponse(total, successCount, totalRevenue);
    }
}
