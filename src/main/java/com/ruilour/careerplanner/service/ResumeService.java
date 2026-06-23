package com.ruilour.careerplanner.service;

import com.ruilour.careerplanner.entity.ResumeRecord;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResumeService {

    /**
     * 上传简历并进行 AI 诊断
     */
    ResumeRecord uploadAndDiagnose(Long userId, MultipartFile file) throws Exception;

    /**
     * 获取用户历史简历诊断记录
     */
    List<ResumeRecord> getHistory(Long userId);

    /**
     * 获取诊断详情
     */
    ResumeRecord getDetail(Long id);

    /**
     * 删除诊断记录
     */
    boolean deleteRecord(Long id);
}