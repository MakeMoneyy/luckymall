import React, { useState } from 'react';
import { Input, Button, Space } from 'antd';
import { SearchOutlined } from '@ant-design/icons';
import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { AppDispatch } from '../store';
import { searchProducts } from '../store/slices/productSlice';

const { Search } = Input;

interface SearchBarProps {
  placeholder?: string;
  size?: 'small' | 'middle' | 'large';
  style?: React.CSSProperties;
  onSearch?: (keyword: string) => void;
}

const SearchBar: React.FC<SearchBarProps> = ({
  placeholder = "搜索商品...",
  size = 'middle',
  style,
  onSearch
}) => {
  const [keyword, setKeyword] = useState('');
  const dispatch = useDispatch<AppDispatch>();
  const navigate = useNavigate();
  
  const handleSearch = (value: string) => {
    const searchKeyword = value.trim();
    if (searchKeyword) {
      // 执行搜索
      dispatch(searchProducts({ 
        keyword: searchKeyword,
        current: 1,
        size: 12 
      }));
      
      // 跳转到搜索结果页面
      navigate(`/search?keyword=${encodeURIComponent(searchKeyword)}`);
      
      // 如果有自定义搜索回调，也执行它
      if (onSearch) {
        onSearch(searchKeyword);
      }
    }
  };
  
  return (
    <Search
      placeholder={placeholder}
      size={size}
      style={style}
      value={keyword}
      onChange={(e) => setKeyword(e.target.value)}
      onSearch={handleSearch}
      enterButton={
        <Button type="primary" icon={<SearchOutlined />}>
          搜索
        </Button>
      }
      allowClear
    />
  );
};

export default SearchBar; 