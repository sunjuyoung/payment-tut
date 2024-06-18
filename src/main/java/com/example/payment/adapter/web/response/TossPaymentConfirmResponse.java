package com.example.payment.adapter.web.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class TossPaymentConfirmResponse {
    private String version;
    private String paymentKey;
    private String type;
    private String orderId;
    private String orderName;
    private String mId;
    private String currency;
    private String method;
    private double totalAmount;
    private double balanceAmount;
    private String status;
    private String requestedAt;
    private String approvedAt;
    private boolean useEscrow;
    private String lastTransactionKey;
    private double suppliedAmount;
    private double vat;
    private boolean cultureExpense;
    private double taxFreeAmount;
    private int taxExemptionAmount;
    private List<Cancel> cancels;
    private double cancelAmount;
    private String cancelReason;
    private double cancelTaxFreeAmount;
    private int cancelTaxExemptionAmount;
    private double refundableAmount;
    private double easyPayDiscountAmount;
    private String canceledAt;
    private String transactionKey;
    private String receiptKey;
    private String cancelStatus;
    private String cancelRequestId;
    private boolean isPartialCancelable;
    private Card card;
    private VirtualAccount virtualAccount;
    private RefundReceiveAccount refundReceiveAccount;
    private String secret;
    private MobilePhone mobilePhone;
    private String receiptUrl;

    // Nested classes for complex fields
    @Getter @Setter
    public  class Cancel {
        private double cancelAmount;
        private String cancelReason;
        private double taxFreeAmount;
        private int taxExemptionAmount;
        private double refundableAmount;
        private double easyPayDiscountAmount;
        private String canceledAt;
        private String transactionKey;
        private String receiptKey;
        private String cancelStatus;
        private String cancelRequestId;
        private boolean isPartialCancelable;

        // Getters and setters
    }
    @Getter @Setter
    public  class Card {
        private double amount;
        private String issuerCode;
        private String acquirerCode;
        private String number;
        private int installmentPlanMonths;
        private String approveNo;
        private boolean useCardPoint;
        private String cardType;
        private String ownerType;
        private String acquireStatus;
        private boolean isInterestFree;
        private String interestPayer;

        // Getters and setters
    }
    @Getter @Setter
    public  class VirtualAccount {
        private String accountType;
        private String accountNumber;
        private String bankCode;
        private String customerName;
        private String dueDate;
        private String refundStatus;
        private boolean expired;
        private String settlementStatus;

        // Getters and setters
    }
    @Getter @Setter
    public  class RefundReceiveAccount {
        private String bankCode;
        private String accountNumber;
        private String holderName;

        // Getters and setters
    }
    @Getter @Setter
    public  class MobilePhone {
        private String plain;
        private String masking;

        // Getters and setters
    }@Getter @Setter
    public  class GiftCertificate {
        private String approveNo;
        private String settlementStatus;

        // Getters and setters
    }
    @Getter @Setter
    public  class Transfer {
        private String bankCode;
        private String settlementStatus;

        // Getters and setters
    }
    @Getter @Setter
    public  class Receipt {
        private String url;

        // Getters and setters
    }
    @Getter @Setter
    public  class Checkout {
        private String url;

        // Getters and setters
    }
    @Getter @Setter
    public  class EasyPay {
        private String provider;
        private double amount;
        private double discountAmount;

        // Getters and setters
    }
    @Getter @Setter
    public  class Failure {
        private String code;
        private String message;

    }

    @Getter @Setter
    public  class CashReceipt {
        private String type;
        private String receiptKey;
        private String issueNumber;
        private String receiptUrl;
        private double amount;
        private double taxFreeAmount;

        // Getters and setters
    }
    @Getter @Setter
    public  class CashReceiptHistory {
        private String receiptKey;
        private String orderId;
        private String orderName;
        private String type;
        private String issueNumber;
        private String receiptUrl;
        private String businessNumber;
        private String transactionType;
        private int amount;
        private int taxFreeAmount;
        private String issueStatus;
        private Failure failure;
        private String customerIdentityNumber;
        private String requestedAt;

        // Getters and setters
    }
}
