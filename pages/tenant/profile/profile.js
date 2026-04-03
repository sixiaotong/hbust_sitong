// pages/tenant/profile/profile.js
Page({
  data: { 
    orders: [], 
    favs: [],
    activeTab: 'profile'
  },
  
  onShow() {
    this.loadUserData();
  },
  
  loadUserData() {
    // 模拟数据加载
    const storedOrders = wx.getStorageSync('orders');
    const storedFavs = wx.getStorageSync('favs');
    
    
    // 只显示最近3条数据
    const allOrders = wx.getStorageSync('orders') || [];
    const allFavs = wx.getStorageSync('favs') || [];
    
    this.setData({
      orders: allOrders.slice(0, 3),
      favs: allFavs.slice(0, 3)
    });
  },
  
  // 跳转到订单列表页
  goOrderList() {
    console.log('正在跳转到订单列表...');
    
    // 先检查页面是否存在
    wx.navigateTo({
      url: '/pages/tenant/order/list/list',
      success: (res) => {
        console.log('订单列表跳转成功');
      },
      fail: (err) => {
        console.error('订单列表跳转失败:', err);
        
        // 如果页面不存在，提示用户
        wx.showModal({
          title: '提示',
          content: '订单页面不存在，是否先创建订单列表页面？',
          confirmText: '去创建',
          cancelText: '取消',
          success: (res) => {
            if (res.confirm) {
              this.createOrderPage();
            }
          }
        });
      }
    });
  },
  
  // 跳转到收藏列表页
  goFavList() {
    console.log('正在跳转到收藏列表...');
    
    wx.navigateTo({
      url: '/pages/tenant/favorite/list/list',
      success: (res) => {
        console.log('收藏列表跳转成功');
      },
      fail: (err) => {
        console.error('收藏列表跳转失败:', err);
        
        // 如果页面不存在，创建收藏页面
        wx.showModal({
          title: '提示',
          content: '收藏页面不存在，是否先创建收藏列表页面？',
          confirmText: '去创建',
          cancelText: '取消',
          success: (res) => {
            if (res.confirm) {
              this.createFavoritePage();
            }
          }
        });
      }
    });
  },
  
  // 创建订单页面
  createOrderPage() {
    wx.showLoading({
      title: '正在创建订单页面...',
      mask: true
    });
    
    // 这里应该在实际项目中创建页面文件
    // 现在先跳转到首页
    setTimeout(() => {
      wx.hideLoading();
      wx.showToast({
        title: '请先配置订单页面路径',
        icon: 'none',
        duration: 2000
      });
      
      // 跳转到首页
      wx.switchTab({
        url: '/pages/tenant/index/index'
      });
    }, 1500);
  },
  
  // 创建收藏页面
  createFavoritePage() {
    wx.showLoading({
      title: '正在创建收藏页面...',
      mask: true
    });
    
    setTimeout(() => {
      wx.hideLoading();
      wx.showToast({
        title: '请先配置收藏页面路径',
        icon: 'none',
        duration: 2000
      });
      
      // 跳转到首页
      wx.switchTab({
        url: '/pages/tenant/index/index'
      });
    }, 1500);
  },
  
  // 查看订单详情
  viewOrder(e) {
    const index = e.currentTarget.dataset.index;
    const order = this.data.orders[index];
    
    wx.showModal({
      title: '订单详情',
      content: `
房源：${order.title}
时间：${order.time}
价格：¥${order.price}/月
状态：${order.statusText}
      `,
      showCancel: false,
      confirmText: '知道了'
    });
  },
  
  // 查看收藏详情
  viewFavorite(e) {
    const index = e.currentTarget.dataset.index;
    const fav = this.data.favs[index];
    
    wx.showModal({
      title: '收藏详情',
      content: `
房源：${fav.title}
价格：¥${fav.price}/月
面积：${fav.area || '--'}㎡
户型：${fav.layout || '--'}
地址：${fav.address || '--'}
      `,
      showCancel: false,
      confirmText: '知道了'
    });
  },
  
  // 跳转到首页
  goIndex() { 
    wx.redirectTo({ 
      url: '/pages/tenant/index/index',
      fail: (err) => {
        console.error('跳转首页失败:', err);
        // 如果跳转失败，尝试其他方式
        wx.switchTab({
          url: '/pages/index/index'
        });
      }
    }); 
  },
  
  // 退出登录
  logout() { 
    wx.showModal({
      title: '确认退出',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          // 清除本地存储
          wx.removeStorageSync('userRole');
          wx.removeStorageSync('username');
          wx.removeStorageSync('orders');
          wx.removeStorageSync('favs');
          
          wx.reLaunch({ 
            url: '/pages/login/login',
            fail: (err) => {
              console.error('跳转登录页失败:', err);
              wx.showToast({
                title: '页面不存在',
                icon: 'error'
              });
            }
          });
        }
      }
    });
  }
});