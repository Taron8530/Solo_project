package com.example.solo_project;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class Payment {
    @Expose
    @SerializedName("aid")
    private String aid;
    @Expose
    @SerializedName("tid")
    private String tid;
    @Expose
    @SerializedName("cid")
    private String cid;
    @Expose
    @SerializedName("sid")
    private String sid;
    @Expose
    @SerializedName("partner_order_id")
    private String partnerOrderId;
    @Expose
    @SerializedName("partner_user_id")
    private String partnerUserId;
    @Expose
    @SerializedName("payment_method_type")
    private String paymentMethodType;
    @Expose
    @SerializedName("amount")
    private Amount amount;
    @Expose
    @SerializedName("card_info")
    private CardInfo cardInfo;
    @Expose
    @SerializedName("item_name")
    private String itemName;
    @Expose
    @SerializedName("item_code")
    private String itemCode;
    @Expose
    @SerializedName("quantity")
    private int quantity;
    @Expose
    @SerializedName("created_at")
    private LocalDateTime createdAt;
    @Expose
    @SerializedName("approved_at")
    private LocalDateTime approvedAt;
    @Expose
    @SerializedName("payload")
    private String payload;

    public class Amount {
        @SerializedName("total")
        @Expose
        private int total;
        @Expose
        @SerializedName("tax_free")
        private int taxFree;
        @Expose
        @SerializedName("vat")
        private int vat;
        @Expose
        @SerializedName("point")
        private int point;
        @Expose
        @SerializedName("discount")
        private int discount;

        public Amount(int total, int taxFree, int vat, int point, int discount) {
            this.total = total;
            this.taxFree = taxFree;
            this.vat = vat;
            this.point = point;
            this.discount = discount;
        }

        public int getTotal() {
            return total;
        }

        public int getTaxFree() {
            return taxFree;
        }

        public int getVat() {
            return vat;
        }

        public int getPoint() {
            return point;
        }
        public int getDiscount() {
            return discount;
        }

    }

    public class CardInfo {
        @Expose
        @SerializedName("purchase_corp")
        private String purchaseCorp;
        @Expose
        @SerializedName("purchase_corp_code")
        private String purchaseCorpCode;
        @Expose
        @SerializedName("issuer_corp")
        private String issuerCorp;
        @Expose
        @SerializedName("issuer_corp_code")
        private String issuerCorpCode;
        @Expose
        @SerializedName("bin")
        private String bin;
        @Expose
        @SerializedName("card_type")
        private String cardType;
        @Expose
        @SerializedName("install_month")
        private String installMonth;
        @Expose
        @SerializedName("approved_id")
        private String approvedId;
        @Expose
        @SerializedName("card_mid")
        private String cardMid;
        @Expose
        @SerializedName("interest_free_install")
        private String interestFreeInstall;
        @Expose
        @SerializedName("card_item_code")
        private String cardItemCode;

        public CardInfo(String purchaseCorp, String purchaseCorpCode, String issuerCorp, String issuerCorpCode,
                        String bin, String cardType, String installMonth, String approvedId, String cardMid,
                        String interestFreeInstall, String cardItemCode) {
            this.purchaseCorp = purchaseCorp;
            this.purchaseCorpCode = purchaseCorpCode;
            this.issuerCorp = issuerCorp;
            this.issuerCorpCode = issuerCorpCode;
            this.bin = bin;
            this.cardType = cardType;
            this.installMonth = installMonth;
            this.approvedId = approvedId;
            this.cardMid = cardMid;
            this.interestFreeInstall = interestFreeInstall;
            this.cardItemCode = cardItemCode;
        }

        public String getPurchaseCorp() {
            return purchaseCorp;
        }

        public String getPurchaseCorpCode() {
            return purchaseCorpCode;
        }

        public String getIssuerCorp() {
            return issuerCorp;
        }

        public String getIssuerCorpCode() {
            return issuerCorpCode;
        }

        public String getBin() {
            return bin;
        }

        public String getCardType() {
            return cardType;
        }
    }

    public Amount getAmount() {
        return amount;
    }

    public CardInfo getCardInfo() {
        return cardInfo;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getAid() {
        return aid;
    }

    public String getCid() {
        return cid;
    }

    public String getItemCode() {
        return itemCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getPartnerOrderId() {
        return partnerOrderId;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public String getItemName() {
        return itemName;
    }

    public String getPartnerUserId() {
        return partnerUserId;
    }

    public String getPayload() {
        return payload;
    }

    public String getPaymentMethodType() {
        return paymentMethodType;
    }

    public String getSid() {
        return sid;
    }

    public String getTid() {
        return tid;
    }
}