package com.example.solo_project;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class Payment {
    @SerializedName("aid")
    private String aid;

    @SerializedName("tid")
    private String tid;

    @SerializedName("cid")
    private String cid;

    @SerializedName("sid")
    private String sid;

    @SerializedName("partner_order_id")
    private String partnerOrderId;

    @SerializedName("partner_user_id")
    private String partnerUserId;

    @SerializedName("payment_method_type")
    private String paymentMethodType;

    @SerializedName("amount")
    private Amount amount;

    @SerializedName("card_info")
    private CardInfo cardInfo;

    @SerializedName("item_name")
    private String itemName;

    @SerializedName("item_code")
    private String itemCode;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("created_at")
    private LocalDateTime createdAt;

    @SerializedName("approved_at")
    private LocalDateTime approvedAt;

    @SerializedName("payload")
    private String payload;

    public class Amount {
        @SerializedName("total")
        private int total;

        @SerializedName("tax_free")
        private int taxFree;

        @SerializedName("vat")
        private int vat;

        @SerializedName("point")
        private int point;

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

        public void setTotal(int total) {
            this.total = total;
        }

        public int getTaxFree() {
            return taxFree;
        }

        public void setTaxFree(int taxFree) {
            this.taxFree = taxFree;
        }

        public int getVat() {
            return vat;
        }

        public void setVat(int vat) {
            this.vat = vat;
        }

        public int getPoint() {
            return point;
        }

        public void setPoint(int point) {
            this.point = point;
        }

        public int getDiscount() {
            return discount;
        }

        public void setDiscount(int discount) {
            this.discount = discount;
        }
    }

    public class CardInfo {
        @SerializedName("purchase_corp")
        private String purchaseCorp;

        @SerializedName("purchase_corp_code")
        private String purchaseCorpCode;

        @SerializedName("issuer_corp")
        private String issuerCorp;

        @SerializedName("issuer_corp_code")
        private String issuerCorpCode;

        @SerializedName("bin")
        private String bin;

        @SerializedName("card_type")
        private String cardType;

        @SerializedName("install_month")
        private String installMonth;

        @SerializedName("approved_id")
        private String approvedId;

        @SerializedName("card_mid")
        private String cardMid;

        @SerializedName("interest_free_install")
        private String interestFreeInstall;

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

        public void setPurchaseCorp(String purchaseCorp) {
            this.purchaseCorp = purchaseCorp;
        }

        public String getPurchaseCorpCode() {
            return purchaseCorpCode;
        }

        public void setPurchaseCorpCode(String purchaseCorpCode) {
            this.purchaseCorpCode = purchaseCorpCode;
        }

        public String getIssuerCorp() {
            return issuerCorp;
        }

        public void setIssuerCorp(String issuerCorp) {
            this.issuerCorp = issuerCorp;
        }

        public String getIssuerCorpCode() {
            return issuerCorpCode;
        }

        public void setIssuerCorpCode(String issuerCorpCode) {
            this.issuerCorpCode = issuerCorpCode;
        }

        public String getBin() {
            return bin;
        }

        public void setBin(String bin) {
            this.bin = bin;
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