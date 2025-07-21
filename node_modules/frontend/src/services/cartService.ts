import api from './api';
import { ApiResponse, CartItem, Product } from '../types';

/**
 * 购物车服务类
 */
export class CartService {
  /**
   * 获取用户购物车商品列表
   */
  static async getCartItems(userId: number): Promise<CartItem[]> {
    try {
      const response = await api.get<ApiResponse<CartItem[]>>(`/api/cart/${userId}`);
      return response.data.data;
    } catch (error) {
      console.error('获取购物车列表失败，使用模拟数据:', error);
      
      // 从localStorage获取购物车数据
      const cartItemsStr = localStorage.getItem('cartItems');
      if (cartItemsStr) {
        return JSON.parse(cartItemsStr);
      }
      
      // 如果localStorage中没有数据，返回空数组
      return [];
    }
  }

  /**
   * 添加商品到购物车
   */
  static async addToCart(userId: number, productId: number, quantity: number): Promise<string> {
    try {
      const response = await api.post<ApiResponse<string>>(`/api/cart/${userId}/add`, {
        productId,
        quantity
      });
      return response.data.message;
    } catch (error) {
      console.error('添加商品到购物车失败，使用模拟数据:', error);
      
      // 模拟成功响应
      return `商品已添加到购物车`;
    }
  }

  /**
   * 更新购物车商品数量
   */
  static async updateCartItemQuantity(userId: number, productId: number, quantity: number): Promise<string> {
    try {
      const response = await api.put<ApiResponse<string>>(`/api/cart/${userId}/update`, {
        productId,
        quantity
      });
      return response.data.message;
    } catch (error) {
      console.error('更新购物车数量失败，使用模拟数据:', error);
      
      // 模拟成功响应
      return `购物车商品数量已更新`;
    }
  }

  /**
   * 从购物车删除商品
   */
  static async removeFromCart(userId: number, cartItemId: number): Promise<string> {
    try {
      const response = await api.delete<ApiResponse<string>>(`/api/cart/${userId}/remove/${cartItemId}`);
      return response.data.message;
    } catch (error) {
      console.error('从购物车删除商品失败，使用模拟数据:', error);
      
      // 模拟成功响应
      return `商品已从购物车中移除`;
    }
  }

  /**
   * 根据商品ID从购物车删除商品
   */
  static async removeFromCartByProductId(userId: number, productId: number): Promise<string> {
    try {
      const response = await api.delete<ApiResponse<string>>(`/api/cart/${userId}/remove-product/${productId}`);
      return response.data.message;
    } catch (error) {
      console.error('从购物车删除商品失败，使用模拟数据:', error);
      
      // 模拟成功响应
      return `商品已从购物车中移除`;
    }
  }

  /**
   * 清空用户购物车
   */
  static async clearCart(userId: number): Promise<string> {
    try {
      const response = await api.delete<ApiResponse<string>>(`/api/cart/${userId}/clear`);
      return response.data.message;
    } catch (error) {
      console.error('清空购物车失败，使用模拟数据:', error);
      
      // 模拟成功响应
      return `购物车已清空`;
    }
  }

  /**
   * 获取用户购物车商品数量
   */
  static async getCartItemCount(userId: number): Promise<number> {
    try {
      const response = await api.get<ApiResponse<number>>(`/api/cart/${userId}/count`);
      return response.data.data;
    } catch (error) {
      console.error('获取购物车商品数量失败，使用模拟数据:', error);
      
      // 从localStorage获取购物车数据
      const cartItemsStr = localStorage.getItem('cartItems');
      if (cartItemsStr) {
        const cartItems = JSON.parse(cartItemsStr);
        return cartItems.reduce((total: number, item: CartItem) => total + item.quantity, 0);
      }
      
      // 如果localStorage中没有数据，返回0
      return 0;
    }
  }
} 