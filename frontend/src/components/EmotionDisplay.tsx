import React from 'react';
import { Tooltip, Progress, Space } from 'antd';
import {
  SmileOutlined,
  MehOutlined,
  FrownOutlined,
  ExclamationCircleOutlined,
  ArrowUpOutlined,
  ArrowDownOutlined,
  MinusOutlined
} from '@ant-design/icons';
import './EmotionDisplay.css';

export interface EmotionData {
  type: 'POSITIVE' | 'NEUTRAL' | 'NEGATIVE';
  intensity: number;
  keywords?: string[];
  trend?: 'IMPROVING' | 'STABLE' | 'DETERIORATING';
}

interface EmotionDisplayProps {
  emotion: EmotionData;
  showDetails?: boolean;
  size?: 'small' | 'default' | 'large';
}

const EmotionDisplay: React.FC<EmotionDisplayProps> = ({
  emotion,
  showDetails = false,
  size = 'default'
}) => {
  const { type, intensity, keywords = [], trend = 'STABLE' } = emotion;

  // 获取情绪图标
  const getEmotionIcon = () => {
    switch (type) {
      case 'POSITIVE':
        return <SmileOutlined className="emotion-icon positive" />;
      case 'NEGATIVE':
        return <FrownOutlined className="emotion-icon negative" />;
      case 'NEUTRAL':
      default:
        return <MehOutlined className="emotion-icon neutral" />;
    }
  };

  // 获取情绪文本
  const getEmotionText = () => {
    switch (type) {
      case 'POSITIVE':
        return intensity >= 5 ? '非常满意' : '满意';
      case 'NEGATIVE':
        return intensity <= 1 ? '非常不满' : '不满';
      case 'NEUTRAL':
      default:
        return '中性';
    }
  };

  // 获取情绪进度条颜色
  const getProgressColor = () => {
    switch (type) {
      case 'POSITIVE':
        return '#52c41a';
      case 'NEGATIVE':
        return '#ff4d4f';
      case 'NEUTRAL':
      default:
        return '#1890ff';
    }
  };

  // 获取情绪强度百分比
  const getIntensityPercent = () => {
    return (intensity / 5) * 100;
  };

  // 获取趋势图标
  const getTrendIcon = () => {
    switch (trend) {
      case 'IMPROVING':
        return <ArrowUpOutlined className="trend-icon improving" />;
      case 'DETERIORATING':
        return <ArrowDownOutlined className="trend-icon deteriorating" />;
      case 'STABLE':
      default:
        return <MinusOutlined className="trend-icon stable" />;
    }
  };

  // 获取趋势文本
  const getTrendText = () => {
    switch (trend) {
      case 'IMPROVING':
        return '改善中';
      case 'DETERIORATING':
        return '恶化中';
      case 'STABLE':
      default:
        return '稳定';
    }
  };

  // 简洁模式
  if (!showDetails) {
    return (
      <Tooltip
        title={`情绪：${getEmotionText()}，强度：${intensity}/5，趋势：${getTrendText()}`}
      >
        <div className={`emotion-indicator ${size}`}>
          {getEmotionIcon()}
          {getTrendIcon()}
        </div>
      </Tooltip>
    );
  }

  // 详细模式
  return (
    <div className="emotion-display">
      <div className="emotion-header">
        <div className="emotion-type">
          {getEmotionIcon()}
          <span className="emotion-text">{getEmotionText()}</span>
        </div>
        <div className="emotion-trend">
          {getTrendIcon()}
          <span className="trend-text">{getTrendText()}</span>
        </div>
      </div>

      <div className="emotion-intensity">
        <Progress
          percent={getIntensityPercent()}
          strokeColor={getProgressColor()}
          showInfo={false}
          size="small"
        />
        <div className="intensity-label">
          情绪强度: {intensity}/5
        </div>
      </div>

      {keywords.length > 0 && (
        <div className="emotion-keywords">
          <Space wrap>
            {keywords.map((keyword, index) => (
              <span key={index} className="keyword-tag">
                {keyword}
              </span>
            ))}
          </Space>
        </div>
      )}

      {type === 'NEGATIVE' && intensity <= 2 && (
        <div className="emotion-warning">
          <ExclamationCircleOutlined /> 用户情绪较为负面，请谨慎回复
        </div>
      )}
    </div>
  );
};

export default EmotionDisplay; 