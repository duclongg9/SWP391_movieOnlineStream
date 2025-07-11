package model;

import java.sql.Timestamp;

public class Purchase {
    private int id;
    private Integer filmId;
    private Integer packageId;
    private Timestamp purchasedAt;
    private Timestamp expiredAt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getFilmId() { return filmId; }
    public void setFilmId(Integer filmId) { this.filmId = filmId; }

    public Integer getPackageId() { return packageId; }
    public void setPackageId(Integer packageId) { this.packageId = packageId; }

    public Timestamp getPurchasedAt() { return purchasedAt; }
    public void setPurchasedAt(Timestamp purchasedAt) { this.purchasedAt = purchasedAt; }

    public Timestamp getExpiredAt() { return expiredAt; }
    public void setExpiredAt(Timestamp expiredAt) { this.expiredAt = expiredAt; }
}