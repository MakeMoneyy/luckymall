import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { Order } from '../../types';
import { OrderService, CreateOrderRequest, CreateOrderResponse } from '../../services/orderService';

// 模拟用户ID
const MOCK_USER_ID = 1;

// 异步action：创建订单
export const createOrderAsync = createAsyncThunk(
  'order/createOrder',
  async (request: CreateOrderRequest, { rejectWithValue }) => {
    try {
      const response = await OrderService.createOrder(MOCK_USER_ID, request);
      return response;
    } catch (error) {
      return rejectWithValue('创建订单失败');
    }
  }
);

// 异步action：获取用户订单列表
export const fetchUserOrdersAsync = createAsyncThunk(
  'order/fetchUserOrders',
  async (_, { rejectWithValue }) => {
    try {
      console.log('获取用户订单列表');
      
      // 尝试从localStorage获取订单数据
      const localOrdersStr = localStorage.getItem('orders');
      if (localOrdersStr) {
        try {
          const localOrders = JSON.parse(localOrdersStr);
          console.log('从localStorage加载订单数据:', localOrders);
          if (Array.isArray(localOrders) && localOrders.length > 0) {
            // 确保所有订单都有状态
            const ordersWithStatus = localOrders.map(order => {
              if (!order.status) {
                return { ...order, status: 'PENDING_PAYMENT' };
              }
              return order;
            });
            return ordersWithStatus;
          }
        } catch (parseError) {
          console.error('解析localStorage订单数据失败:', parseError);
        }
      }
      
      // 如果localStorage中没有数据，则从API获取
      const orders = await OrderService.getOrdersByUserId(MOCK_USER_ID);
      console.log('从API获取到的订单列表:', orders);
      
      // 确保所有订单都有状态
      const ordersWithStatus = orders.map(order => {
        if (!order.status) {
          return { ...order, status: 'PENDING_PAYMENT' };
        }
        return order;
      });
      
      // 保存到localStorage
      localStorage.setItem('orders', JSON.stringify(ordersWithStatus));
      
      return ordersWithStatus;
    } catch (error) {
      console.error('获取订单列表失败:', error);
      return rejectWithValue('获取订单列表失败');
    }
  }
);

// 异步action：取消订单
export const cancelOrderAsync = createAsyncThunk(
  'order/cancelOrder',
  async (orderId: number | string, { rejectWithValue, dispatch }) => {
    try {
      console.log('取消订单:', orderId);
      
      // 直接在本地更新订单状态
      const localOrdersStr = localStorage.getItem('orders');
      if (localOrdersStr) {
        try {
          const localOrders = JSON.parse(localOrdersStr);
          const orderIndex = localOrders.findIndex((order: any) => 
            order.id === orderId || order.id === orderId.toString()
          );
          
          if (orderIndex !== -1) {
            localOrders[orderIndex].status = 'CANCELLED';
            localStorage.setItem('orders', JSON.stringify(localOrders));
            console.log('本地订单状态已更新为已取消');
            
            // 返回更新后的订单
            return {
              id: orderId,
              status: 'CANCELLED'
            };
          }
        } catch (parseError) {
          console.error('解析localStorage订单数据失败:', parseError);
        }
      }
      
      // 如果本地更新失败，则调用API
      // 转换为数字类型，如果是字符串的话
      const numericOrderId = typeof orderId === 'string' ? parseInt(orderId) : orderId;
      await OrderService.cancelOrder(numericOrderId, MOCK_USER_ID);
      return {
        id: orderId,
        status: 'CANCELLED'
      }; // 返回原始orderId和状态
    } catch (error) {
      console.error('取消订单失败:', error);
      return rejectWithValue('取消订单失败');
    }
  }
);

// 异步action：确认收货
export const confirmReceiveAsync = createAsyncThunk(
  'order/confirmReceive',
  async (orderId: number, { rejectWithValue }) => {
    try {
      await OrderService.confirmReceive(orderId, MOCK_USER_ID);
      return orderId;
    } catch (error) {
      return rejectWithValue('确认收货失败');
    }
  }
);

interface OrderState {
  orders: Order[];
  currentOrder: CreateOrderResponse | null;
  loading: boolean;
  error: string | null;
}

const initialState: OrderState = {
  orders: JSON.parse(localStorage.getItem('orders') || '[]'),
  currentOrder: null,
  loading: false,
  error: null
};

const orderSlice = createSlice({
  name: 'order',
  initialState,
  reducers: {
    clearCurrentOrder: (state) => {
      state.currentOrder = null;
    },
    // 添加订单到本地存储（用于模拟）
    addLocalOrder: (state, action: PayloadAction<Order>) => {
      const newOrder = action.payload;
      console.log('添加/更新订单:', newOrder);
      
      // 检查是否已存在相同ID的订单
      const existingOrderIndex = state.orders.findIndex(order => order.id === newOrder.id);
      
      if (existingOrderIndex !== -1) {
        // 如果订单已存在，更新它
        console.log('更新已存在的订单:', existingOrderIndex);
        const updatedOrder = {
          ...state.orders[existingOrderIndex],
          ...newOrder
        };
        
        // 确保状态字段存在
        if (!updatedOrder.status) {
          updatedOrder.status = 'PENDING_PAYMENT'; // 默认为待付款
        }
        
        state.orders[existingOrderIndex] = updatedOrder;
        console.log('更新后的订单:', updatedOrder);
      } else {
        // 如果订单不存在，添加它
        console.log('添加新订单');
        // 确保状态字段存在
        if (!newOrder.status) {
          newOrder.status = 'PENDING_PAYMENT'; // 默认为待付款
        }
        
        // 确保订单有items字段
        if (!newOrder.items) {
          newOrder.items = [];
        }
        
        state.orders.unshift(newOrder);
        console.log('添加后的订单列表长度:', state.orders.length);
      }
      
      // 保存到localStorage
      localStorage.setItem('orders', JSON.stringify(state.orders));
    }
  },
  extraReducers: (builder) => {
    builder
      // 创建订单
      .addCase(createOrderAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createOrderAsync.fulfilled, (state, action) => {
        state.loading = false;
        state.currentOrder = action.payload;
      })
      .addCase(createOrderAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      
      // 获取用户订单列表
      .addCase(fetchUserOrdersAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchUserOrdersAsync.fulfilled, (state, action) => {
        state.loading = false;
        state.orders = action.payload;
      })
      .addCase(fetchUserOrdersAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      
      // 取消订单
      .addCase(cancelOrderAsync.fulfilled, (state, action) => {
        const { id, status } = action.payload;
        console.log('取消订单成功, orderId:', id, 'status:', status);
        
        const orderIndex = state.orders.findIndex(order => 
          order.id === id || order.id === id.toString()
        );
        
        if (orderIndex !== -1) {
          console.log('找到订单，更新状态为已取消');
          state.orders[orderIndex].status = status;
          // 保存到localStorage
          localStorage.setItem('orders', JSON.stringify(state.orders));
        } else {
          console.log('未找到订单:', id);
        }
      })
      
      // 确认收货
      .addCase(confirmReceiveAsync.fulfilled, (state, action) => {
        const orderId = action.payload;
        const orderIndex = state.orders.findIndex(order => Number(order.id) === orderId);
        if (orderIndex !== -1) {
          state.orders[orderIndex].status = 'COMPLETED';
        }
      });
  }
});

export const { clearCurrentOrder, addLocalOrder } = orderSlice.actions;

export default orderSlice.reducer; 