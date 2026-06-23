package com.ruilour.careerplanner.service.impl;

import com.ruilour.careerplanner.entity.ResumeRecord;
import com.ruilour.careerplanner.mapper.ResumeRecordMapper;
import com.ruilour.careerplanner.service.ResumeService;
import com.ruilour.careerplanner.util.AIPromptUtil;
import com.ruilour.careerplanner.util.DeepSeekUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ResumeServiceImpl implements ResumeService {

    @Autowired
    private ResumeRecordMapper mapper;

    @Value("${file.upload.dir:./uploads/resumes}")
    private String uploadDir;

    @Override
    public ResumeRecord uploadAndDiagnose(Long userId, MultipartFile file) throws Exception {
        // 1. 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "未命名文件";
        }

        // 2. 获取文件扩展名
        String extension = "";
        if (originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 3. 生成新文件名
        String newFilename = UUID.randomUUID().toString() + extension;

        // 4. 统一使用项目根目录下的 uploads/resumes 目录
        String projectRoot = System.getProperty("user.dir");
        String uploadPathStr = projectRoot + File.separator + "uploads" + File.separator + "resumes";

        Path uploadPath = Paths.get(uploadPathStr);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            System.out.println("📁 创建上传目录: " + uploadPathStr);
        }

        Path filePath = uploadPath.resolve(newFilename);
        String filePathStr = filePath.toString();
        System.out.println("📂 文件保存路径: " + filePathStr);

        // 5. 保存文件
        try {
            file.transferTo(filePath.toFile());
            System.out.println("✅ 文件保存成功: " + filePathStr);
        } catch (IOException e) {
            throw new Exception("文件保存失败: " + e.getMessage());
        }

        // 6. 检查文件是否存在
        if (!Files.exists(filePath)) {
            throw new Exception("文件保存失败，找不到文件: " + filePathStr);
        }

        // 7. 读取文件内容
        String content = readFileContent(filePath.toFile());
        if (content == null || content.isEmpty()) {
            throw new Exception("文件内容为空或无法解析，请上传正确格式的简历（支持 PDF、Word、TXT）");
        }

        // ✅ 打印解析出的内容，方便调试
        System.out.println("📄 解析出的简历内容（前500字符）:");
        System.out.println(content.length() > 500 ? content.substring(0, 500) + "..." : content);

        // 8. 调用 AI 诊断
        String prompt = AIPromptUtil.buildResumeDiagnosisPrompt(content);
        String diagnosis = DeepSeekUtil.chat(prompt);

        // 9. 提取分数
        int score = extractScore(diagnosis);

        // 10. 保存记录
        ResumeRecord record = new ResumeRecord();
        record.setUserId(userId);
        record.setFileName(originalFilename);
        record.setFilePath(filePathStr);
        record.setParsedContent(content);
        record.setDiagnosis(diagnosis);
        record.setScore(score);
        mapper.insert(record);

        return record;
    }

    @Override
    public List<ResumeRecord> getHistory(Long userId) {
        return mapper.selectByUserId(userId);
    }

    @Override
    public ResumeRecord getDetail(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public boolean deleteRecord(Long id) {
        return mapper.deleteById(id) > 0;
    }

    /**
     * 读取文件内容（支持 TXT、PDF、Word）
     */
    private String readFileContent(File file) throws IOException {
        String fileName = file.getName().toLowerCase();
        System.out.println("📖 读取文件: " + fileName);

        if (fileName.endsWith(".txt")) {
            return new String(Files.readAllBytes(file.toPath()), "UTF-8");
        } else if (fileName.endsWith(".pdf")) {
            return readPdfContent(file);
        } else if (fileName.endsWith(".docx")) {
            return readWordContent(file);
        } else if (fileName.endsWith(".doc")) {
            try {
                return readWordContent(file);
            } catch (Exception e) {
                return "【提示】.doc 格式较旧，建议转换为 .docx 或 PDF 格式上传";
            }
        } else {
            return "【提示】不支持的文件格式，请上传 TXT、PDF 或 Word 文档";
        }
    }

    /**
     * 读取 PDF 文件内容
     */
    private String readPdfContent(File file) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            if (text == null || text.trim().isEmpty()) {
                return "【提示】PDF 文件内容为空，请检查文件是否损坏或包含文字";
            }
            return text;
        } catch (Exception e) {
            throw new IOException("PDF 解析失败: " + e.getMessage());
        }
    }

    /**
     * ✅ 读取 Word 文件内容（支持段落 + 表格）
     */
    private String readWordContent(File file) throws IOException {
        try (XWPFDocument document = new XWPFDocument(Files.newInputStream(file.toPath()))) {
            StringBuilder sb = new StringBuilder();

            // 1. 读取所有段落
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText();
                if (text != null && !text.trim().isEmpty()) {
                    sb.append(text).append("\n");
                }
            }

            // 2. ✅ 读取所有表格（关键修复）
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        String cellText = cell.getText();
                        if (cellText != null && !cellText.trim().isEmpty()) {
                            sb.append(cellText).append(" ");
                        }
                    }
                    sb.append("\n");
                }
            }

            String result = sb.toString();
            if (result == null || result.trim().isEmpty()) {
                return "【提示】Word 文件内容为空，请检查文件是否包含文字";
            }
            return result;
        } catch (Exception e) {
            throw new IOException("Word 解析失败: " + e.getMessage());
        }
    }

    /**
     * 从 AI 诊断中提取分数
     */
    private int extractScore(String diagnosis) {
        if (diagnosis == null) {
            return 60;
        }
        Pattern pattern = Pattern.compile("(?:综合)?评分[：:](\\d+)");
        Matcher matcher = pattern.matcher(diagnosis);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                return 60;
            }
        }
        return 60;
    }
}