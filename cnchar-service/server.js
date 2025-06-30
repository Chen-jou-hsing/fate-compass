const express = require('express');
const cors = require('cors');
const cnchar = require('cnchar');
require('cnchar-trad'); // 載入繁體字支援

const app = express();
const PORT = process.env.PORT || 3001;

// 中間件
app.use(cors());
app.use(express.json());

// 健康檢查
app.get('/health', (req, res) => {
  res.json({ 
    status: 'healthy', 
    service: 'cnchar-stroke-service',
    version: '1.0.0',
    timestamp: new Date().toISOString()
  });
});

// 計算單個字符筆劃
app.get('/stroke/:char', (req, res) => {
  try {
    const char = req.params.char;
    
    if (!char || char.length !== 1) {
      return res.status(400).json({
        error: '請提供單個字符',
        example: '/stroke/王'
      });
    }

    const strokes = cnchar.stroke(char);
    const pinyin = cnchar.spell(char);
    const radical = cnchar.radical(char);
    
    res.json({
      character: char,
      strokes: strokes,
      pinyin: pinyin,
      radical: radical,
      timestamp: new Date().toISOString()
    });
    
  } catch (error) {
    console.error('筆劃計算錯誤:', error);
    res.status(500).json({
      error: '筆劃計算失敗',
      message: error.message
    });
  }
});

// 計算字符串總筆劃
app.post('/strokes', (req, res) => {
  try {
    const { text } = req.body;
    
    if (!text || typeof text !== 'string') {
      return res.status(400).json({
        error: '請在請求體中提供text字段',
        example: '{ "text": "王明華" }'
      });
    }

    const characters = text.split('');
    let totalStrokes = 0;
    const details = [];
    
    for (const char of characters) {
      if (char.trim()) { // 忽略空白字符
        const strokes = cnchar.stroke(char);
        totalStrokes += strokes;
        
        details.push({
          character: char,
          strokes: strokes,
          pinyin: cnchar.spell(char),
          radical: cnchar.radical(char)
        });
      }
    }
    
    res.json({
      text: text,
      totalStrokes: totalStrokes,
      characterCount: details.length,
      details: details,
      timestamp: new Date().toISOString()
    });
    
  } catch (error) {
    console.error('筆劃計算錯誤:', error);
    res.status(500).json({
      error: '筆劃計算失敗',
      message: error.message
    });
  }
});

// 批量查詢
app.post('/batch', (req, res) => {
  try {
    const { names } = req.body;
    
    if (!Array.isArray(names)) {
      return res.status(400).json({
        error: '請提供names數組',
        example: '{ "names": ["王明華", "陳美麗"] }'
      });
    }

    const results = names.map(name => {
      const characters = name.split('');
      let totalStrokes = 0;
      const details = [];
      
      for (const char of characters) {
        if (char.trim()) {
          const strokes = cnchar.stroke(char);
          totalStrokes += strokes;
          
          details.push({
            character: char,
            strokes: strokes
          });
        }
      }
      
      return {
        name: name,
        totalStrokes: totalStrokes,
        details: details
      };
    });
    
    res.json({
      results: results,
      count: results.length,
      timestamp: new Date().toISOString()
    });
    
  } catch (error) {
    console.error('批量計算錯誤:', error);
    res.status(500).json({
      error: '批量計算失敗',
      message: error.message
    });
  }
});

// API文檔
app.get('/', (req, res) => {
  res.json({
    service: 'cnchar筆劃計算服務',
    version: '1.0.0',
    endpoints: {
      'GET /health': '健康檢查',
      'GET /stroke/:char': '計算單個字符筆劃',
      'POST /strokes': '計算字符串總筆劃 { "text": "王明華" }',
      'POST /batch': '批量計算 { "names": ["王明華", "陳美麗"] }'
    },
    examples: {
      singleChar: 'GET /stroke/王',
      string: 'POST /strokes with {"text": "王明華"}',
      batch: 'POST /batch with {"names": ["王明華", "陳美麗"]}'
    }
  });
});

// 啟動服務器
app.listen(PORT, () => {
  console.log(`🚀 cnchar筆劃計算服務已啟動`);
  console.log(`📍 服務地址: http://localhost:${PORT}`);
  console.log(`📚 API文檔: http://localhost:${PORT}`);
  console.log(`💖 使用cnchar庫進行繁體中文筆劃計算`);
});

// 優雅關閉
process.on('SIGTERM', () => {
  console.log('收到SIGTERM信號，正在優雅關閉...');
  process.exit(0);
});

process.on('SIGINT', () => {
  console.log('收到SIGINT信號，正在優雅關閉...');
  process.exit(0);
}); 