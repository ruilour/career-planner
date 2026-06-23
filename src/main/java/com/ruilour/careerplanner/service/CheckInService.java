package com.ruilour.careerplanner.service;

import com.ruilour.careerplanner.entity.CheckIn;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CheckInService {
    // 执行打卡
    Map<String, Object> doCheckIn(Long userId);

    // 获取本月打卡记录
    List<CheckIn> getMonthRecords(Long userId);

    // 获取打卡统计
    Map<String, Object> getStats(Long userId);

    // 获取本月打卡日期列表（用于日历展示）
    List<Integer> getCheckInDays(Long userId, int year, int month);
}