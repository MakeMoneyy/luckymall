import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Provider } from 'react-redux';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import { store } from './store';
import Layout from './components/Layout';
import ProductList from './components/ProductList';
import ProductDetail from './components/ProductDetail';
import SearchResults from './components/SearchResults';
import CartPage from './pages/CartPage';
import OrderPage from './pages/OrderPage';
import OrderHistoryPage from './pages/OrderHistoryPage';
import PaymentPage from './pages/PaymentPage';
import OrderSuccessPage from './pages/OrderSuccessPage';
import CustomerService from './components/CustomerService';
import HomePage from './pages/HomePage';
import './App.css';

const App: React.FC = () => {
  return (
    <Provider store={store}>
      <ConfigProvider locale={zhCN}>
        <Router>
          <Layout>
            <Routes>
              {/* 首页 */}
              <Route path="/" element={<HomePage />} />
              
              {/* 全部商品页 */}
              <Route path="/products" element={<ProductList />} />
              
              {/* 商品详情页 */}
              <Route path="/product/:id" element={<ProductDetail />} />
              
              {/* 搜索结果页 */}
              <Route path="/search" element={<SearchResults />} />
              
              {/* 购物车页面 */}
              <Route path="/cart" element={<CartPage />} />
              
              {/* 订单页面 */}
              <Route path="/order" element={<OrderPage />} />
              
              {/* 订单历史页面 */}
              <Route path="/orders" element={<OrderHistoryPage />} />
              
              {/* 支付页面 */}
              <Route path="/payment" element={<PaymentPage />} />
              
              {/* 订单成功页面 */}
              <Route path="/order-success" element={<OrderSuccessPage />} />
              
              {/* 404页面 */}
              <Route path="*" element={
                <div style={{ 
                  textAlign: 'center', 
                  padding: '100px 0',
                  fontSize: '18px',
                  color: '#999'
                }}>
                  页面不存在
                </div>
              } />
            </Routes>
            
            {/* 智能客服组件 - 在所有页面显示 */}
            <CustomerService />
          </Layout>
        </Router>
      </ConfigProvider>
    </Provider>
  );
};

export default App;
