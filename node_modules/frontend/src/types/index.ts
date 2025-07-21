// 通用响应结果类型
export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
}

// 分页结果类型
export interface PageResult<T = any> {
  records: T[];
  total: number;
  current: number;
  size: number;
  pages: number;
}

// 商品类型
export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  stockQuantity: number;
  categoryId: number;
  imageUrl: string;
  salesCount: number;
  status: number;
  createdAt: string;
  updatedAt: string;
  categoryName?: string;
}

// 商品分类类型
export interface Category {
  id: number;
  name: string;
  description: string;
  parentId: number | null;
  createdAt: string;
}

// 用户类型
export interface User {
  id: number;
  username: string;
  email: string;
  phone: string;
  createdAt: string;
  updatedAt: string;
  points?: number; // 用户积分
}

// 商品查询参数
export interface ProductQueryParams {
  current?: number;
  size?: number;
  categoryId?: number;
  minPrice?: number;
  maxPrice?: number;
  sortBy?: string;
}

// 商品搜索参数
export interface ProductSearchParams {
  keyword: string;
  current?: number;
  size?: number;
  sortBy?: string;
  categoryId?: number;
}

// 购物车商品类型
export interface CartItem {
  product: Product;
  quantity: number;
}

// 订单类型
export interface Order {
  id: string;
  userId: number;
  items: CartItem[];
  totalAmount: number;
  status: string;
  createdAt: string;
  addressId?: number;
  address?: UserAddress;
  paymentMethod?: string;
  remark?: string;
  discountAmount?: number;
  actualAmount?: number;
  pointsUsed?: number;
  couponId?: number;
}

// 排序选项
export interface SortOption {
  value: string;
  label: string;
}

// 优惠券类型
export interface Coupon {
  id: number;
  name: string;
  description: string;
  type: string; // 优惠券类型：满减、折扣等
  value: number; // 优惠券面值或折扣率
  minAmount: number; // 使用门槛
  startDate: string;
  endDate: string;
  status: number; // 0-无效 1-有效
}

// 用户地址类型
export interface UserAddress {
  id: number;
  userId: number;
  receiverName: string;
  phone: string;
  province: string;
  city: string;
  district: string;
  detailAddress: string;
  postalCode?: string;
  isDefault: boolean;
}

// 支付方式类型
export interface PaymentMethod {
  id: string;
  name: string;
  icon: string;
}

// 用户信用卡类型
export interface UserCreditCard {
  id: number;
  userId: number;
  cardNumber: string;
  cardholderName?: string;
  cardType?: string;
  expiryDate: string;
  isDefault: boolean;
}

// 分期方案类型
export interface InstallmentPlan {
  id: number;
  planName?: string;
  name?: string;
  installmentCount?: number;
  months?: number;
  interestRate: number;
  minAmount: number;
  maxAmount?: number;
  description?: string;
  status?: number;
}

// 二维码支付响应类型
export interface QRCodePaymentResponse {
  qrCodeUrl: string;
  paymentNo: string;
  expireTime: string;
  amount: number;
}

// 聊天请求类型
export interface ChatRequest {
  userId: string;
  sessionId: string;
  message: string;
  context: Record<string, any>;
}

// 聊天响应类型
export interface ChatResponse {
  result: string;
  sessionId: string;
  requestId?: string;
  usage?: {
    inputTokens: number;
    outputTokens: number;
  };
} 