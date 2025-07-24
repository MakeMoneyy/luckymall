import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { Product, PageResult, ProductQueryParams, ProductSearchParams } from '../../types';
import { ProductService } from '../../services/productService';

// 异步action：获取商品列表
export const fetchProducts = createAsyncThunk(
  'products/fetchProducts',
  async (params: ProductQueryParams = {}) => {
    const response = await ProductService.getProducts(params);
    return response;
  }
);

// 异步action：获取商品详情
export const fetchProductById = createAsyncThunk(
  'products/fetchProductById',
  async (id: number) => {
    const response = await ProductService.getProductById(id);
    return response;
  }
);

// 异步action：搜索商品
export const searchProducts = createAsyncThunk(
  'products/searchProducts',
  async (params: ProductSearchParams) => {
    const response = await ProductService.searchProducts(params);
    return response;
  }
);

interface ProductState {
  // 商品列表相关
  products: Product[];
  total: number;
  current: number;
  size: number;
  pages: number;
  loading: boolean;
  error: string | null;
  
  // 商品详情相关
  currentProduct: Product | null;
  detailLoading: boolean;
  detailError: string | null;
  
  // 搜索相关
  searchResults: Product[];
  searchTotal: number;
  searchCurrent: number;
  searchLoading: boolean;
  searchError: string | null;
  lastSearchKeyword: string;
  
  // 筛选条件
  filters: {
    categoryId?: number;
    minPrice?: number;
    maxPrice?: number;
    sortBy?: string;
  };
}

const initialState: ProductState = {
  products: [],
  total: 0,
  current: 1,
  size: 10,
  pages: 0,
  loading: false,
  error: null,
  
  currentProduct: null,
  detailLoading: false,
  detailError: null,
  
  searchResults: [],
  searchTotal: 0,
  searchCurrent: 1,
  searchLoading: false,
  searchError: null,
  lastSearchKeyword: '',
  
  filters: {},
};

const productSlice = createSlice({
  name: 'products',
  initialState,
  reducers: {
    // 设置筛选条件
    setFilters: (state, action: PayloadAction<Partial<ProductState['filters']>>) => {
      state.filters = { ...state.filters, ...action.payload };
    },
    
    // 清除筛选条件
    clearFilters: (state) => {
      state.filters = {};
    },
    
    // 重置搜索结果
    resetSearch: (state) => {
      state.searchResults = [];
      state.searchTotal = 0;
      state.searchCurrent = 1;
      state.searchError = null;
      state.lastSearchKeyword = '';
    },
    
    // 重置当前商品详情
    resetCurrentProduct: (state) => {
      state.currentProduct = null;
      state.detailError = null;
    },
  },
  extraReducers: (builder) => {
    // 获取商品列表
    builder
      .addCase(fetchProducts.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchProducts.fulfilled, (state, action) => {
        state.loading = false;
        state.products = action.payload.records;
        state.total = action.payload.total;
        state.current = action.payload.current;
        state.size = action.payload.size;
        state.pages = action.payload.pages;
      })
      .addCase(fetchProducts.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || '获取商品列表失败';
      });
    
    // 获取商品详情
    builder
      .addCase(fetchProductById.pending, (state) => {
        state.detailLoading = true;
        state.detailError = null;
      })
      .addCase(fetchProductById.fulfilled, (state, action) => {
        state.detailLoading = false;
        state.currentProduct = action.payload;
      })
      .addCase(fetchProductById.rejected, (state, action) => {
        state.detailLoading = false;
        state.detailError = action.error.message || '获取商品详情失败';
      });
    
    // 搜索商品
    builder
      .addCase(searchProducts.pending, (state) => {
        state.searchLoading = true;
        state.searchError = null;
      })
      .addCase(searchProducts.fulfilled, (state, action) => {
        state.searchLoading = false;
        state.searchResults = action.payload.records;
        state.searchTotal = action.payload.total;
        state.searchCurrent = action.payload.current;
      })
      .addCase(searchProducts.rejected, (state, action) => {
        state.searchLoading = false;
        state.searchError = action.error.message || '搜索商品失败';
      });
  },
});

export const { 
  setFilters, 
  clearFilters, 
  resetSearch, 
  resetCurrentProduct 
} = productSlice.actions;

export default productSlice.reducer; 