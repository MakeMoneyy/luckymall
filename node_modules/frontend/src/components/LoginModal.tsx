import React from 'react';
import { 
  Modal, 
  Form, 
  Input, 
  Button, 
  Checkbox, 
  message, 
  Divider,
  Space 
} from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { useDispatch, useSelector } from 'react-redux';
import { RootState, AppDispatch } from '../store';
import { 
  setCurrentUser, 
  hideLoginModal, 
  showRegisterModal, 
  setLoading, 
  setError 
} from '../store/slices/userSlice';

interface LoginFormData {
  username: string;
  password: string;
  remember: boolean;
}

const LoginModal: React.FC = () => {
  const [form] = Form.useForm();
  const dispatch = useDispatch<AppDispatch>();
  
  const { loginModalVisible, loading, error } = useSelector(
    (state: RootState) => state.user
  );
  
  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      dispatch(setLoading(true));
      
      // 模拟登录API调用
      setTimeout(() => {
        // 模拟登录成功
        const mockUser = {
          id: 1,
          username: values.username,
          email: `${values.username}@example.com`,
          phone: '13800138000',
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString()
        };
        
        dispatch(setCurrentUser(mockUser));
        dispatch(setLoading(false));
        message.success('登录成功！');
        form.resetFields();
      }, 1000);
      
    } catch (errorInfo) {
      console.log('表单验证失败:', errorInfo);
    }
  };
  
  const handleCancel = () => {
    dispatch(hideLoginModal());
    form.resetFields();
  };
  
  const handleShowRegister = () => {
    dispatch(showRegisterModal());
  };
  
  return (
    <Modal
      title="用户登录"
      open={loginModalVisible}
      onOk={handleOk}
      onCancel={handleCancel}
      confirmLoading={loading}
      okText="登录"
      cancelText="取消"
      width={400}
    >
      <Form
        form={form}
        name="login"
        initialValues={{ remember: true }}
        autoComplete="off"
        layout="vertical"
      >
        {error && (
          <div style={{ marginBottom: 16, color: '#ff4d4f', textAlign: 'center' }}>
            {error}
          </div>
        )}
        
        <Form.Item
          label="用户名"
          name="username"
          rules={[
            { required: true, message: '请输入用户名!' },
            { min: 3, message: '用户名至少3个字符!' }
          ]}
        >
          <Input 
            prefix={<UserOutlined />} 
            placeholder="请输入用户名" 
            size="large"
          />
        </Form.Item>
        
        <Form.Item
          label="密码"
          name="password"
          rules={[
            { required: true, message: '请输入密码!' },
            { min: 6, message: '密码至少6个字符!' }
          ]}
        >
          <Input.Password 
            prefix={<LockOutlined />} 
            placeholder="请输入密码" 
            size="large"
          />
        </Form.Item>
        
        <Form.Item name="remember" valuePropName="checked">
          <Checkbox>记住我</Checkbox>
        </Form.Item>
      </Form>
      
      <Divider plain>其他操作</Divider>
      
      <Space direction="vertical" style={{ width: '100%', textAlign: 'center' }}>
        <Button type="link" onClick={handleShowRegister}>
          还没有账号？立即注册
        </Button>
        <Button type="link" size="small">
          忘记密码？
        </Button>
      </Space>
    </Modal>
  );
};

export default LoginModal; 