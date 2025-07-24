import api from './api';
import { ApiResponse, User, UserAddress } from '../types';

/**
 * 用户服务类
 */
export class UserService {
  /**
   * 获取用户信息
   */
  static async getUserInfo(userId: number): Promise<User> {
    const response = await api.get<ApiResponse<User>>(`/api/users/${userId}`);
    return response.data.data;
  }

  /**
   * 获取用户地址列表
   */
  static async getUserAddresses(userId: number): Promise<UserAddress[]> {
    const response = await api.get<ApiResponse<UserAddress[]>>(`/api/users/${userId}/addresses`);
    return response.data.data;
  }

  /**
   * 添加用户地址
   */
  static async addUserAddress(userId: number, address: Partial<UserAddress>): Promise<UserAddress> {
    const response = await api.post<ApiResponse<UserAddress>>(`/api/users/${userId}/addresses`, address);
    return response.data.data;
  }

  /**
   * 更新用户地址
   */
  static async updateUserAddress(userId: number, addressId: number, address: Partial<UserAddress>): Promise<UserAddress> {
    const response = await api.put<ApiResponse<UserAddress>>(`/api/users/${userId}/addresses/${addressId}`, address);
    return response.data.data;
  }

  /**
   * 删除用户地址
   */
  static async deleteUserAddress(userId: number, addressId: number): Promise<string> {
    const response = await api.delete<ApiResponse<string>>(`/api/users/${userId}/addresses/${addressId}`);
    return response.data.message;
  }

  /**
   * 设置默认地址
   */
  static async setDefaultAddress(userId: number, addressId: number): Promise<string> {
    const response = await api.put<ApiResponse<string>>(`/api/users/${userId}/addresses/${addressId}/default`);
    return response.data.message;
  }

  /**
   * 获取用户积分
   */
  static async getUserPoints(userId: number): Promise<number> {
    const response = await api.get<ApiResponse<number>>(`/api/users/${userId}/points`);
    return response.data.data;
  }

  /**
   * 使用积分
   */
  static async usePoints(userId: number, points: number): Promise<string> {
    const response = await api.post<ApiResponse<string>>(`/api/users/${userId}/points/use`, { points });
    return response.data.message;
  }
} 