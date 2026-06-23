package com.ruilour.careerplanner.service.impl;

import com.ruilour.careerplanner.entity.CheckIn;
import com.ruilour.careerplanner.mapper.CheckInMapper;
import com.ruilour.careerplanner.service.CheckInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CheckInServiceImpl implements CheckInService {

    @Autowired
    private CheckInMapper checkInMapper;

    @Override
    @Transactional
    public Map<String, Object> doCheckIn(Long userId) {
        Map<String, Object> result = new HashMap<>();
        LocalDate today = LocalDate.now();

        // 1. 检查今天是否已打卡
        CheckIn todayCheck = checkInMapper.selectByUserIdAndDate(userId, today);
        if (todayCheck != null) {
            result.put("success", false);
            result.put("message", "今天已经打卡了！");
            result.put("continuousDays", todayCheck.getContinuousDays());
            return result;
        }

        // 2. 计算连续打卡天数
        int continuousDays = 1;
        LocalDate yesterday = today.minusDays(1);
        CheckIn yesterdayCheck = checkInMapper.selectByUserIdAndDate(userId, yesterday);
        if (yesterdayCheck != null) {
            continuousDays = yesterdayCheck.getContinuousDays() + 1;
        }

        // 3. 保存打卡记录
        CheckIn checkIn = new CheckIn();
        checkIn.setUserId(userId);
        checkIn.setCheckInDate(today);
        checkIn.setContinuousDays(continuousDays);
        checkInMapper.insert(checkIn);

        // 4. 获取总打卡天数
        int totalDays = checkInMapper.countByUserId(userId);

        result.put("success", true);
        result.put("message", "🎉 打卡成功！");
        result.put("continuousDays", continuousDays);
        result.put("totalDays", totalDays);
        return result;
    }

    @Override
    public List<CheckIn> getMonthRecords(Long userId) {
        LocalDate now = LocalDate.now();
        return checkInMapper.selectByUserIdAndMonth(userId, now.getYear(), now.getMonthValue());
    }

    @Override
    public Map<String, Object> getStats(Long userId) {
        Map<String, Object> result = new HashMap<>();

        // 总打卡天数
        int totalDays = checkInMapper.countByUserId(userId);

        // 本月打卡天数
        LocalDate now = LocalDate.now();
        List<CheckIn> monthRecords = checkInMapper.selectByUserIdAndMonth(userId, now.getYear(), now.getMonthValue());
        int monthDays = monthRecords.size();

        // 当前连续天数
        int continuousDays = 0;
        CheckIn last = checkInMapper.selectLastByUserId(userId);
        if (last != null) {
            LocalDate today = LocalDate.now();
            if (last.getCheckInDate().equals(today)) {
                continuousDays = last.getContinuousDays();
            } else if (last.getCheckInDate().equals(today.minusDays(1))) {
                continuousDays = last.getContinuousDays();
            }
            // 如果最后打卡日期不是今天或昨天，连续天数重置为0
            if (!last.getCheckInDate().equals(today) && !last.getCheckInDate().equals(today.minusDays(1))) {
                continuousDays = 0;
            }
        }

        result.put("totalDays", totalDays);
        result.put("monthDays", monthDays);
        result.put("continuousDays", continuousDays);
        return result;
    }

    @Override
    public List<Integer> getCheckInDays(Long userId, int year, int month) {
        List<CheckIn> records = checkInMapper.selectByUserIdAndMonth(userId, year, month);
        return records.stream()
                .map(r -> r.getCheckInDate().getDayOfMonth())
                .collect(Collectors.toList());
    }
}