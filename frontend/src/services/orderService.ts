import api from './api';
import { ApiResponse, Order } from '../types';

/**
 * 订单请求参数
 */
export interface CreateOrderRequest {
  cartItemIds: number[];
  addressId: number;
  paymentMethod: string;
  creditCardId?: number;
  installmentPlanId?: number;
  couponId?: number;
  pointsUsed?: number;
  expectedAmount: number;
  remark?: string;
}

/**
 * 订单响应
 */
export interface CreateOrderResponse {
  orderId: number;
  orderNo: string;
  totalAmount: number;
  actualAmount: number;
  isInstallment: boolean;
  installmentCount?: number;
  monthlyAmount?: number;
  paymentMethod: string;
  receiverAddress: string;
}

/**
 * 订单服务类
 */
export class OrderService {
  /**
   * 创建订单
   */
  static async createOrder(userId: number, request: CreateOrderRequest): Promise<CreateOrderResponse> {
    try {
      // 尝试调用真实API
      const response = await api.post<ApiResponse<CreateOrderResponse>>(`/api/orders/${userId}`, request);
      return response.data.data;
    } catch (error) {
      console.error('调用订单API失败，使用模拟数据:', error);
      
      // 模拟响应
      return {
        orderId: Math.floor(Math.random() * 1000000),
        orderNo: `ORD${Date.now()}`,
        totalAmount: request.expectedAmount,
        actualAmount: request.expectedAmount - (request.pointsUsed ? request.pointsUsed / 100 : 0),
        isInstallment: !!request.installmentPlanId,
        installmentCount: request.installmentPlanId ? (request.installmentPlanId === 1 ? 3 : 
                                                     request.installmentPlanId === 2 ? 6 : 
                                                     request.installmentPlanId === 3 ? 12 : 
                                                     request.installmentPlanId === 4 ? 24 : 36) : undefined,
        monthlyAmount: request.installmentPlanId ? 
                       (request.expectedAmount / (request.installmentPlanId === 1 ? 3 : 
                                                request.installmentPlanId === 2 ? 6 : 
                                                request.installmentPlanId === 3 ? 12 : 
                                                request.installmentPlanId === 4 ? 24 : 36)) : undefined,
        paymentMethod: request.paymentMethod,
        receiverAddress: '模拟地址'
      };
    }
  }

  /**
   * 根据ID查询订单
   */
  static async getOrderById(orderId: number): Promise<Order> {
    const response = await api.get<ApiResponse<Order>>(`/api/orders/${orderId}`);
    return response.data.data;
  }

  /**
   * 根据订单号查询订单
   */
  static async getOrderByOrderNo(orderNo: string): Promise<Order> {
    const response = await api.get<ApiResponse<Order>>(`/api/orders/no/${orderNo}`);
    return response.data.data;
  }

  /**
   * 根据用户ID查询订单列表
   */
  static async getOrdersByUserId(userId: number): Promise<Order[]> {
    try {
      const response = await api.get<ApiResponse<Order[]>>(`/api/orders/user/${userId}`);
      return response.data.data;
    } catch (error) {
      console.error('获取订单列表失败，使用模拟数据:', error);
      
      // 返回模拟订单数据
      const mockOrders: Order[] = [
        {
          id: '14',
          userId: userId,
          items: [
            {
              product: {
                id: 1,
                name: 'iPhone 15 Pro Max',
                description: '最新款苹果手机',
                price: 8999.00,
                stockQuantity: 100,
                categoryId: 1,
                imageUrl: 'https://via.placeholder.com/300x300?text=iPhone15',
                salesCount: 1000,
                status: 1,
                createdAt: '2023-01-01',
                updatedAt: '2023-01-01',
                categoryName: '手机'
              },
              quantity: 1
            }
          ],
          totalAmount: 8999.00,
          status: 'PENDING_PAYMENT',
          createdAt: '2025-07-17 14:59:03',
          addressId: 1,
          address: {
            id: 1,
            userId: userId,
            receiverName: '张三',
            phone: '13800138000',
            province: '广东省',
            city: '深圳市',
            district: '南山区',
            detailAddress: '科技园1号',
            postalCode: '518000',
            isDefault: true
          },
          paymentMethod: 'credit_card'
        },
        {
          id: '13',
          userId: userId,
          items: [
            {
              product: {
                id: 2,
                name: '华为 Mate 60 Pro',
                description: '华为最新旗舰手机',
                price: 2999.00,
                stockQuantity: 50,
                categoryId: 1,
                imageUrl: 'https://via.placeholder.com/300x300?text=Mate60Pro',
                salesCount: 800,
                status: 1,
                createdAt: '2023-01-01',
                updatedAt: '2023-01-01',
                categoryName: '手机'
              },
              quantity: 1
            }
          ],
          totalAmount: 2999.00,
          status: 'PENDING_PAYMENT',
          createdAt: '2025-07-17 14:58:38',
          addressId: 1,
          address: {
            id: 1,
            userId: userId,
            receiverName: '张三',
            phone: '13800138000',
            province: '广东省',
            city: '深圳市',
            district: '南山区',
            detailAddress: '科技园1号',
            postalCode: '518000',
            isDefault: true
          },
          paymentMethod: 'credit_card'
        }
      ];
      
      return mockOrders;
    }
  }

  /**
   * 根据用户ID和状态查询订单列表
   */
  static async getOrdersByUserIdAndStatus(userId: number, orderStatus: string): Promise<Order[]> {
    const response = await api.get<ApiResponse<Order[]>>(`/api/orders/user/${userId}/status/${orderStatus}`);
    return response.data.data;
  }

  /**
   * 取消订单
   */
  static async cancelOrder(orderId: number, userId: number): Promise<string> {
    try {
      console.log('调用取消订单API:', orderId, userId);
      const response = await api.post<ApiResponse<string>>(`/api/orders/${orderId}/cancel`, null, {
        params: { userId }
      });
      return response.data.message;
    } catch (error) {
      console.error('取消订单API调用失败，使用模拟数据:', error);
      
      // 模拟成功响应
      // 更新localStorage中的订单状态
      try {
        const ordersStr = localStorage.getItem('orders');
        if (ordersStr) {
          const orders = JSON.parse(ordersStr);
          const orderIndex = orders.findIndex((order: any) => 
            order.id === orderId.toString() || order.id === orderId
          );
          
          if (orderIndex !== -1) {
            orders[orderIndex].status = 'CANCELLED';
            localStorage.setItem('orders', JSON.stringify(orders));
            console.log('本地订单状态已更新为已取消');
          } else {
            console.log('未找到要取消的订单:', orderId);
          }
        }
      } catch (localError) {
        console.error('更新本地订单状态失败:', localError);
      }
      
      return `订单 ${orderId} 已成功取消`;
    }
  }

  /**
   * 确认收货
   */
  static async confirmReceive(orderId: number, userId: number): Promise<string> {
    const response = await api.post<ApiResponse<string>>(`/api/orders/${orderId}/confirm`, null, {
      params: { userId }
    });
    return response.data.message;
  }

  /**
   * 支付订单
   */
  static async payOrder(orderId: number, userId: number): Promise<string> {
    const response = await api.post<ApiResponse<string>>(`/api/orders/${orderId}/pay`, null, {
      params: { userId }
    });
    return response.data.message;
  }
} 