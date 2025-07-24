import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { User } from '../../types';

interface UserState {
  currentUser: User | null;
  isLoggedIn: boolean;
  loginModalVisible: boolean;
  registerModalVisible: boolean;
  loading: boolean;
  error: string | null;
}

const initialState: UserState = {
  currentUser: null,
  isLoggedIn: false,
  loginModalVisible: false,
  registerModalVisible: false,
  loading: false,
  error: null,
};

const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    // 设置当前用户（模拟登录）
    setCurrentUser: (state, action: PayloadAction<User>) => {
      state.currentUser = action.payload;
      state.isLoggedIn = true;
      state.loginModalVisible = false;
      state.error = null;
    },
    
    // 清除当前用户（登出）
    clearCurrentUser: (state) => {
      state.currentUser = null;
      state.isLoggedIn = false;
    },
    
    // 显示登录模态框
    showLoginModal: (state) => {
      state.loginModalVisible = true;
      state.registerModalVisible = false;
    },
    
    // 隐藏登录模态框
    hideLoginModal: (state) => {
      state.loginModalVisible = false;
      state.error = null;
    },
    
    // 显示注册模态框
    showRegisterModal: (state) => {
      state.registerModalVisible = true;
      state.loginModalVisible = false;
    },
    
    // 隐藏注册模态框
    hideRegisterModal: (state) => {
      state.registerModalVisible = false;
      state.error = null;
    },
    
    // 设置加载状态
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.loading = action.payload;
    },
    
    // 设置错误信息
    setError: (state, action: PayloadAction<string>) => {
      state.error = action.payload;
      state.loading = false;
    },
    
    // 清除错误信息
    clearError: (state) => {
      state.error = null;
    },
    
    // 模拟登录
    mockLogin: (state, action: PayloadAction<{ username: string; password: string }>) => {
      const { username } = action.payload;
      
      // 模拟用户数据
      const mockUser: User = {
        id: 1,
        username,
        email: `${username}@example.com`,
        phone: '13800138000',
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      };
      
      state.currentUser = mockUser;
      state.isLoggedIn = true;
      state.loginModalVisible = false;
      state.error = null;
    },
    
    // 模拟注册
    mockRegister: (state, action: PayloadAction<{
      username: string;
      password: string;
      email: string;
      phone: string;
    }>) => {
      const { username, email, phone } = action.payload;
      
      // 模拟用户数据
      const mockUser: User = {
        id: Date.now(), // 使用时间戳作为模拟ID
        username,
        email,
        phone,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      };
      
      state.currentUser = mockUser;
      state.isLoggedIn = true;
      state.registerModalVisible = false;
      state.error = null;
    },
  },
});

export const {
  setCurrentUser,
  clearCurrentUser,
  showLoginModal,
  hideLoginModal,
  showRegisterModal,
  hideRegisterModal,
  setLoading,
  setError,
  clearError,
  mockLogin,
  mockRegister,
} = userSlice.actions;

export default userSlice.reducer; 