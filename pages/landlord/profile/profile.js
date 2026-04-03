Page({
  data: {
    // ... 原有的data数据保持不变
  },

  onShow() {
    this.loadHousesData();
  },

  // 加载房源数据（基于原版逻辑扩展）
  loadHousesData() {
    const allHouses = wx.getStorageSync('houses') || [];
    const myHouses = allHouses.filter(h => h.landlordId === 'landlord_1');
    
    // 统计各类房源数量
    const totalCount = myHouses.length;
    const activeCount = myHouses.filter(h => h.status === 'active' || h.status === '在租中').length;
    const pendingCount = myHouses.filter(h => h.status === 'pending' || h.status === '审核中').length;
    const offlineCount = myHouses.filter(h => h.status === 'offline' || h.status === '已下架').length;
    
    // 计算活跃率
    const activePercent = totalCount > 0 ? Math.round((activeCount / totalCount) * 100) : 0;
    
    // 获取最近发布的房源（最多3条）
    const recentPublishes = myHouses
      .sort((a, b) => new Date(b.publishTime || b.createTime) - new Date(a.publishTime || a.createTime))
      .slice(0, 3)
      .map(house => ({
        id: house.id,
        title: house.title || '未命名房源',
        area: house.area || 0,
        district: house.district || '未知区域',
        publishTime: this.formatTime(house.publishTime || house.createTime),
        status: this.getStatusValue(house.status),
        statusText: this.getStatusText(house.status)
      }));
    
    // 计算趋势（这里简化处理，实际可根据历史数据计算）
    const trend = 0; // 原版无此数据，设为0
    
    this.setData({
      count: totalCount, // 保留原版count
      totalCount: totalCount,
      activeCount: activeCount,
      pendingCount: pendingCount,
      offlineCount: offlineCount,
      activePercent: activePercent,
      trend: trend,
      lastUpdateTime: this.getCurrentTime(),
      recentPublishes: recentPublishes
    });
  },

  // 格式化时间
  formatTime(time) {
    if (!time) return '未知时间';
    
    const date = new Date(time);
    const now = new Date();
    
    // 今天
    if (date.toDateString() === now.toDateString()) {
      return `今天 ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
    }
    
    // 昨天
    const yesterday = new Date(now);
    yesterday.setDate(now.getDate() - 1);
    if (date.toDateString() === yesterday.toDateString()) {
      return `昨天 ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
    }
    
    // 更早的时间
    return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
  },

  // 获取当前时间
  getCurrentTime() {
    const now = new Date();
    return `${now.getFullYear()}-${(now.getMonth() + 1).toString().padStart(2, '0')}-${now.getDate().toString().padStart(2, '0')} ${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`;
  },

  // 获取状态数值
  getStatusValue(status) {
    const statusMap = {
      'active': 2,
      '在租中': 2,
      'pending': 1,
      '审核中': 1,
      'offline': 0,
      '已下架': 0,
      'draft': 3,
      '草稿': 3
    };
    return statusMap[status] || 0;
  },

  // 获取状态文本
  getStatusText(status) {
    const statusMap = {
      'active': '在租中',
      '在租中': '在租中',
      'pending': '审核中',
      '审核中': '审核中',
      'offline': '已下架',
      '已下架': '已下架',
      'draft': '草稿',
      '草稿': '草稿'
    };
    return statusMap[status] || '未知状态';
  },

  // ==================== 导航相关方法 ====================
  
  // 统一导航方法：根据data-url跳转（用于Tab栏跳转）
  navTo(e) {
    const url = e.currentTarget.dataset.url;
    if (url) {
      wx.reLaunch({ 
        url: url,
        fail: function(err) {
          console.error('导航失败:', err);
          wx.showToast({
            title: '页面不存在',
            icon: 'error'
          });
        }
      });
    }
  },
  
  // 查看房源详情（新功能）- 使用navigateTo保持页面栈
  viewHouseDetail(e) {
    const id = e.currentTarget.dataset.id;
    if (id) {
      wx.navigateTo({
        url: `/pages/landlord/houses/detail?id=${id}`,
        fail: function(err) {
          console.error('导航失败:', err);
          // 如果页面不存在，先创建页面路径
          wx.showModal({
            title: '提示',
            content: '房源详情页面未创建，是否先创建页面？',
            success: function(res) {
              if (res.confirm) {
                // 这里可以引导用户创建页面或使用其他方式
                wx.navigateTo({
                  url: '/pages/landlord/houses/records'
                });
              }
            }
          });
        }
      });
    }
  },

  // 导航到发布记录页面（新功能）
  navToRecords() {
    wx.navigateTo({
      url: '/pages/landlord/houses/records',
      fail: function(err) {
        console.error('导航失败:', err);
        wx.showModal({
          title: '页面未创建',
          content: '发布记录页面路径为：/pages/landlord/houses/records',
          showCancel: false
        });
      }
    });
  },

  // 导航到发布页面（新功能）
  navToPublish() {
    wx.navigateTo({
      url: '/pages/landlord/houses/publish',
      fail: function(err) {
        console.error('导航失败:', err);
        wx.showModal({
          title: '页面未创建',
          content: '发布页面路径为：/pages/landlord/houses/publish',
          showCancel: false
        });
      }
    });
  },

  // 显示数据图表（新功能）
  showDataChart() {
    wx.navigateTo({
      url: '/pages/landlord/statistics/echart',
      fail: function(err) {
        console.error('导航失败:', err);
        wx.showModal({
          title: '页面未创建',
          content: '敬请期待',
          showCancel: false
        });
      }
    });
  },

  // ==================== 模态框相关方法 ====================
  
  // 显示详情（新功能）
  showDetail(e) {
    const type = e.currentTarget.dataset.type;
    let title = '';
    
    switch(type) {
      case 'total':
        title = '累计发布';
        break;
      case 'active':
        title = '在租中';
        break;
      case 'pending':
        title = '待审核';
        break;
      case 'offline':
        title = '已下架';
        break;
    }
    
    this.setData({
      showStatsModal: true,
      modalTitle: title,
      modalType: type
    });
  },

  // 隐藏模态框（新功能）
  hideModal() {
    this.setData({
      showStatsModal: false
    });
  },

  // ==================== 退出登录方法 ====================
  
  // 保留原版退出功能
  logout() {
    wx.showModal({
      title: '确认退出',
      content: '确定要退出当前账号吗？',
      success: (res) => {
        if (res.confirm) {
          wx.reLaunch({ 
            url: '/pages/login/login',
            fail: function(err) {
              console.error('导航失败:', err);
              wx.showToast({
                title: '登录页面不存在',
                icon: 'error'
              });
            }
          });
        }
      }
    });
  },

  // ==================== 新增：调试方法 ====================
  
  // 检查页面是否存在
  checkPageExists(url) {
    return new Promise((resolve) => {
      wx.navigateTo({
        url: url,
        fail: () => resolve(false),
        success: () => {
          wx.navigateBack();
          resolve(true);
        }
      });
    });
  },
  
  // 显示所有页面路径
  showAllPaths() {
    const pages = [
      '/pages/landlord/index/index',
      '/pages/landlord/houses/detail',
      '/pages/landlord/houses/records',
      '/pages/landlord/houses/publish',
      '/pages/login/login'
    ];
    
    console.log('可用页面路径：', pages);
  }
});