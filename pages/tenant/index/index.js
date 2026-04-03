Page({
  data: { houses: [] },
  onShow() {
    const all = wx.getStorageSync('houses') || [];
    this.setData({ houses: all.filter(h => h.status === 'approved') });
  },
  goDetail(e) { wx.navigateTo({ url: `/pages/tenant/detail/detail?id=${e.currentTarget.dataset.id}` }); },
  goMine() { wx.redirectTo({ url: '/pages/tenant/profile/profile' }); }
})