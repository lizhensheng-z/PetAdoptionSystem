package com.yr.pet.adoption.model.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 信用信息响应DTO
 * @author yr
 * @since 2026-01-01
 */
public class CreditInfoResponse {

    private Long userId;
    private String username;
    private Integer score;
    private Integer level;
    private String levelName;
    private String levelDescription;
    private LevelProgress progressToNextLevel;
    private CreditStatistics statistics;
    private List<CreditLogItem> recentActivities;
    private List<BadgeInfo> badges;
    private LocalDateTime lastCalcTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getLevelDescription() {
        return levelDescription;
    }

    public void setLevelDescription(String levelDescription) {
        this.levelDescription = levelDescription;
    }

    public LevelProgress getProgressToNextLevel() {
        return progressToNextLevel;
    }

    public void setProgressToNextLevel(LevelProgress progressToNextLevel) {
        this.progressToNextLevel = progressToNextLevel;
    }

    public CreditStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(CreditStatistics statistics) {
        this.statistics = statistics;
    }

    public List<CreditLogItem> getRecentActivities() {
        return recentActivities;
    }

    public void setRecentActivities(List<CreditLogItem> recentActivities) {
        this.recentActivities = recentActivities;
    }

    public List<BadgeInfo> getBadges() {
        return badges;
    }

    public void setBadges(List<BadgeInfo> badges) {
        this.badges = badges;
    }

    public LocalDateTime getLastCalcTime() {
        return lastCalcTime;
    }

    public void setLastCalcTime(LocalDateTime lastCalcTime) {
        this.lastCalcTime = lastCalcTime;
    }

    public static class LevelProgress {
        private Integer currentScore;
        private Integer nextLevelScore;
        private Integer remainingScore;
        private Integer percentage;

        public Integer getCurrentScore() {
            return currentScore;
        }

        public void setCurrentScore(Integer currentScore) {
            this.currentScore = currentScore;
        }

        public Integer getNextLevelScore() {
            return nextLevelScore;
        }

        public void setNextLevelScore(Integer nextLevelScore) {
            this.nextLevelScore = nextLevelScore;
        }

        public Integer getRemainingScore() {
            return remainingScore;
        }

        public void setRemainingScore(Integer remainingScore) {
            this.remainingScore = remainingScore;
        }

        public Integer getPercentage() {
            return percentage;
        }

        public void setPercentage(Integer percentage) {
            this.percentage = percentage;
        }
    }

    public static class CreditStatistics {
        private Integer totalApplications;
        private Integer successfulAdoptions;
        private Double successRate;
        private Integer checkinCount;
        private Integer averageCheckinDays;
        private Integer violations;
        private LocalDateTime lastCheckinTime;

        public Integer getTotalApplications() {
            return totalApplications;
        }

        public void setTotalApplications(Integer totalApplications) {
            this.totalApplications = totalApplications;
        }

        public Integer getSuccessfulAdoptions() {
            return successfulAdoptions;
        }

        public void setSuccessfulAdoptions(Integer successfulAdoptions) {
            this.successfulAdoptions = successfulAdoptions;
        }

        public Double getSuccessRate() {
            return successRate;
        }

        public void setSuccessRate(Double successRate) {
            this.successRate = successRate;
        }

        public Integer getCheckinCount() {
            return checkinCount;
        }

        public void setCheckinCount(Integer checkinCount) {
            this.checkinCount = checkinCount;
        }

        public Integer getAverageCheckinDays() {
            return averageCheckinDays;
        }

        public void setAverageCheckinDays(Integer averageCheckinDays) {
            this.averageCheckinDays = averageCheckinDays;
        }

        public Integer getViolations() {
            return violations;
        }

        public void setViolations(Integer violations) {
            this.violations = violations;
        }

        public LocalDateTime getLastCheckinTime() {
            return lastCheckinTime;
        }

        public void setLastCheckinTime(LocalDateTime lastCheckinTime) {
            this.lastCheckinTime = lastCheckinTime;
        }
    }

    public static class CreditLogItem {
        private Long logId;
        private Integer delta;
        private String reason;
        private String refType;
        private Long refId;
        private Integer beforeScore;
        private Integer afterScore;
        private LocalDateTime createTime;

        public Long getLogId() {
            return logId;
        }

        public void setLogId(Long logId) {
            this.logId = logId;
        }

        public Integer getDelta() {
            return delta;
        }

        public void setDelta(Integer delta) {
            this.delta = delta;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getRefType() {
            return refType;
        }

        public void setRefType(String refType) {
            this.refType = refType;
        }

        public Long getRefId() {
            return refId;
        }

        public void setRefId(Long refId) {
            this.refId = refId;
        }

        public Integer getBeforeScore() {
            return beforeScore;
        }

        public void setBeforeScore(Integer beforeScore) {
            this.beforeScore = beforeScore;
        }

        public Integer getAfterScore() {
            return afterScore;
        }

        public void setAfterScore(Integer afterScore) {
            this.afterScore = afterScore;
        }

        public LocalDateTime getCreateTime() {
            return createTime;
        }

        public void setCreateTime(LocalDateTime createTime) {
            this.createTime = createTime;
        }
    }

    public static class BadgeInfo {
        private Long id;
        private String name;
        private String description;
        private String icon;
        private LocalDateTime unlockedTime;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public LocalDateTime getUnlockedTime() {
            return unlockedTime;
        }

        public void setUnlockedTime(LocalDateTime unlockedTime) {
            this.unlockedTime = unlockedTime;
        }
    }
}