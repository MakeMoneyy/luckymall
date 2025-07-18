import axios from 'axios';

export interface ChatRequest {
  userId: string;
  sessionId: string;
  message: string;
  context?: Record<string, any>;
}

export interface ChatResponse {
  result: string;
  sessionId: string;
  responseTime: number;
  cacheHit: boolean;
  error?: string;
}

// 创建axios实例
const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || '',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * 发送聊天消息
 * @param request 聊天请求参数
 * @returns 聊天响应
 */
export const sendChatMessage = async (request: ChatRequest): Promise<ChatResponse> => {
  try {
    const response = await api.post<ChatResponse>('/api/chat', request);
    return response.data;
  } catch (error) {
    console.error('Error sending chat message:', error);
    throw error;
  }
};

/**
 * 获取流式聊天响应
 * @param request 聊天请求参数
 * @returns 聊天响应
 */
export const sendStreamChatMessage = async (request: ChatRequest): Promise<ChatResponse> => {
  try {
    const { userId, sessionId, message, context } = request;
    const params = new URLSearchParams();
    params.append('userId', userId);
    if (sessionId) params.append('sessionId', sessionId);
    params.append('message', message);
    if (context) params.append('context', JSON.stringify(context));
    
    const response = await api.get<ChatResponse>(`/api/chat/stream?${params.toString()}`);
    return response.data;
  } catch (error) {
    console.error('Error sending stream chat message:', error);
    throw error;
  }
}; 