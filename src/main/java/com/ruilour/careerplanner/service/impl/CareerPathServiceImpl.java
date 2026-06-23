package com.ruilour.careerplanner.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruilour.careerplanner.entity.CareerPath;
import com.ruilour.careerplanner.mapper.CareerPathMapper;
import com.ruilour.careerplanner.service.CareerPathService;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CareerPathServiceImpl implements CareerPathService {

    @Autowired
    private CareerPathMapper careerPathMapper;

    @Value("${deepseek.api.key}")
    private String apiKey;

    private static final String DEEPSEEK_URL = "https://api.deepseek.com/v1/chat/completions";

    // ✅ 关键修改：配置带超时时间的 OkHttp 客户端
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)   // 连接超时：60秒
            .writeTimeout(60, TimeUnit.SECONDS)     // 写入超时：60秒
            .readTimeout(120, TimeUnit.SECONDS)     // 读取超时：120秒（AI生成较慢）
            .build();

    @Override
    public String generatePlan(Long userId, String jobName) throws Exception {
        // 1. 检查 API Key
        if (apiKey == null || apiKey.isEmpty() || apiKey.startsWith("sk-你的")) {
            throw new Exception("请先在 application.properties 中配置有效的 DeepSeek API Key");
        }

        // 2. 查缓存
        CareerPath cached = careerPathMapper.selectByUserIdAndJob(userId, jobName);
        if (cached != null) {
            return cached.getSteps();
        }

        // 3. 调用 AI（带超时）
        String aiResponse = callDeepSeekApi(jobName);

        // 4. 存入数据库
        CareerPath newPlan = new CareerPath();
        newPlan.setUserId(userId);
        newPlan.setJobName(jobName);
        newPlan.setSteps(aiResponse);
        careerPathMapper.insert(newPlan);

        return aiResponse;
    }

    @Override
    public List<CareerPath> getUserHistory(Long userId) {
        return careerPathMapper.selectByUserId(userId);
    }

    @Override
    public boolean deletePlan(Long planId) {
        return careerPathMapper.deleteById(planId) > 0;
    }

    private String callDeepSeekApi(String jobName) throws Exception {
        String prompt = "你是一位资深职业规划师。请为想成为【" + jobName + "】的零基础大学生，" +
                "制定一份详细的3个月学习计划。要求：\n" +
                "1. 按周输出（共12周）\n" +
                "2. 包含每个阶段需要学习的具体技术栈\n" +
                "3. 每周要有具体的学习目标和实践项目\n" +
                "4. 用 Markdown 格式输出，结构清晰";

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "deepseek-chat");

        JSONArray messages = new JSONArray();
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);
        requestBody.put("messages", messages);
        requestBody.put("stream", false);
        requestBody.put("max_tokens", 2048);

        Request request = new Request.Builder()
                .url(DEEPSEEK_URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(
                        requestBody.toJSONString(),
                        MediaType.parse("application/json")
                ))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "无响应体";
                throw new Exception("DeepSeek API 调用失败: " + response.code() + " - " + response.message() + "\n详情: " + errorBody);
            }

            String responseBody = response.body().string();
            JSONObject jsonResponse = JSON.parseObject(responseBody);
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices == null || choices.isEmpty()) {
                throw new Exception("DeepSeek API 返回数据格式异常: " + responseBody);
            }

            JSONObject choice = choices.getJSONObject(0);
            JSONObject message = choice.getJSONObject("message");
            return message.getString("content");
        }
    }
    @Override
    public CareerPath getDetail(Long id) {
        return careerPathMapper.selectById(id);
    }
}