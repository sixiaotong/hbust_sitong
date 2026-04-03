Page({
  data: {
    allHouses: [],
    filteredHouses: [],
    currentFilter: 'all',
    totalCount: 0,
    activeCount: 0,
    pendingCount: 0
  },

  onShow() {
    this.loadHouses();
  },

  loadHouses() {
    const allHouses = wx.getStorageSync('houses') || [];
    const myHouses = allHouses.filter(h => h.landlordId === 'landlord_1');
    
    // 统计数量
    const totalCount = myHouses.length;
    const activeCount = myHouses.filter(h => 
      h.status === 'active' || h.status === '在租中'
    ).length;
    const pendingCount = myHouses.filter(h => 
      h.status === 'pending' || h.status === '审核中'
    ).length;
    
    // 按时间排序
    const sortedHouses = myHouses.sort((a, b) => 
      new Date(b.publishTime || b.createTime) - new Date(a.publishTime || a.createTime)
    );
    
    this.setData({
      allHouses: sortedHouses,
      filteredHouses: sortedHouses,
      totalCount,
      activeCount,
      pendingCount
    });
    
    // 应用当前筛选
    this.applyFilter(this.data.currentFilter);
  },

  setFilter(e) {
    const filter = e.currentTarget.dataset.filter;
    this.setData({ currentFilter: filter });
    this.applyFilter(filter);
  },

  applyFilter(filter) {
    let filteredHouses = this.data.allHouses;
    
    if (filter === 'active') {
      filteredHouses = filteredHouses.filter(h => 
        h.status === 'active' || h.status === '在租中'
      );
    } else if (filter === 'pending') {
      filteredHouses = filteredHouses.filter(h => 
        h.status === 'pending' || h.status === '审核中'
      );
    }
    
    this.setData({ filteredHouses });
  },

  getStatusValue(status) {
    const statusMap = {
      'active': 2, '在租中': 2,
      'pending': 1, '审核中': 1,
      'offline': 0, '已下架': 0
    };
    return statusMap[status] || 0;
  },

  getStatusText(status) {
    const statusMap = {
      'active': '在租中', '在租中': '在租中',
      'pending': '审核中', '审核中': '审核中',
      'offline': '已下架', '已下架': '已下架'
    };
    return statusMap[status] || '未知状态';
  },

  formatTime(time) {
    if (!time) return '未知时间';
    const date = new Date(time);
    const now = new Date();
    
    if (date.toDateString() === now.toDateString()) {
      return `今天 ${date.getHours()}:${date.getMinutes().toString().padStart(2, '0')}`;
    }
    
    const yesterday = new Date(now);
    yesterday.setDate(now.getDate() - 1);
    if (date.toDateString() === yesterday.toDateString()) {
      return `昨天 ${date.getHours()}:${date.getMinutes().toString().padStart(2, '0')}`;
    }
    
    return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
  },

  viewDetail(e) {
    const houseId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/landlord/houses/detail?id=${houseId}`
    });
  },

  addNewHouse() {
    wx.navigateTo({
      url: '/pages/landlord/houses/publish'
    });
  },

  goBack() {
    wx.navigateBack();
  }
});