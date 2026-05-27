package com.smartnote.service;

import com.smartnote.dto.NotificationVO;

import java.util.List;

public interface NotificationService {
    List<NotificationVO> getNotifications(Long userId);
}
