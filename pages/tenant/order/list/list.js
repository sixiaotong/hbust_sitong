Page({
  data: {
    orders: []
  },
  
  onLoad() {
    this.loadOrders();
  },
  
  loadOrders() {
    const orders = wx.getStorageSync('orders') || [];
    this.setData({ orders });
  },
  
  goBack() {
    wx.navigateBack();
  },
  
  goToHouse() {
    wx.switchTab({
      url: '/pages/tenant/index/index'
    });
  },
  
  cancelOrder(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '确认取消',
      content: '确定要取消这个订单吗？',
      success: (res) => {
        if (res.confirm) {
          let orders = wx.getStorageSync('orders') || [];
          orders = orders.map(order => {
            if (order.id == id) {
              return { ...order, status: 'cancelled', statusText: '已取消' };
            }
            return order;
          });
          
          wx.setStorageSync('orders', orders);
          wx.showToast({ title: '已取消' });
          this.loadOrders();
        }
      }
    });
  },
  
  viewOrderDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '订单详情',
      content: '查看订单详情功能开发中',
      showCancel: false
    });
  }
});