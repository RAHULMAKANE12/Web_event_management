package com.spoorthy.ems.dto;

import java.time.LocalDateTime;

public class EventRegistrationDTO {

    private Long id;
    private Long eventId;
    private String eventName;
    private Long userId;
    private String userName;
    private String userEmail;
    private String userRollNumber;
    private String userMobile;
    private LocalDateTime registeredAt;

    public EventRegistrationDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserRollNumber() { return userRollNumber; }
    public void setUserRollNumber(String userRollNumber) { this.userRollNumber = userRollNumber; }

    public String getUserMobile() { return userMobile; }
    public void setUserMobile(String userMobile) { this.userMobile = userMobile; }

    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }
}
