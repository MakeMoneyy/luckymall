import React from 'react';
import { 
  Modal, 
  Form, 
  Input, 
  Button, 
  message, 
  Divider,
  Space 
} from 'antd';
import { UserOutlined, LockOutlined, MailOutlined, PhoneOutlined } from '@ant-design/icons';
import { useDispatch, useSelector } from 'react-redux';
import { RootState, AppDispatch } from '../store';
import { 
  hideRegisterModal, 
  showLoginModal, 
  setLoading, 
  setError 
} from '../store/slices/userSlice';

interface RegisterFormData {
  username: string;
  email: string;
  phone: string;
  password: string;
  confirmPassword: string;
}

const RegisterModal: React.FC = () => {
  const [form] = Form.useForm();
  const dispatch = useDispatch<AppDispatch>();
  
  const { registerModalVisible, loading, error } = useSelector(
    (state: RootState) => state.user
  );
  
  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      dispatch(setLoading(true));
      
      // 模拟注册API调用
      setTimeout(() => {
        dispatch(setLoading(false));
        message.success('注册成功！请登录');
        form.resetFields();
        dispatch(hideRegisterModal());
        dispatch(showLoginModal());
      }, 1000);
      
    } catch (errorInfo) {
      console.log('表单验证失败:', errorInfo);
    }
  };
  
  const handleCancel = () => {
    dispatch(hideRegisterModal());
    form.resetFields();
  };
  
  const handleShowLogin = () => {
    dispatch(showLoginModal());
  };
  
  return (
    <Modal
      title="用户注册"
      open={registerModalVisible}
      onOk={handleOk}
      onCancel={handleCancel}
      confirmLoading={loading}
      okText="注册"
      cancelText="取消"
      width={450}
    >
      <Form
        form={form}
        name="register"
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
            { min: 3, max: 20, message: '用户名长度为3-20个字符!' },
            { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线!' }
          ]}
        >
          <Input 
            prefix={<UserOutlined />} 
            placeholder="请输入用户名" 
            size="large"
          />
        </Form.Item>
        
        <Form.Item
          label="邮箱"
          name="email"
          rules={[
            { required: true, message: '请输入邮箱!' },
            { type: 'email', message: '请输入有效的邮箱地址!' }
          ]}
        >
          <Input 
            prefix={<MailOutlined />} 
            placeholder="请输入邮箱" 
            size="large"
          />
        </Form.Item>
        
        <Form.Item
          label="手机号"
          name="phone"
          rules={[
            { required: true, message: '请输入手机号!' },
            { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号!' }
          ]}
        >
          <Input 
            prefix={<PhoneOutlined />} 
            placeholder="请输入手机号" 
            size="large"
          />
        </Form.Item>
        
        <Form.Item
          label="密码"
          name="password"
          rules={[
            { required: true, message: '请输入密码!' },
            { min: 6, max: 20, message: '密码长度为6-20个字符!' }
          ]}
        >
          <Input.Password 
            prefix={<LockOutlined />} 
            placeholder="请输入密码" 
            size="large"
          />
        </Form.Item>
        
        <Form.Item
          label="确认密码"
          name="confirmPassword"
          dependencies={['password']}
          rules={[
            { required: true, message: '请确认密码!' },
            ({ getFieldValue }) => ({
              validator(_, value) {
                if (!value || getFieldValue('password') === value) {
                  return Promise.resolve();
                }
                return Promise.reject(new Error('两次输入的密码不一致!'));
              },
            }),
          ]}
        >
          <Input.Password 
            prefix={<LockOutlined />} 
            placeholder="请确认密码" 
            size="large"
          />
        </Form.Item>
      </Form>
      
      <Divider plain>其他操作</Divider>
      
      <Space direction="vertical" style={{ width: '100%', textAlign: 'center' }}>
        <Button type="link" onClick={handleShowLogin}>
          已有账号？立即登录
        </Button>
      </Space>
    </Modal>
  );
};

export default RegisterModal; 