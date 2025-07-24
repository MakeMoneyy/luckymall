import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import ProductList from '../ProductList';
import productReducer from '../../store/slices/productSlice';
import categoryReducer from '../../store/slices/categorySlice';

// 创建测试store
const createTestStore = (initialState = {}) => {
  return configureStore({
    reducer: {
      products: productReducer,
      categories: categoryReducer,
    },
    preloadedState: initialState,
  });
};

// 模拟商品数据
const mockProducts = [
  {
    id: 1,
    name: 'iPhone 15 Pro',
    description: 'Apple iPhone 15 Pro 256GB',
    price: 8999,
    stockQuantity: 50,
    categoryId: 6,
    imageUrl: 'https://via.placeholder.com/400x400',
    salesCount: 128,
    status: 1,
    createdAt: '2024-01-01',
    updatedAt: '2024-01-01',
    categoryName: '手机通讯'
  },
  {
    id: 2,
    name: '华为 Mate 60 Pro',
    description: '华为 Mate 60 Pro 512GB',
    price: 6999,
    stockQuantity: 30,
    categoryId: 6,
    imageUrl: 'https://via.placeholder.com/400x400',
    salesCount: 89,
    status: 1,
    createdAt: '2024-01-01',
    updatedAt: '2024-01-01',
    categoryName: '手机通讯'
  }
];

const renderWithProvider = (component: React.ReactElement, initialState = {}) => {
  const store = createTestStore(initialState);
  return render(
    <Provider store={store}>
      {component}
    </Provider>
  );
};

describe('ProductList', () => {
  
  it('应该显示加载状态', () => {
    const initialState = {
      products: {
        products: [],
        loading: true,
        error: null,
        total: 0,
        current: 1,
        size: 10,
        pages: 0,
        filters: {},
        currentProduct: null,
        detailLoading: false,
        detailError: null,
        searchResults: [],
        searchTotal: 0,
        searchCurrent: 1,
        searchLoading: false,
        searchError: null,
        lastSearchKeyword: '',
      }
    };
    
    renderWithProvider(<ProductList />, initialState);
    
    expect(screen.getByText(/加载中/i)).toBeInTheDocument();
  });

  it('应该显示商品列表', () => {
    const initialState = {
      products: {
        products: mockProducts,
        loading: false,
        error: null,
        total: 2,
        current: 1,
        size: 10,
        pages: 1,
        filters: {},
        currentProduct: null,
        detailLoading: false,
        detailError: null,
        searchResults: [],
        searchTotal: 0,
        searchCurrent: 1,
        searchLoading: false,
        searchError: null,
        lastSearchKeyword: '',
      }
    };
    
    renderWithProvider(<ProductList />, initialState);
    
    expect(screen.getByText('iPhone 15 Pro')).toBeInTheDocument();
    expect(screen.getByText('华为 Mate 60 Pro')).toBeInTheDocument();
    expect(screen.getByText('¥8999')).toBeInTheDocument();
    expect(screen.getByText('¥6999')).toBeInTheDocument();
  });

  it('应该显示错误信息', () => {
    const initialState = {
      products: {
        products: [],
        loading: false,
        error: '网络连接失败',
        total: 0,
        current: 1,
        size: 10,
        pages: 0,
        filters: {},
        currentProduct: null,
        detailLoading: false,
        detailError: null,
        searchResults: [],
        searchTotal: 0,
        searchCurrent: 1,
        searchLoading: false,
        searchError: null,
        lastSearchKeyword: '',
      }
    };
    
    renderWithProvider(<ProductList />, initialState);
    
    expect(screen.getByText('网络连接失败')).toBeInTheDocument();
  });

  it('应该支持分页', () => {
    const initialState = {
      products: {
        products: mockProducts,
        loading: false,
        error: null,
        total: 20,
        current: 1,
        size: 10,
        pages: 2,
        filters: {},
        currentProduct: null,
        detailLoading: false,
        detailError: null,
        searchResults: [],
        searchTotal: 0,
        searchCurrent: 1,
        searchLoading: false,
        searchError: null,
        lastSearchKeyword: '',
      }
    };
    
    renderWithProvider(<ProductList />, initialState);
    
    // 应该显示分页组件
    expect(screen.getByText('2')).toBeInTheDocument(); // 第二页
  });

  it('应该支持筛选功能', () => {
    renderWithProvider(<ProductList />);
    
    // 应该显示筛选组件
    expect(screen.getByText(/筛选/i)).toBeInTheDocument();
  });

  it('应该支持排序功能', () => {
    renderWithProvider(<ProductList />);
    
    // 应该显示排序选项
    expect(screen.getByText(/排序/i)).toBeInTheDocument();
  });
}); 