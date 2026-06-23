package com.ruilour.careerplanner.service.impl;

import com.ruilour.careerplanner.entity.CareerRecommendation;
import com.ruilour.careerplanner.mapper.CareerRecommendationMapper;
import com.ruilour.careerplanner.service.CareerRecommendationService;
import com.ruilour.careerplanner.util.AIPromptUtil;
import com.ruilour.careerplanner.util.DeepSeekUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CareerRecommendationServiceImpl implements CareerRecommendationService {

    @Autowired
    private CareerRecommendationMapper mapper;

    @Override
    public CareerRecommendation generateRecommendation(Long userId, String jobTarget,
                                                       String currentSkills,
                                                       Integer experienceYears,
                                                       String education) throws Exception {
        // 1. 构建 Prompt
        String prompt = AIPromptUtil.buildCareerRecommendPrompt(
                jobTarget, currentSkills, experienceYears, education
        );

        // 2. 调用 AI
        String aiResponse = DeepSeekUtil.chat(prompt);

        // 3. 存入数据库
        CareerRecommendation record = new CareerRecommendation();
        record.setUserId(userId);
        record.setJobTarget(jobTarget);
        record.setCurrentSkills(currentSkills);
        record.setExperienceYears(experienceYears);
        record.setEducation(education);
        record.setRecommendation(aiResponse);
        mapper.insert(record);

        return record;
    }

    @Override
    public List<CareerRecommendation> getHistory(Long userId) {
        return mapper.selectByUserId(userId);
    }

    @Override
    public CareerRecommendation getDetail(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public boolean deleteRecord(Long id) {
        return mapper.deleteById(id) > 0;
    }
}