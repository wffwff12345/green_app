package com.example.green_app.model;

import java.math.BigDecimal;
import java.util.Date;

public class Location {
    private static Location location;

    /**
     * 巡线工单ID
     */
    private Long patrolId;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;
    private Date createTime;

    public Long getPatrolId() {
        return patrolId;
    }

    public void setPatrolId(Long patrolId) {
        this.patrolId = patrolId;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public static Location Instance() {
        if (location == null) {
            synchronized (Location.class) {
                if (location == null) {
                    location = new Location();
                }
            }
        }
        return location;
    }
}
