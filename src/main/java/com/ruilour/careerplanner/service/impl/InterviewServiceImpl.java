package com.ruilour.careerplanner.service.impl;

import com.ruilour.careerplanner.entity.InterviewRecord;
import com.ruilour.careerplanner.mapper.InterviewRecordMapper;
import com.ruilour.careerplanner.service.InterviewService;
import com.ruilour.careerplanner.util.AIPromptUtil;
import com.ruilour.careerplanner.util.DeepSeekUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class InterviewServiceImpl implements InterviewService {

    @Autowired
    private InterviewRecordMapper mapper;

    @Override
    public InterviewRecord generateQuestion(Long userId, String jobPosition, String difficulty) throws Exception {
        String prompt = AIPromptUtil.buildInterviewQuestionPrompt(jobPosition, difficulty);
        String aiResponse = DeepSeekUtil.chat(prompt);

        InterviewRecord record = new InterviewRecord();
        record.setUserId(userId);
        record.setJobPosition(jobPosition);
        record.setDifficulty(difficulty);
        record.setQuestion(aiResponse);
        record.setStatus("pending");
        mapper.insert(record);

        return record;
    }

    @Override
    public InterviewRecord submitAnswer(Long recordId, String userAnswer) throws Exception {
        InterviewRecord record = mapper.selectById(recordId);
        if (record == null) {
            throw new Exception("面试记录不存在");
        }

        String prompt = AIPromptUtil.buildInterviewScorePrompt(
                record.getJobPosition(),
                record.getQuestion(),
                userAnswer
        );

        String aiFeedback = DeepSeekUtil.chat(prompt);
        int score = extractScore(aiFeedback);

        record.setUserAnswer(userAnswer);
        record.setAiFeedback(aiFeedback);
        record.setScore(score);
        record.setStatus("completed");
        mapper.updateFeedback(record);

        return record;
    }

    @Override
    public List<InterviewRecord> getHistory(Long userId) {
        return mapper.selectByUserId(userId);
    }

    @Override
    public InterviewRecord getPendingQuestion(Long userId) {
        return mapper.selectPendingByUserId(userId);
    }

    @Override
    public boolean deleteRecord(Long id) {
        return mapper.deleteById(id) > 0;
    }

    /**
     * 从 AI 反馈中提取分数
     */
    private int extractScore(String feedback) {
        Pattern pattern = Pattern.compile("(?:综合)?评分[：:](\\d+)");
        Matcher matcher = pattern.matcher(feedback);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 60;
    }

    @Override
    public InterviewRecord getDetail(Long id) {
        // ✅ 修复：改成 mapper
        return mapper.selectById(id);
    }
}