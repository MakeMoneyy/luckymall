import api from './api';
import { 
  Product, 
  Category, 
  ApiResponse, 
  PageResult, 
  ProductQueryParams, 
  ProductSearchParams 
} from '../types';

/**
 * 商品服务类
 */
export class ProductService {
  
  /**
   * 获取商品列表
   */
  static async getProducts(params: ProductQueryParams = {}): Promise<PageResult<Product>> {
    const response = await api.get<ApiResponse<PageResult<Product>>>('/api/products', { params });
    return response.data.data;
  }

  /**
   * 根据ID获取商品详情
   */
  static async getProductById(id: number): Promise<Product> {
    const response = await api.get<ApiResponse<Product>>(`/api/products/${id}`);
    return response.data.data;
  }

  /**
   * 搜索商品
   */
  static async searchProducts(params: ProductSearchParams): Promise<PageResult<Product>> {
    const response = await api.get<ApiResponse<PageResult<Product>>>('/api/products/search', { params });
    return response.data.data;
  }

  /**
   * 获取所有分类
   */
  static async getAllCategories(): Promise<Category[]> {
    const response = await api.get<ApiResponse<Category[]>>('/api/categories');
    return response.data.data;
  }

  /**
   * 获取顶级分类
   */
  static async getTopCategories(): Promise<Category[]> {
    const response = await api.get<ApiResponse<Category[]>>('/api/categories/top');
    return response.data.data;
  }

  /**
   * 根据父分类ID获取子分类
   */
  static async getCategoriesByParentId(parentId: number): Promise<Category[]> {
    const response = await api.get<ApiResponse<Category[]>>(`/api/categories/parent/${parentId}`);
    return response.data.data;
  }

  /**
   * 根据ID获取分类详情
   */
  static async getCategoryById(id: number): Promise<Category> {
    const response = await api.get<ApiResponse<Category>>(`/api/categories/${id}`);
    return response.data.data;
  }
} 