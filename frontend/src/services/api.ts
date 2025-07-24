import axios from 'axios';
import { ApiResponse } from '../types';

// 创建axios实例
const api = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    // 在这里可以添加token等认证信息
    console.log('发送请求:', config.method?.toUpperCase(), config.url);
    return config;
  },
  (error) => {
    console.error('请求错误:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    const data: ApiResponse = response.data;
    console.log('收到响应:', response.status, data.message);
    
    // 检查业务状态码
    if (data.code === 200) {
      return response;
    } else {
      console.error('业务错误:', data.message);
      return Promise.reject(new Error(data.message));
    }
  },
  (error) => {
    console.error('响应错误详情:', error);
    
    if (error.response) {
      // 服务器返回错误状态码
      const { status, statusText } = error.response;
      const message = `服务器错误 ${status}: ${statusText}`;
      console.error('服务器返回错误:', message);
      return Promise.reject(new Error(message));
    } else if (error.request) {
      // 请求发送但没有收到响应
      console.error('网络连接失败详情:', {
        url: error.config?.url,
        method: error.config?.method,
        baseURL: error.config?.baseURL,
        timeout: error.config?.timeout,
        message: error.message
      });
      return Promise.reject(new Error('网络连接失败，请检查后端服务是否启动'));
    } else {
      // 其他错误
      console.error('其他错误:', error.message);
      return Promise.reject(error);
    }
  }
);

export default api; 