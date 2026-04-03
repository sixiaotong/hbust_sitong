Page({
  data: {
    favorites: []
  },
  
  onLoad() {
    this.loadFavorites();
  },
  
  loadFavorites() {
    const favorites = wx.getStorageSync('favs') || [];
    this.setData({ favorites });
  },
  
  goBack() {
    wx.navigateBack();
  },
  
  goToHouse() {
    wx.switchTab({
      url: '/pages/tenant/index/index'
    });
  },
  
  viewHouse(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/tenant/detail/detail?id=${id}`,
      fail: () => {
        wx.showToast({
          title: '房源详情页不存在',
          icon: 'none'
        });
      }
    });
  },
  
  removeFavorite(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '确认移除',
      content: '确定要从收藏中移除吗？',
      success: (res) => {
        if (res.confirm) {
          let favorites = wx.getStorageSync('favs') || [];
          favorites = favorites.filter(item => item.id != id);
          
          wx.setStorageSync('favs', favorites);
          wx.showToast({ title: '已移除' });
          this.loadFavorites();
        }
      }
    });
  }
});