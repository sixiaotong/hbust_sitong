Page({
  data: {
    currentTab: 'pending', // 默认显示待审核
    allHouse: [],
    displayList: []
  },
  onLoad(options) {
    // 支持从个人中心传参跳转
    if (options.status) {
      this.setData({ currentTab: options.status });
    }
  },
  onShow() {
    this.loadData();
  },
  loadData() {
    const all = wx.getStorageSync('houses') || [];
    const filtered = all.filter(h => h.status === this.data.currentTab);
    this.setData({ 
      allHouse: all,
      displayList: filtered 
    });
  },
  switchTab(e) {
    const status = e.currentTarget.dataset.status;
    this.setData({ currentTab: status }, () => {
      this.loadData();
    });
  },
  goReview(e) {
    wx.navigateTo({ url: `/pages/admin/review/review?id=${e.currentTarget.dataset.id}` });
  },
  navTo(e) {
    wx.reLaunch({ url: e.currentTarget.dataset.url });
  }
})