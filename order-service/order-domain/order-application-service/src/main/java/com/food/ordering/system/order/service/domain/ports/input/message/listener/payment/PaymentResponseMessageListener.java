package com.food.ordering.system.order.service.domain.ports.input.message.listener.payment;

import com.food.ordering.system.order.service.domain.dto.message.PaymentReponse;

public interface PaymentResponseMessageListener {
    void paymentCompleted(PaymentReponse paymentReponse);

    void paymentCancelled(PaymentReponse paymentReponse);
}
