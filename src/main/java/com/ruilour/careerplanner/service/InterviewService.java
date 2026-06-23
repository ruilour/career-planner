package com.ruilour.careerplanner.service;

import com.ruilour.careerplanner.entity.InterviewRecord;
import java.util.List;

public interface InterviewService {

    /**
     * 生成面试题
     */
    InterviewRecord generateQuestion(Long userId, String jobPosition, String difficulty) throws Exception;

    /**
     * 提交答案并获取评分
     */
    InterviewRecord submitAnswer(Long recordId, String userAnswer) throws Exception;

    /**
     * 获取用户历史面试记录
     */
    List<InterviewRecord> getHistory(Long userId);

    /**
     * 获取面试详情
     */
    InterviewRecord getDetail(Long id);

    /**
     * 获取待回答的题目（用户还没回答的）
     */
    InterviewRecord getPendingQuestion(Long userId);

    /**
     * 删除面试记录
     */
    boolean deleteRecord(Long id);

}