package model;

import java.sql.Timestamp;

public class Promotion {
    private int id;
    private String code;
    private double discountPct;
    private String applyTo;
    private String targetType;
    private Integer targetId;
    private Timestamp validUntil;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public double getDiscountPct() { return discountPct; }
    public void setDiscountPct(double discountPct) { this.discountPct = discountPct; }

    public String getApplyTo() { return applyTo; }
    public void setApplyTo(String applyTo) { this.applyTo = applyTo; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public Integer getTargetId() { return targetId; }
    public void setTargetId(Integer targetId) { this.targetId = targetId; }

    public Timestamp getValidUntil() { return validUntil; }
    public void setValidUntil(Timestamp validUntil) { this.validUntil = validUntil; }
}
