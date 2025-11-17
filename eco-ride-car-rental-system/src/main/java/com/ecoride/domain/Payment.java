package com.ecoride.domain;

import java.math.BigDecimal;

public class Payment {

    private String paymentId;
    private BigDecimal amount;
    private String method;
    private String status;

    public Payment(String id, BigDecimal amount, String method, String status) {
        this.paymentId = id;
        this.amount = amount;
        this.method = method;
        this.status = status;
    }

    public String getPaymentId() { return paymentId; }
    public BigDecimal getAmount() { return amount; }
    public String getMethod() { return method; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return "Payment {" +
                "id='" + paymentId + '\'' +
                ", amount=" + amount +
                ", method='" + method + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
