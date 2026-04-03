Page({
  data: {
    showLoginForm: false,
    selectedRole: '',
    username: '',
    password: '',
    showPassword: false
  },

  // 原有的登录方式 - 直接跳转
  loginAs: function(e) {
    const role = e.currentTarget.dataset.role;
    
    // 保存角色到本地存储
    wx.setStorageSync('userRole', role);
    
    // 显示加载状态
    wx.showLoading({
      title: '正在进入...',
      mask: true
    });
    
    // 延迟跳转，让加载动画显示
    setTimeout(() => {
      wx.hideLoading();
      wx.reLaunch({ 
        url: `/pages/${role}/index/index` 
      });
    }, 800);
  },

  // 新功能：带账号密码的登录
  selectRole: function(e) {
    const role = e.currentTarget.dataset.role;
    
    this.setData({
      selectedRole: role,
      showLoginForm: true,
      username: '',
      password: ''
    });
  },

  goBackToRoleSelect: function() {
    this.setData({
      showLoginForm: false,
      selectedRole: '',
      username: '',
      password: ''
    });
  },

  onUsernameInput: function(e) {
    this.setData({
      username: e.detail.value.trim()
    });
  },

  onPasswordInput: function(e) {
    this.setData({
      password: e.detail.value
    });
  },

  togglePassword: function() {
    this.setData({
      showPassword: !this.data.showPassword
    });
  },

  fillTestAccount: function() {
    this.setData({
      username: '777',
      password: '1234567'
    });
    
    wx.showToast({
      title: '测试账号已填充',
      icon: 'success',
      duration: 1500
    });
  },

  handleLogin: function() {
    const { username, password, selectedRole } = this.data;
    
    if (!username || !password) {
      wx.showToast({
        title: '请输入账号密码',
        icon: 'none',
        duration: 2000
      });
      return;
    }

    // 验证测试账号
    if (username === '777' && password === '1234567') {
      wx.showLoading({
        title: '登录中...',
        mask: true
      });
      
      setTimeout(() => {
        wx.hideLoading();
        
        // 保存角色和用户信息
        wx.setStorageSync('userRole', selectedRole);
        wx.setStorageSync('username', username);
        
        wx.showToast({
          title: '登录成功！',
          icon: 'success',
          duration: 1500
        });
        
        // 跳转到对应角色页面
        setTimeout(() => {
          wx.reLaunch({ 
            url: `/pages/${selectedRole}/index/index` 
          });
        }, 1500);
        
      }, 1500);
    } else {
      wx.showToast({
        title: '账号或密码错误\n请使用测试账号:777 密码:1234567',
        icon: 'none',
        duration: 3000
      });
    }
  }
});