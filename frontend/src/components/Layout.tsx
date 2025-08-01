import React from 'react';
import { 
  Layout as AntLayout, 
  Menu, 
  Button, 
  Space, 
  Badge, 
  Avatar, 
  Dropdown,
  Typography,
  Tooltip
} from 'antd';
import { 
  ShoppingCartOutlined, 
  UserOutlined, 
  LoginOutlined,
  LogoutOutlined,
  FileTextOutlined,
  SearchOutlined,
  HomeOutlined
} from '@ant-design/icons';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate, useLocation } from 'react-router-dom';
import { RootState, AppDispatch } from '../store';
import { 
  showLoginModal, 
  clearCurrentUser 
} from '../store/slices/userSlice';
import { openCart } from '../store/slices/cartSlice';
import SearchBar from './SearchBar';
import LoginModal from './LoginModal';
import RegisterModal from './RegisterModal';

const { Header, Content } = AntLayout;
const { Title } = Typography;

interface LayoutProps {
  children: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const dispatch = useDispatch<AppDispatch>();
  
  const { currentUser, isLoggedIn } = useSelector((state: RootState) => state.user);
  const { totalQuantity } = useSelector((state: RootState) => state.cart);
  
  // 处理用户菜单点击
  const handleUserMenuClick = ({ key }: { key: string }) => {
    switch (key) {
      case 'orders':
        navigate('/orders');
        break;
      case 'logout':
        dispatch(clearCurrentUser());
        navigate('/');
        break;
      default:
        break;
    }
  };
  
  // 用户下拉菜单
  const userMenu = (
    <Menu onClick={handleUserMenuClick}>
      <Menu.Item key="orders" icon={<FileTextOutlined />}>
        我的订单
      </Menu.Item>
      <Menu.Divider />
      <Menu.Item key="logout" icon={<LogoutOutlined />}>
        退出登录
      </Menu.Item>
    </Menu>
  );
  
  // 获取当前页面菜单选中状态
  const getSelectedMenuKey = () => {
    const path = location.pathname;
    if (path === '/') return 'home';
    if (path === '/products') return 'products';
    if (path.startsWith('/search')) return 'search';
    if (path.startsWith('/cart')) return 'cart';
    if (path.startsWith('/orders')) return 'orders';
    return 'home';
  };
  
  return (
    <AntLayout style={{ minHeight: '100vh' }}>
      <Header style={{ 
        display: 'flex', 
        alignItems: 'center', 
        justifyContent: 'space-between',
        backgroundColor: '#fff',
        borderBottom: '1px solid #f0f0f0',
        padding: '0 24px'
      }}>
        {/* 左侧Logo和主导航 */}
        <div style={{ display: 'flex', alignItems: 'center', flex: 1 }}>
          <div 
            style={{ 
              marginRight: 32, 
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center'
            }}
            onClick={() => navigate('/')}
          >
            <Title level={3} style={{ margin: 0, color: '#1890ff' }}>
              招财商城
            </Title>
          </div>
          
          <Menu 
            mode="horizontal" 
            selectedKeys={[getSelectedMenuKey()]}
            style={{ 
              border: 'none',
              backgroundColor: 'transparent',
              marginRight: 32
            }}
          >
            <Menu.Item key="home" onClick={() => navigate('/')}>
              首页
            </Menu.Item>
            <Menu.Item key="products" onClick={() => navigate('/products')}>
              全部商品
            </Menu.Item>
            <Menu.Item key="orders" onClick={() => navigate('/orders')}>
              我的订单
            </Menu.Item>
          </Menu>
          
          {/* 搜索框 */}
          <div style={{ flex: 1, maxWidth: 400 }}>
            <SearchBar 
              placeholder="搜索商品..." 
              size="middle"
            />
          </div>
        </div>
        
        {/* 右侧用户操作 */}
        <Space size="large">
          {/* 购物车 */}
          <Badge count={totalQuantity} size="small">
            <Button 
              type="text" 
              icon={<ShoppingCartOutlined />} 
              size="large"
              onClick={() => navigate('/cart')}
            >
              购物车
            </Button>
          </Badge>
          
          {/* 用户信息 */}
          {isLoggedIn && currentUser ? (
            <Dropdown overlay={userMenu} placement="bottomRight">
              <Space style={{ cursor: 'pointer' }}>
                <Avatar icon={<UserOutlined />} />
                <span>{currentUser.username}</span>
              </Space>
            </Dropdown>
          ) : (
            <Button 
              type="primary" 
              icon={<LoginOutlined />}
              onClick={() => dispatch(showLoginModal())}
            >
              登录
            </Button>
          )}
        </Space>
      </Header>
      
      <Content style={{ backgroundColor: '#f5f5f5', position: 'relative' }}>
        {children}
      </Content>
      
      {/* 模态框 */}
      <LoginModal />
      <RegisterModal />
    </AntLayout>
  );
};

export default Layout; 