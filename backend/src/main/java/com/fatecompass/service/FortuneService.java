package com.fatecompass.service;

import com.fatecompass.entity.FortuneHistory;
import com.fatecompass.entity.User;
import com.fatecompass.repository.FortuneHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 算命服務類 - 提供生辰八字、姓名算命、運勢查詢等功能
 */
@Service
public class FortuneService {

    @Autowired
    private FortuneHistoryRepository fortuneHistoryRepository;
    
    @Autowired
    private CncharStrokeService cncharStrokeService;
    
    // 天干地支等常量
    private static final String[] HEAVENLY_STEMS = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    private static final String[] EARTHLY_BRANCHES = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
    private static final String[] ZODIAC_ANIMALS = {"鼠", "牛", "虎", "兔", "龍", "蛇", "馬", "羊", "猴", "雞", "狗", "豬"};
    private static final String[] FIVE_ELEMENTS = {"金", "木", "水", "火", "土"};
    
    // 姓名筆劃五行對應
    private static final Map<Integer, String> STROKE_ELEMENTS = new HashMap<>();
    static {
        // 簡化的筆劃五行對應規則
        STROKE_ELEMENTS.put(1, "木"); STROKE_ELEMENTS.put(2, "木");
        STROKE_ELEMENTS.put(3, "火"); STROKE_ELEMENTS.put(4, "火");
        STROKE_ELEMENTS.put(5, "土"); STROKE_ELEMENTS.put(6, "土");
        STROKE_ELEMENTS.put(7, "金"); STROKE_ELEMENTS.put(8, "金");
        STROKE_ELEMENTS.put(9, "水"); STROKE_ELEMENTS.put(0, "水");
    }
    

    
    /**
     * 生辰八字算命
     */
    public Map<String, Object> calculateBaZi(User user, LocalDateTime birthDateTime, String birthPlace) {
        Map<String, Object> result = new HashMap<>();
        
        // 計算年柱
        int year = birthDateTime.getYear();
        String yearStem = HEAVENLY_STEMS[(year - 4) % 10];
        String yearBranch = EARTHLY_BRANCHES[(year - 4) % 12];
        String yearPillar = yearStem + yearBranch;
        String zodiac = ZODIAC_ANIMALS[(year - 4) % 12];
        
        // 簡化的月日時柱計算
        int month = birthDateTime.getMonthValue();
        String monthPillar = HEAVENLY_STEMS[(month - 1) % 10] + EARTHLY_BRANCHES[(month - 1) % 12];
        
        int day = birthDateTime.getDayOfMonth();
        String dayPillar = HEAVENLY_STEMS[(day - 1) % 10] + EARTHLY_BRANCHES[(day - 1) % 12];
        
        int hour = birthDateTime.getHour();
        String hourPillar = HEAVENLY_STEMS[(hour / 2) % 10] + EARTHLY_BRANCHES[(hour / 2) % 12];
        
        // 五行分析
        Map<String, Integer> elementCount = analyzeElements();
        
        // 生成分析結果
        String analysis = generateBaZiAnalysis(yearPillar, monthPillar, dayPillar, hourPillar, zodiac, elementCount);
        
        // 計算綜合評分
        int score = calculateScore(elementCount);
        
        result.put("yearPillar", yearPillar);
        result.put("monthPillar", monthPillar);
        result.put("dayPillar", dayPillar);
        result.put("hourPillar", hourPillar);
        result.put("zodiac", zodiac);
        result.put("elements", elementCount);
        result.put("analysis", analysis);
        result.put("score", score);
        result.put("birthTime", birthDateTime.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH時")));
        result.put("birthPlace", birthPlace);
        
        // 保存算命記錄
        saveFortuneHistory(user, FortuneHistory.FortuneType.BAZI, 
                         birthDateTime.toString() + "|" + birthPlace, analysis, score);
        
        return result;
    }
    
    /**
     * 姓名算命
     */
    public Map<String, Object> calculateNameFortune(User user, String fullName) {
        Map<String, Object> result = new HashMap<>();
        
        System.out.println("🎯 開始姓名算命 - 輸入姓名: " + fullName);
        
        // 計算筆劃數
        int totalStrokes = calculateStrokes(fullName);
        String element = FIVE_ELEMENTS[totalStrokes % 5];
        
        System.out.println("🎯 五行計算 - 筆劃: " + totalStrokes + ", 五行: " + element);
        
        // 生成姓名分析
        String analysis = generateNameAnalysis(fullName, totalStrokes, element);
        Random random = new Random();
        int score = 60 + random.nextInt(31); // 60-90分
        
        result.put("name", fullName);
        result.put("totalStrokes", totalStrokes);
        result.put("element", element);
        result.put("analysis", analysis);
        result.put("score", score);
        
        System.out.println("🎯 最終結果 - 筆劃數: " + result.get("totalStrokes"));
        System.out.println("🎯 完整結果: " + result);
        
        // 保存算命記錄
        saveFortuneHistory(user, FortuneHistory.FortuneType.NAME, fullName, analysis, score);
        
        return result;
    }
    
    /**
     * 每日運勢查詢
     */
    public Map<String, Object> getDailyFortune(User user, String zodiac) {
        Map<String, Object> result = new HashMap<>();
        Random random = new Random();
        
        String[] luckLevels = {"大吉", "中吉", "小吉", "平", "小凶"};
        String overallLuck = luckLevels[random.nextInt(luckLevels.length)];
        
        int loveScore = 60 + random.nextInt(40);
        int careerScore = 60 + random.nextInt(40);
        int wealthScore = 60 + random.nextInt(40);
        int healthScore = 60 + random.nextInt(40);
        
        String suggestion = generateDailySuggestion(overallLuck, loveScore, careerScore, wealthScore, healthScore);
        
        result.put("zodiac", zodiac);
        result.put("date", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")));
        result.put("overallLuck", overallLuck);
        result.put("loveScore", loveScore);
        result.put("careerScore", careerScore);
        result.put("wealthScore", wealthScore);
        result.put("healthScore", healthScore);
        result.put("suggestion", suggestion);
        
        // 保存算命記錄
        saveFortuneHistory(user, FortuneHistory.FortuneType.DAILY, 
                zodiac + "|" + LocalDate.now(), suggestion, 
                (loveScore + careerScore + wealthScore + healthScore) / 4);
        
        return result;
    }
    
    /**
     * 獲取用戶算命歷史
     */
    public List<FortuneHistory> getUserFortuneHistory(Long userId) {
        return fortuneHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    // 私有輔助方法
    private Map<String, Integer> analyzeElements() {
        Map<String, Integer> elementCount = new HashMap<>();
        Random random = new Random();
        Arrays.stream(FIVE_ELEMENTS).forEach(element -> 
            elementCount.put(element, random.nextInt(3) + 1));
        return elementCount;
    }
    
    private String generateBaZiAnalysis(String year, String month, String day, String hour, 
                                       String zodiac, Map<String, Integer> elements) {
        StringBuilder analysis = new StringBuilder();
        analysis.append("您的生辰八字為：").append(year).append(" ").append(month)
                .append(" ").append(day).append(" ").append(hour).append("\n");
        analysis.append("生肖：").append(zodiac).append("\n\n");
        
        analysis.append("五行分析：\n");
        elements.forEach((element, count) -> 
            analysis.append(element).append("：").append(count).append(" "));
        analysis.append("\n\n");
        
        // 5種隨機性格特點模板
        String[] personalityTemplates = {
            "性格特點：您天性聰穎，思維敏捷，富有創造力。為人正直善良，樂於助人。",
            "性格特點：您性格沉穩內斂，做事謹慎周密，具有很強的責任心和執行力。", 
            "性格特點：您個性開朗活潑，溝通能力強，善於處理人際關係，具有領導才能。",
            "性格特點：您心思細膩，感性豐富，具有藝術天賦，對美有獨特的感知能力。",
            "性格特點：您意志堅定，目標明確，不輕易放棄，具有很強的抗壓能力。"
        };
        
        // 5種隨機事業運勢模板
        String[] careerTemplates = {
            "事業運勢：工作穩定向上，貴人相助，有升遷加薪的機會。宜從事文職或管理類工作。",
            "事業運勢：事業發展順利，創業運佳，適合自主創業或投資理財。財運亨通。",
            "事業運勢：工作中表現突出，深受上司賞識。適合從事技術性或專業性強的工作。",
            "事業運勢：事業平穩發展，雖無大起大落，但步步為營，前景看好。",
            "事業運勢：工作運勢旺盛，多有新的機遇和挑戰。適合拓展新業務或轉換跑道。"
        };
        
        // 5種隨機感情運勢模板  
        String[] loveTemplates = {
            "感情運勢：感情生活和諧美滿，與伴侶感情深厚。單身者有望遇到心儀對象。",
            "感情運勢：桃花運旺盛，感情機會多。已婚者夫妻恩愛，家庭和睦。",
            "感情運勢：感情路較為波折，需要耐心經營。真愛需要時間來證明。",
            "感情運勢：感情穩定發展，適合談婚論嫁。家庭生活幸福美滿。",
            "感情運勢：感情運勢一般，需要主動出擊。多參與社交活動有助感情發展。"
        };
        
        // 5種隨機健康運勢模板
        String[] healthTemplates = {
            "健康運勢：身體健康狀況良好，精力充沛。注意作息規律，適度運動。",
            "健康運勢：整體健康運佳，但需注意腸胃保養。多吃清淡食物，少熬夜。", 
            "健康運勢：身體狀況尚可，注意情緒調節，避免壓力過大影響睡眠。",
            "健康運勢：健康運勢平穩，注意季節變化，預防感冒。定期體檢很重要。",
            "健康運勢：體質較佳，抵抗力強。適合戶外運動，多接觸大自然。"
        };
        
        Random random = new Random();
        analysis.append(personalityTemplates[random.nextInt(personalityTemplates.length)]).append("\n");
        analysis.append(careerTemplates[random.nextInt(careerTemplates.length)]).append("\n");
        analysis.append(loveTemplates[random.nextInt(loveTemplates.length)]).append("\n");
        analysis.append(healthTemplates[random.nextInt(healthTemplates.length)]);
        
        return analysis.toString();
    }
    
    private String generateNameAnalysis(String name, int strokes, String element) {
        StringBuilder analysis = new StringBuilder();
        analysis.append("姓名：").append(name).append("\n");
        analysis.append("總筆劃：").append(strokes).append("畫\n");
        analysis.append("主要五行：").append(element).append("\n\n");
        
        // 5種隨機姓名寓意模板
        String[] meaningTemplates = {
            "姓名寓意：您的姓名數理暗示富貴榮華，一生多貴人相助，事業蒸蒸日上。",
            "姓名寓意：您的姓名暗示智慧過人，學識淵博，適合發展文化教育事業。",
            "姓名寓意：您的姓名象徵堅毅不拔，意志堅強，能在困境中開創新局。",
            "姓名寓意：您的姓名預示人緣極佳，善於交際，在團隊中能發揮重要作用。",
            "姓名寓意：您的姓名暗含創新精神，具有開拓進取的特質，適合創業發展。"
        };
        
        // 5種隨機性格影響模板
        String[] personalityTemplates = {
            "性格影響：性格溫和親切，富有同情心，容易得到他人信任和喜愛。",
            "性格影響：性格堅毅果斷，處事冷靜理性，具有很強的領導組織能力。", 
            "性格影響：性格活潑開朗，樂觀向上，善於化解困難，適應能力強。",
            "性格影響：性格謹慎細心，做事一絲不苟，責任心強，值得信賴。",
            "性格影響：性格獨立自主，思想前衛，不拘一格，具有創新思維。"
        };
        
        // 5種隨機運勢影響模板
        String[] luckTemplates = {
            "運勢影響：整體運勢佳，前景光明。財運亨通，事業有成，家庭幸福。",
            "運勢影響：運勢平穩上升，雖進展緩慢但根基穩固，晚年運勢特別好。",
            "運勢影響：早年運勢一般，中年後運勢轉旺，適合大器晚成型發展。",
            "運勢影響：運勢波動較大，需要積極面對挑戰，危機中往往蘊含轉機。",
            "運勢影響：運勢穩中有升，貴人運旺，適合與他人合作發展事業。"
        };
        
        Random random = new Random();
        analysis.append(meaningTemplates[random.nextInt(meaningTemplates.length)]).append("\n");
        analysis.append(personalityTemplates[random.nextInt(personalityTemplates.length)]).append("\n");
        analysis.append(luckTemplates[random.nextInt(luckTemplates.length)]);
        
        return analysis.toString();
    }
    
    private String generateDailySuggestion(String luck, int love, int career, int wealth, int health) {
        StringBuilder suggestion = new StringBuilder();
        suggestion.append("今日運勢：").append(luck).append("\n\n");
        suggestion.append("建議：\n");
        
        // 根據分數範圍給出不同的建議，增加多樣性
        Random random = new Random();
        
        // 感情運勢建議
        if (love >= 80) {
            String[] loveTips = {
                "• 感情運勢極佳，適合表白或求婚",
                "• 愛情甜蜜，與伴侶關係和諧",
                "• 桃花運旺盛，單身者易遇良緣",
                "• 感情穩定發展，可考慮進一步發展",
                "• 夫妻恩愛，家庭和睦幸福"
            };
            suggestion.append(loveTips[random.nextInt(loveTips.length)]).append("\n");
        } else if (love >= 60) {
            String[] loveTips = {
                "• 感情運勢平穩，需要用心經營",
                "• 多與伴侶溝通，增進彼此了解",
                "• 保持耐心，感情需要時間培養",
                "• 適合約會或增進感情的活動",
                "• 單身者可多參與社交活動"
            };
            suggestion.append(loveTips[random.nextInt(loveTips.length)]).append("\n");
        } else {
            String[] loveTips = {
                "• 感情運勢較弱，避免爭吵衝突",
                "• 多包容理解，化解感情危機",
                "• 不宜討論敏感話題，保持冷靜",
                "• 給彼此一些空間和時間",
                "• 單身者暫時不宜主動追求"
            };
            suggestion.append(loveTips[random.nextInt(loveTips.length)]).append("\n");
        }
        
        // 事業運勢建議
        if (career >= 80) {
            String[] careerTips = {
                "• 工作運勢極佳，適合承接重要項目",
                "• 表現突出，有升遷加薪機會",
                "• 適合提出新的想法或建議",
                "• 貴人運旺，容易得到上司賞識",
                "• 創業或投資項目有好的發展"
            };
            suggestion.append(careerTips[random.nextInt(careerTips.length)]).append("\n");
        } else if (career >= 60) {
            String[] careerTips = {
                "• 工作穩定進展，按部就班即可",
                "• 適合學習新技能提升自己",
                "• 與同事保持良好合作關係",
                "• 完成手頭工作，不宜操之過急",
                "• 可以規劃未來的職業發展"
            };
            suggestion.append(careerTips[random.nextInt(careerTips.length)]).append("\n");
        } else {
            String[] careerTips = {
                "• 工作上宜謹慎行事，避免出錯",
                "• 不宜做重大決定或冒險",
                "• 多聽取他人意見，三思而後行",
                "• 專注完成基本工作任務",
                "• 避免與上司或同事發生衝突"
            };
            suggestion.append(careerTips[random.nextInt(careerTips.length)]).append("\n");
        }
        
        // 財運建議
        if (wealth >= 80) {
            String[] wealthTips = {
                "• 財運極佳，投資理財有好收益",
                "• 適合購買或出售重要物品",
                "• 可考慮新的賺錢機會",
                "• 偏財運旺，可適度嘗試投資",
                "• 收入有增加的機會"
            };
            suggestion.append(wealthTips[random.nextInt(wealthTips.length)]).append("\n");
        } else if (wealth >= 60) {
            String[] wealthTips = {
                "• 財運平穩，收支基本平衡",
                "• 適合儲蓄，為未來做準備",
                "• 理性消費，避免衝動購物",
                "• 可學習理財投資知識",
                "• 小額投資需謹慎評估"
            };
            suggestion.append(wealthTips[random.nextInt(wealthTips.length)]).append("\n");
        } else {
            String[] wealthTips = {
                "• 財運較弱，避免大額投資",
                "• 控制支出，減少不必要花費",
                "• 不宜借貸或擔保他人",
                "• 謹慎處理金錢相關事務",
                "• 保守理財，以穩為主"
            };
            suggestion.append(wealthTips[random.nextInt(wealthTips.length)]).append("\n");
        }
        
        // 健康運勢建議
        if (health >= 80) {
            String[] healthTips = {
                "• 身體狀況極佳，精力充沛",
                "• 適合進行體能訓練或運動",
                "• 身體抵抗力強，不易生病",
                "• 可以嘗試新的健身方式",
                "• 保持良好的生活習慣"
            };
            suggestion.append(healthTips[random.nextInt(healthTips.length)]);
        } else if (health >= 60) {
            String[] healthTips = {
                "• 健康狀況良好，注意維持",
                "• 適度運動，保持身體活力",
                "• 注意飲食營養均衡",
                "• 保持充足睡眠和休息",
                "• 可進行輕度的戶外活動"
            };
            suggestion.append(healthTips[random.nextInt(healthTips.length)]);
        } else {
            String[] healthTips = {
                "• 注意身體健康，多休息",
                "• 避免過度勞累和熬夜",
                "• 飲食清淡，避免刺激性食物",
                "• 如有不適應及時就醫",
                "• 調節情緒，避免壓力過大"
            };
            suggestion.append(healthTips[random.nextInt(healthTips.length)]);
        }
        
        return suggestion.toString();
    }
    
    private void saveFortuneHistory(User user, FortuneHistory.FortuneType type, 
                                   String inputData, String resultData, int score) {
        FortuneHistory history = new FortuneHistory();
        history.setUser(user);
        history.setFortuneType(type);
        history.setInputData(inputData);
        history.setResultData(resultData);
        history.setScore(score);
        fortuneHistoryRepository.save(history);
    }
    
    /**
     * 計算繁體中文筆劃數
     * 強制只使用cnchar庫，不使用備用方案
     */
    private int calculateStrokes(String name) {
        // 使用cnchar微服務
        try {
            int strokes = cncharStrokeService.calculateTotalStrokes(name);
            System.out.println("===== 🔍 DEBUG: cnchar計算結果 =====");
            System.out.println("姓名: " + name);
            System.out.println("cnchar返回筆劃數: " + strokes);
            System.out.println("===================================");
            return strokes;
        } catch (Exception e) {
            System.err.println("❌ cnchar服務調用失敗: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("cnchar服務失敗: " + e.getMessage(), e);
        }
    }
    

    
    private int calculateScore(Map<String, Integer> elements) {
        // 生辰八字評分：65-85分之間，避免太高分
        Random random = new Random();
        return 65 + random.nextInt(21); // 65-85分
    }
} 