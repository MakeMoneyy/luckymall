import { ProductService } from '../productService';
import { Product, PageResult, Category } from '../../types';

// 模拟API响应
jest.mock('../api');

describe('ProductService', () => {
  
  describe('getProducts', () => {
    it('应该返回商品列表', async () => {
      const mockProducts: PageResult<Product> = {
        records: [
          {
            id: 1,
            name: '测试商品',
            description: '测试描述',
            price: 99.99,
            stockQuantity: 10,
            categoryId: 1,
            imageUrl: 'test.jpg',
            salesCount: 5,
            status: 1,
            createdAt: '2024-01-01',
            updatedAt: '2024-01-01',
            categoryName: '测试分类'
          }
        ],
        total: 1,
        current: 1,
        size: 10,
        pages: 1
      };

      // 这里应该模拟API调用返回上述数据
      const result = await ProductService.getProducts({ current: 1, size: 10 });
      
      expect(result).toBeDefined();
      expect(result.records).toBeInstanceOf(Array);
      expect(result.total).toBeGreaterThanOrEqual(0);
    });

    it('应该支持分类筛选', async () => {
      const result = await ProductService.getProducts({ 
        current: 1, 
        size: 10, 
        categoryId: 6 
      });
      
      expect(result).toBeDefined();
      expect(result.records).toBeInstanceOf(Array);
    });

    it('应该支持价格区间筛选', async () => {
      const result = await ProductService.getProducts({ 
        current: 1, 
        size: 10, 
        minPrice: 100,
        maxPrice: 1000
      });
      
      expect(result).toBeDefined();
      expect(result.records).toBeInstanceOf(Array);
    });

    it('应该支持排序', async () => {
      const result = await ProductService.getProducts({ 
        current: 1, 
        size: 10, 
        sortBy: 'price_asc'
      });
      
      expect(result).toBeDefined();
      expect(result.records).toBeInstanceOf(Array);
    });
  });

  describe('getProductById', () => {
    it('应该返回指定ID的商品详情', async () => {
      const result = await ProductService.getProductById(1);
      
      expect(result).toBeDefined();
      expect(typeof result.id).toBe('number');
      expect(typeof result.name).toBe('string');
      expect(typeof result.price).toBe('number');
    });
  });

  describe('searchProducts', () => {
    it('应该根据关键词搜索商品', async () => {
      const result = await ProductService.searchProducts({ 
        keyword: '手机',
        current: 1,
        size: 10
      });
      
      expect(result).toBeDefined();
      expect(result.records).toBeInstanceOf(Array);
    });

    it('应该支持搜索结果排序', async () => {
      const result = await ProductService.searchProducts({ 
        keyword: '手机',
        current: 1,
        size: 10,
        sortBy: 'sales_desc'
      });
      
      expect(result).toBeDefined();
      expect(result.records).toBeInstanceOf(Array);
    });

    it('应该支持按分类搜索', async () => {
      const result = await ProductService.searchProducts({ 
        keyword: '手机',
        current: 1,
        size: 10,
        categoryId: 6
      });
      
      expect(result).toBeDefined();
      expect(result.records).toBeInstanceOf(Array);
    });
  });

  describe('Category APIs', () => {
    it('应该获取所有分类', async () => {
      const result = await ProductService.getAllCategories();
      
      expect(result).toBeDefined();
      expect(result).toBeInstanceOf(Array);
    });

    it('应该获取顶级分类', async () => {
      const result = await ProductService.getTopCategories();
      
      expect(result).toBeDefined();
      expect(result).toBeInstanceOf(Array);
    });

    it('应该根据父分类ID获取子分类', async () => {
      const result = await ProductService.getCategoriesByParentId(1);
      
      expect(result).toBeDefined();
      expect(result).toBeInstanceOf(Array);
    });

    it('应该根据ID获取分类详情', async () => {
      const result = await ProductService.getCategoryById(1);
      
      expect(result).toBeDefined();
      expect(typeof result.id).toBe('number');
      expect(typeof result.name).toBe('string');
    });
  });
}); 