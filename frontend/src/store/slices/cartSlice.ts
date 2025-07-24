import { createSlice, PayloadAction, createAsyncThunk } from '@reduxjs/toolkit';
import { CartItem, Product, Coupon } from '../../types';
import { CartService } from '../../services/cartService';

// 模拟用户ID
const MOCK_USER_ID = 1;

// 异步action：添加商品到购物车
export const addToCartAsync = createAsyncThunk(
  'cart/addToCartAsync',
  async ({ product, quantity }: { product: Product; quantity: number }, { dispatch }) => {
    try {
      // 调用后端API
      await CartService.addToCart(MOCK_USER_ID, product.id, quantity);
      // 更新Redux状态
      dispatch(addToCart({ product, quantity }));
      return { success: true };
    } catch (error) {
      console.error('添加商品到购物车失败:', error);
      throw error;
    }
  }
);

// 异步action：更新购物车商品数量
export const updateQuantityAsync = createAsyncThunk(
  'cart/updateQuantityAsync',
  async ({ productId, quantity }: { productId: number; quantity: number }, { dispatch }) => {
    try {
      // 调用后端API
      await CartService.updateCartItemQuantity(MOCK_USER_ID, productId, quantity);
      // 更新Redux状态
      dispatch(updateQuantity({ productId, quantity }));
      return { success: true };
    } catch (error) {
      console.error('更新购物车商品数量失败:', error);
      throw error;
    }
  }
);

// 异步action：从购物车删除商品
export const removeFromCartAsync = createAsyncThunk(
  'cart/removeFromCartAsync',
  async (productId: number, { dispatch }) => {
    try {
      // 调用后端API
      await CartService.removeFromCartByProductId(MOCK_USER_ID, productId);
      // 更新Redux状态
      dispatch(removeFromCart(productId));
      return { success: true };
    } catch (error) {
      console.error('从购物车删除商品失败:', error);
      throw error;
    }
  }
);

// 异步action：清空购物车
export const clearCartAsync = createAsyncThunk(
  'cart/clearCartAsync',
  async (_, { dispatch }) => {
    try {
      // 调用后端API
      await CartService.clearCart(MOCK_USER_ID);
      // 更新Redux状态
      dispatch(clearCart());
      return { success: true };
    } catch (error) {
      console.error('清空购物车失败:', error);
      throw error;
    }
  }
);

// 异步action：加载购物车
export const loadCartAsync = createAsyncThunk(
  'cart/loadCartAsync',
  async (_, { dispatch }) => {
    try {
      // 调用后端API获取购物车数据
      const cartItems = await CartService.getCartItems(MOCK_USER_ID);
      // 更新Redux状态
      dispatch(setCartItems(cartItems));
      return { success: true };
    } catch (error) {
      console.error('加载购物车失败:', error);
      throw error;
    }
  }
);

interface CartState {
  items: CartItem[];
  totalQuantity: number;
  totalAmount: number;
  isOpen: boolean; // 购物车抽屉是否打开
  coupon: Coupon | null; // 已选择的优惠券
  pointsUsed: number; // 使用的积分
  discountAmount: number; // 优惠金额
  actualAmount: number; // 实际支付金额
}

const initialState: CartState = {
  items: [],
  totalQuantity: 0,
  totalAmount: 0,
  isOpen: false,
  coupon: null,
  pointsUsed: 0,
  discountAmount: 0,
  actualAmount: 0,
};

// 计算购物车总数量和总金额
const calculateTotals = (items: CartItem[]) => {
  let totalQuantity = 0;
  let totalAmount = 0;
  
  items.forEach(item => {
    totalQuantity += item.quantity;
    totalAmount += item.product.price * item.quantity;
  });
  
  return { totalQuantity, totalAmount };
};

// 计算优惠后的实际金额
const calculateActualAmount = (totalAmount: number, coupon: Coupon | null, pointsUsed: number) => {
  let discountAmount = 0;
  
  // 计算优惠券折扣
  if (coupon) {
    if (totalAmount >= coupon.minAmount) {
      if (coupon.type === 'DISCOUNT') {
        // 折扣券，如8折就是0.8
        discountAmount += totalAmount * (1 - coupon.value / 10);
      } else if (coupon.type === 'FIXED') {
        // 满减券
        discountAmount += coupon.value;
      }
    }
  }
  
  // 计算积分折扣（假设100积分=1元）
  const pointsDiscount = pointsUsed / 100;
  discountAmount += pointsDiscount;
  
  // 确保折扣不超过总金额
  discountAmount = Math.min(discountAmount, totalAmount);
  
  // 计算实际支付金额
  const actualAmount = totalAmount - discountAmount;
  
  return { discountAmount, actualAmount };
};

const cartSlice = createSlice({
  name: 'cart',
  initialState,
  reducers: {
    // 设置购物车商品列表
    setCartItems: (state, action: PayloadAction<CartItem[]>) => {
      state.items = action.payload;
      
      // 重新计算总计
      const totals = calculateTotals(state.items);
      state.totalQuantity = totals.totalQuantity;
      state.totalAmount = totals.totalAmount;
      
      // 重新计算优惠后金额
      const { discountAmount, actualAmount } = calculateActualAmount(
        state.totalAmount, 
        state.coupon, 
        state.pointsUsed
      );
      state.discountAmount = discountAmount;
      state.actualAmount = actualAmount;
    },
    
    // 添加商品到购物车
    addToCart: (state, action: PayloadAction<{ product: Product; quantity?: number }>) => {
      const { product, quantity = 1 } = action.payload;
      const existingItemIndex = state.items.findIndex(item => item.product.id === product.id);
      
      if (existingItemIndex >= 0) {
        // 如果商品已存在，增加数量
        state.items[existingItemIndex].quantity += quantity;
      } else {
        // 如果商品不存在，添加新项
        state.items.push({ product, quantity });
      }
      
      // 重新计算总计
      const totals = calculateTotals(state.items);
      state.totalQuantity = totals.totalQuantity;
      state.totalAmount = totals.totalAmount;
      
      // 重新计算优惠后金额
      const { discountAmount, actualAmount } = calculateActualAmount(
        state.totalAmount, 
        state.coupon, 
        state.pointsUsed
      );
      state.discountAmount = discountAmount;
      state.actualAmount = actualAmount;
    },
    
    // 从购物车移除商品
    removeFromCart: (state, action: PayloadAction<number>) => {
      const productId = action.payload;
      state.items = state.items.filter(item => item.product.id !== productId);
      
      // 重新计算总计
      const totals = calculateTotals(state.items);
      state.totalQuantity = totals.totalQuantity;
      state.totalAmount = totals.totalAmount;
      
      // 重新计算优惠后金额
      const { discountAmount, actualAmount } = calculateActualAmount(
        state.totalAmount, 
        state.coupon, 
        state.pointsUsed
      );
      state.discountAmount = discountAmount;
      state.actualAmount = actualAmount;
    },
    
    // 更新商品数量
    updateQuantity: (state, action: PayloadAction<{ productId: number; quantity: number }>) => {
      const { productId, quantity } = action.payload;
      const itemIndex = state.items.findIndex(item => item.product.id === productId);
      
      if (itemIndex >= 0) {
        if (quantity <= 0) {
          // 如果数量为0或负数，移除商品
          state.items.splice(itemIndex, 1);
        } else {
          // 更新数量
          state.items[itemIndex].quantity = quantity;
        }
        
        // 重新计算总计
        const totals = calculateTotals(state.items);
        state.totalQuantity = totals.totalQuantity;
        state.totalAmount = totals.totalAmount;
        
        // 重新计算优惠后金额
        const { discountAmount, actualAmount } = calculateActualAmount(
          state.totalAmount, 
          state.coupon, 
          state.pointsUsed
        );
        state.discountAmount = discountAmount;
        state.actualAmount = actualAmount;
      }
    },
    
    // 清空购物车
    clearCart: (state) => {
      state.items = [];
      state.totalQuantity = 0;
      state.totalAmount = 0;
      state.coupon = null;
      state.pointsUsed = 0;
      state.discountAmount = 0;
      state.actualAmount = 0;
    },
    
    // 应用优惠券
    applyCoupon: (state, action: PayloadAction<Coupon | null>) => {
      state.coupon = action.payload;
      
      // 重新计算优惠后金额
      const { discountAmount, actualAmount } = calculateActualAmount(
        state.totalAmount, 
        state.coupon, 
        state.pointsUsed
      );
      state.discountAmount = discountAmount;
      state.actualAmount = actualAmount;
    },
    
    // 使用积分
    applyPoints: (state, action: PayloadAction<number>) => {
      state.pointsUsed = action.payload;
      
      // 重新计算优惠后金额
      const { discountAmount, actualAmount } = calculateActualAmount(
        state.totalAmount, 
        state.coupon, 
        state.pointsUsed
      );
      state.discountAmount = discountAmount;
      state.actualAmount = actualAmount;
    },
    
    // 打开购物车抽屉
    openCart: (state) => {
      state.isOpen = true;
    },
    
    // 关闭购物车抽屉
    closeCart: (state) => {
      state.isOpen = false;
    },
    
    // 切换购物车抽屉状态
    toggleCart: (state) => {
      state.isOpen = !state.isOpen;
    },
  },
});

export const {
  setCartItems,
  addToCart,
  removeFromCart,
  updateQuantity,
  clearCart,
  applyCoupon,
  applyPoints,
  openCart,
  closeCart,
  toggleCart,
} = cartSlice.actions;

export default cartSlice.reducer; 