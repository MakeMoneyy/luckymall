import React, { useState, useEffect } from 'react';
import { Input, Button, Tag, Tooltip, Space, Divider } from 'antd';
import { SearchOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import './NaturalLanguageInput.css';

interface NaturalLanguageInputProps {
  onSearch: (query: string) => void;
  loading?: boolean;
  placeholder?: string;
}

// 常见问题列表
const COMMON_QUESTIONS = [
  { text: '查询我的订单', category: 'order' },
  { text: '我的物流信息', category: 'logistics' },
  { text: '退换货政策', category: 'return' },
  { text: '积分查询', category: 'points' },
  { text: '分期付款', category: 'payment' }
];

// 语义查询示例
const SEMANTIC_EXAMPLES = [
  '查询最近三天的订单',
  '昨天买的手机什么时候发货',
  '怎么申请退款',
  '我的积分可以兑换什么',
  '这个商品可以分期吗'
];

const NaturalLanguageInput: React.FC<NaturalLanguageInputProps> = ({
  onSearch,
  loading = false,
  placeholder = '您可以直接询问如"查询我最近的订单"或"我想退货"'
}) => {
  const [value, setValue] = useState<string>('');
  const [showExamples, setShowExamples] = useState<boolean>(false);
  const [suggestions, setSuggestions] = useState<string[]>([]);

  // 当输入变化时提供智能建议
  useEffect(() => {
    if (value.length > 1) {
      // 简单的关键词匹配来提供建议
      const matchedSuggestions = SEMANTIC_EXAMPLES.filter(example => 
        example.toLowerCase().includes(value.toLowerCase())
      ).slice(0, 3);
      
      setSuggestions(matchedSuggestions);
    } else {
      setSuggestions([]);
    }
  }, [value]);

  // 处理输入变化
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setValue(e.target.value);
  };

  // 处理搜索
  const handleSearch = () => {
    if (value.trim()) {
      onSearch(value.trim());
      setValue('');
    }
  };

  // 处理按键事件
  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  // 处理快捷问题点击
  const handleQuestionClick = (question: string) => {
    onSearch(question);
  };

  // 处理建议点击
  const handleSuggestionClick = (suggestion: string) => {
    onSearch(suggestion);
    setValue('');
    setSuggestions([]);
  };

  return (
    <div className="natural-language-input">
      <div className="input-container">
        <Input
          value={value}
          onChange={handleInputChange}
          onKeyPress={handleKeyPress}
          placeholder={placeholder}
          disabled={loading}
          suffix={
            <Tooltip title="您可以用自然语言提问，例如'查询我最近的订单'">
              <QuestionCircleOutlined
                style={{ color: 'rgba(0,0,0,.45)' }}
                onClick={() => setShowExamples(!showExamples)}
              />
            </Tooltip>
          }
        />
        <Button
          type="primary"
          icon={<SearchOutlined />}
          onClick={handleSearch}
          loading={loading}
          disabled={!value.trim()}
        >
          搜索
        </Button>
      </div>

      {/* 智能建议 */}
      {suggestions.length > 0 && (
        <div className="suggestions">
          <div className="suggestions-title">您是否想问：</div>
          <Space>
            {suggestions.map((suggestion, index) => (
              <Tag
                key={index}
                color="blue"
                className="suggestion-tag"
                onClick={() => handleSuggestionClick(suggestion)}
              >
                {suggestion}
              </Tag>
            ))}
          </Space>
        </div>
      )}

      {/* 示例和常见问题 */}
      {showExamples && (
        <div className="examples-container">
          <Divider orientation="left">常见问题</Divider>
          <div className="common-questions">
            {COMMON_QUESTIONS.map((question, index) => (
              <Button
                key={index}
                type="default"
                size="small"
                className={`question-button ${question.category}`}
                onClick={() => handleQuestionClick(question.text)}
              >
                {question.text}
              </Button>
            ))}
          </div>
          
          <Divider orientation="left">语义查询示例</Divider>
          <div className="semantic-examples">
            {SEMANTIC_EXAMPLES.map((example, index) => (
              <div key={index} className="example-item">
                <span className="example-icon">💡</span>
                <span className="example-text">{example}</span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default NaturalLanguageInput; 