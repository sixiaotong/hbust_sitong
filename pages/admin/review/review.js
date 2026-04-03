Page({
  data: { h: {}, reason: '' },
  onLoad(opt) {
    const all = wx.getStorageSync('houses') || [];
    const target = all.find(x => x.id == opt.id);
    this.setData({ h: target });
  },
  inputReason(e) { this.setData({ reason: e.detail.value }); },
  doAudit(e) {
    const status = e.currentTarget.dataset.res;
    if (status === 'rejected' && !this.data.reason) {
      return wx.showToast({ title: '驳回请输入原因', icon: 'none' });
    }

    let all = wx.getStorageSync('houses') || [];
    all = all.map(item => {
      if (item.id == this.data.h.id) {
        item.status = status;
        item.refuseReason = status === 'rejected' ? this.data.reason : '';
      }
      return item;
    });

    wx.setStorageSync('houses', all);
    wx.showToast({ title: status === 'approved' ? '审核通成功' : '已成功驳回' });
    
    setTimeout(() => { wx.navigateBack(); }, 1000);
  }
})