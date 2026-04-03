Page({
  data: { myHouses: [] },
  onShow() {
    // 模拟当前登录房东ID为 landlord_1
    const all = wx.getStorageSync('houses') || [];
    const my = all.filter(h => h.landlordId === 'landlord_1');
    this.setData({ myHouses: my });
  },
  goSubmit() {
    wx.navigateTo({ url: '/pages/landlord/submit/submit' });
  },
  navTo(e) {
    wx.reLaunch({ url: e.currentTarget.dataset.url });
  }
})