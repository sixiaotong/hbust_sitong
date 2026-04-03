Page({
  data: {
    house: null
  },

  onLoad(options) {
    const houseId = options.id;
    this.loadHouseDetail(houseId);
  },

  loadHouseDetail(houseId) {
    const allHouses = wx.getStorageSync('houses') || [];
    const house = allHouses.find(h => h.id == houseId);
    
    if (house) {
      this.setData({ house });
    } else {
      wx.showToast({
        title: '房源不存在',
        icon: 'error',
        complete: () => {
          setTimeout(() => wx.navigateBack(), 1500);
        }
      });
    }
  },

  getStatusValue(status) {
    const statusMap = {
      'active': 2, '在租中': 2,
      'pending': 1, '审核中': 1,
      'offline': 0, '已下架': 0,
      'draft': 3, '草稿': 3
    };
    return statusMap[status] || 0;
  },

  getStatusText(status) {
    const statusMap = {
      'active': '在租中', '在租中': '在租中',
      'pending': '审核中', '审核中': '审核中',
      'offline': '已下架', '已下架': '已下架',
      'draft': '草稿', '草稿': '草稿'
    };
    return statusMap[status] || '未知状态';
  },

  editHouse() {
    const houseId = this.data.house.id;
    wx.navigateTo({
      url: `/pages/landlord/houses/edit?id=${houseId}`
    });
  },

  deleteHouse() {
    const houseId = this.data.house.id;
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这个房源吗？此操作不可恢复。',
      success: (res) => {
        if (res.confirm) {
          this.deleteHouseFromStorage(houseId);
        }
      }
    });
  },

  deleteHouseFromStorage(houseId) {
    let allHouses = wx.getStorageSync('houses') || [];
    allHouses = allHouses.filter(h => h.id != houseId);
    
    wx.setStorageSync('houses', allHouses);
    
    wx.showToast({
      title: '删除成功',
      icon: 'success',
      complete: () => {
        setTimeout(() => {
          wx.navigateBack();
          // 通知列表页面更新
          const pages = getCurrentPages();
          const prevPage = pages[pages.length - 2];
          if (prevPage && prevPage.loadHousesData) {
            prevPage.loadHousesData();
          }
        }, 1500);
      }
    });
  },

  goBack() {
    wx.navigateBack();
  }
});