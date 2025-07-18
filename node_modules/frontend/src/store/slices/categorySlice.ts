import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { Category } from '../../types';
import { ProductService } from '../../services/productService';

// 异步action：获取所有分类
export const fetchAllCategories = createAsyncThunk(
  'categories/fetchAllCategories',
  async () => {
    const response = await ProductService.getAllCategories();
    return response;
  }
);

// 异步action：获取顶级分类
export const fetchTopCategories = createAsyncThunk(
  'categories/fetchTopCategories',
  async () => {
    const response = await ProductService.getTopCategories();
    return response;
  }
);

// 异步action：根据父分类ID获取子分类
export const fetchCategoriesByParentId = createAsyncThunk(
  'categories/fetchCategoriesByParentId',
  async (parentId: number) => {
    const response = await ProductService.getCategoriesByParentId(parentId);
    return { parentId, categories: response };
  }
);

interface CategoryState {
  // 所有分类
  allCategories: Category[];
  
  // 顶级分类
  topCategories: Category[];
  
  // 子分类映射 {parentId: Category[]}
  subCategories: Record<number, Category[]>;
  
  // 加载状态
  loading: boolean;
  error: string | null;
  
  // 当前选中的分类
  selectedCategoryId: number | null;
}

const initialState: CategoryState = {
  allCategories: [],
  topCategories: [],
  subCategories: {},
  loading: false,
  error: null,
  selectedCategoryId: null,
};

const categorySlice = createSlice({
  name: 'categories',
  initialState,
  reducers: {
    // 设置选中的分类
    setSelectedCategory: (state, action) => {
      state.selectedCategoryId = action.payload;
    },
    
    // 清除选中的分类
    clearSelectedCategory: (state) => {
      state.selectedCategoryId = null;
    },
  },
  extraReducers: (builder) => {
    // 获取所有分类
    builder
      .addCase(fetchAllCategories.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchAllCategories.fulfilled, (state, action) => {
        state.loading = false;
        state.allCategories = action.payload;
      })
      .addCase(fetchAllCategories.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || '获取分类失败';
      });
    
    // 获取顶级分类
    builder
      .addCase(fetchTopCategories.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchTopCategories.fulfilled, (state, action) => {
        state.loading = false;
        state.topCategories = action.payload;
      })
      .addCase(fetchTopCategories.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || '获取顶级分类失败';
      });
    
    // 获取子分类
    builder
      .addCase(fetchCategoriesByParentId.fulfilled, (state, action) => {
        const { parentId, categories } = action.payload;
        state.subCategories[parentId] = categories;
      });
  },
});

export const { 
  setSelectedCategory, 
  clearSelectedCategory 
} = categorySlice.actions;

export default categorySlice.reducer; 