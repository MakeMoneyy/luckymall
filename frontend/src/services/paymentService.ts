import api from './api';
import { ApiResponse, InstallmentPlan, UserCreditCard, QRCodePaymentResponse } from '../types';

/**
 * 支付请求参数
 */
export interface PaymentRequest {
  orderId: number;
  paymentMethod: string;
  creditCardId?: number;
  installmentPlanId?: number;
  amount: number;
}

/**
 * 支付响应
 */
export interface PaymentResponse {
  paymentId: number;
  paymentNo: string;
  status: string;
  paymentTime: string;
}

/**
 * 支付服务类
 */
export class PaymentService {
  /**
   * 获取用户信用卡列表
   */
  static async getUserCreditCards(userId: number): Promise<UserCreditCard[]> {
    const response = await api.get<ApiResponse<UserCreditCard[]>>(`/api/payments/credit-cards/${userId}`);
    return response.data.data;
  }

  /**
   * 添加信用卡
   */
  static async addCreditCard(userId: number, cardData: Partial<UserCreditCard>): Promise<UserCreditCard> {
    const response = await api.post<ApiResponse<UserCreditCard>>(`/api/payments/credit-cards/${userId}`, cardData);
    return response.data.data;
  }

  /**
   * 设置默认信用卡
   */
  static async setDefaultCreditCard(userId: number, cardId: number): Promise<string> {
    const response = await api.put<ApiResponse<string>>(`/api/payments/credit-cards/${userId}/default/${cardId}`);
    return response.data.message;
  }

  /**
   * 删除信用卡
   */
  static async deleteCreditCard(userId: number, cardId: number): Promise<string> {
    const response = await api.delete<ApiResponse<string>>(`/api/payments/credit-cards/${userId}/${cardId}`);
    return response.data.message;
  }

  /**
   * 获取可用分期方案列表
   */
  static async getAvailableInstallmentPlans(amount: number): Promise<InstallmentPlan[]> {
    const response = await api.get<ApiResponse<InstallmentPlan[]>>(`/api/payments/installment-plans`, {
      params: { amount }
    });
    return response.data.data;
  }

  /**
   * 计算分期付款详情
   */
  static async calculateInstallment(amount: number, installmentPlanId: number): Promise<{
    monthlyAmount: number;
    totalAmount: number;
    interestAmount: number;
  }> {
    const response = await api.get<ApiResponse<{
      monthlyAmount: number;
      totalAmount: number;
      interestAmount: number;
    }>>(`/api/payments/calculate-installment`, {
      params: { amount, installmentPlanId }
    });
    return response.data.data;
  }

  /**
   * 支付订单
   */
  static async payOrder(userId: number, request: PaymentRequest): Promise<PaymentResponse> {
    const response = await api.post<ApiResponse<PaymentResponse>>(`/api/payments/${userId}/pay`, request);
    return response.data.data;
  }

  /**
   * 查询支付状态
   */
  static async queryPaymentStatus(paymentNo: string): Promise<string> {
    const response = await api.get<ApiResponse<string>>(`/api/payments/status/${paymentNo}`);
    return response.data.data;
  }
  
  /**
   * 获取支付宝支付二维码
   */
  static async getAlipayQRCode(userId: number, orderId: number, amount: number): Promise<QRCodePaymentResponse> {
    const response = await api.post<ApiResponse<QRCodePaymentResponse>>(`/api/payments/${userId}/alipay/qrcode`, {
      orderId,
      amount
    });
    return response.data.data;
  }
  
  /**
   * 获取微信支付二维码
   */
  static async getWechatPayQRCode(userId: number, orderId: number, amount: number): Promise<QRCodePaymentResponse> {
    const response = await api.post<ApiResponse<QRCodePaymentResponse>>(`/api/payments/${userId}/wechat/qrcode`, {
      orderId,
      amount
    });
    return response.data.data;
  }
} 