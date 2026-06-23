package com.ruilour.careerplanner.util;

public class AIPromptUtil {

    /**
     * 职业推荐提示词
     */
    public static String buildCareerRecommendPrompt(String jobTarget, String currentSkills,
                                                    Integer experienceYears, String education) {
        return String.format("""
            你是一位资深职业规划师。请根据以下用户信息，生成一份详细的职业发展建议：
            
            【目标岗位】%s
            【当前技能】%s
            【工作经验】%d年
            【学历背景】%s
            
            请输出以下内容：
            1. 岗位匹配度分析（该岗位的核心能力要求，用户与岗位的差距）
            2. 学习路径规划（按优先级列出需要学习的技术栈，每个阶段建议时间）
            3. 项目实践建议（推荐2-3个练手项目，说明为什么推荐）
            4. 求职策略（简历亮点突出方向、目标公司类型、薪资范围参考）
            5. 未来3-5年职业发展路径
            
            用 Markdown 格式输出，结构清晰。
            """, jobTarget, currentSkills, experienceYears, education);
    }

    /**
     * 生成面试题提示词
     */
    public static String buildInterviewQuestionPrompt(String jobPosition, String difficulty) {
        String difficultyMap = switch (difficulty) {
            case "easy" -> "初级（适合应届生）";
            case "hard" -> "高级（资深/架构师级别）";
            default -> "中级（1-3年经验）";
        };
        return String.format("""
            你是一位资深技术面试官。请为【%s】岗位生成一道%s的面试题。
            
            要求：
            1. 题目应考察核心能力，不要偏难怪
            2. 附带考察要点（面试官想听到什么）
            3. 附带参考回答思路（不是标准答案，而是得分点）
            4. 标记题目难度等级：基础/进阶/挑战
            
            按以下格式输出：
            ### 题目
            [题目内容]
            
            ### 考察要点
            [考察点列表]
            
            ### 参考回答思路
            [回答要点]
            
            ### 难度等级
            [基础/进阶/挑战]
            """, jobPosition, difficultyMap);
    }

    /**
     * 面试答案评分提示词
     */
    public static String buildInterviewScorePrompt(String jobPosition, String question, String userAnswer) {
        return String.format("""
            你是一位资深技术面试官。请对以下面试回答进行评分和反馈：
            
            【岗位】%s
            【面试题】%s
            【候选人的回答】%s
            
            请输出：
            1. 综合评分（0-100分）
            2. 优点分析（回答中做对了什么）
            3. 不足之处（哪些地方需要改进）
            4. 优化建议（如何修改会更好）
            5. 参考答案（给出一个高分回答范例）
            
            用 Markdown 格式输出。
            """, jobPosition, question, userAnswer);
    }

    /**
     * 简历诊断提示词
     */
    public static String buildResumeDiagnosisPrompt(String resumeContent) {
        return String.format("""
            你是一位资深简历优化专家。请对以下简历进行诊断和优化建议：
            
            【简历内容】
            %s
            
            请输出：
            1. 简历综合评分（0-100分）
            2. 优点分析（当前简历做对了什么）
            3. 问题诊断（按严重程度列出问题，并说明为什么）
            4. 优化建议（具体到每个部分的修改方案）
            5. 关键词优化（推荐添加的行业关键词）
            6. 简历整体结构优化建议
            
            用 Markdown 格式输出。
            """, resumeContent);
    }
}