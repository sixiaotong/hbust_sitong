// pages/tenant/detail/detail.js
Page({
  data: {
    house: {},         // 房源详情
    comment: '',       // 评论内容
    showShare: false,  // 分享面板
    isCollected: false // 是否已收藏
  },

  onLoad(options) {
    // 从URL参数获取房源ID
    const houseId = options.id;
    this.loadHouseDetail(houseId);
  },

  // 加载房源详情
  loadHouseDetail(id) {
    const houses = wx.getStorageSync('houses') || [];
    const foundHouse = houses.find(item => item.id == id);
    
    if (foundHouse) {
      // 检查是否已收藏
      const favs = wx.getStorageSync('favs') || [];
      const isCollected = favs.some(item => item.id == foundHouse.id);
      
      this.setData({
        house: {
          ...foundHouse,
          comments: foundHouse.comments || []
        },
        isCollected
      });
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

  // 输入评论
  onCommentInput(e) {
    this.setData({ comment: e.detail.value });
  },

  // 发送评论
  sendComment() {
    const { comment, house } = this.data;
    
    if (!comment.trim()) {
      wx.showToast({
        title: '请输入评论内容',
        icon: 'none'
      });
      return;
    }

    if (comment.length > 200) {
      wx.showToast({
        title: '评论不能超过200字',
        icon: 'none'
      });
      return;
    }

    // 保存评论
    const houses = wx.getStorageSync('houses') || [];
    const updatedHouses = houses.map(item => {
      if (item.id == house.id) {
        const newComment = {
          id: Date.now(),
          content: comment,
          time: new Date().toLocaleString(),
          user: '当前用户'
        };
        item.comments = [...(item.comments || []), newComment];
      }
      return item;
    });

    wx.setStorageSync('houses', updatedHouses);
    
    // 更新页面数据
    const updatedComments = [...(house.comments || []), {
      id: Date.now(),
      content: comment,
      time: new Date().toLocaleString(),
      user: '当前用户'
    }];

    this.setData({
      'house.comments': updatedComments,
      comment: ''
    });

    wx.showToast({
      title: '评论成功',
      icon: 'success'
    });
  },

  // 收藏/取消收藏
  toggleFavorite() {
    const { house, isCollected } = this.data;
    let favs = wx.getStorageSync('favs') || [];
    
    if (isCollected) {
      // 取消收藏
      favs = favs.filter(item => item.id != house.id);
      wx.showToast({
        title: '已取消收藏',
        icon: 'success'
      });
    } else {
      // 添加收藏
      if (!favs.some(item => item.id == house.id)) {
        favs.push(house);
        wx.showToast({
          title: '收藏成功',
          icon: 'success'
        });
      }
    }
    
    wx.setStorageSync('favs', favs);
    this.setData({ isCollected: !isCollected });
  },

  // 立即预订
  makeReservation() {
    const { house } = this.data;
    
    wx.showModal({
      title: '确认预订',
      content: `确定要预订 "${house.title}" 吗？`,
      success: (res) => {
        if (res.confirm) {
          this.processReservation();
        }
      }
    });
  },

  // 处理预订逻辑
  processReservation() {
    const { house } = this.data;
    let orders = wx.getStorageSync('orders') || [];
    
    const order = {
      id: Date.now(),
      houseId: house.id,
      title: house.title,
      price: house.price,
      time: new Date().toLocaleString(),
      status: 'pending', // pending, confirmed, completed, cancelled
      statusText: '待确认'
    };
    
    orders.push(order);
    wx.setStorageSync('orders', orders);
    
    wx.showToast({
      title: '预订成功',
      icon: 'success',
      duration: 2000,
      complete: () => {
        // 跳转到订单页面
        setTimeout(() => {
          wx.navigateTo({
            url: '/pages/tenant/order/list'
          });
        }, 1500);
      }
    });
  },

  // 联系房东
  contactLandlord() {
    wx.showModal({
      title: '联系房东',
      content: '拨打电话：13800138000',
      showCancel: true,
      confirmText: '拨打',
      success: (res) => {
        if (res.confirm) {
          wx.makePhoneCall({
            phoneNumber: '13800138000'
          });
        }
      }
    });
  },

  // 分享房源
  shareHouse() {
    this.setData({ showShare: true });
  },

  // 关闭分享面板
  closeShare() {
    this.setData({ showShare: false });
  },

  // 预览图片
  previewImage(e) {
    const index = e.currentTarget.dataset.index;
    const images = this.data.house.images || [];
    
    wx.previewImage({
      current: images[index],
      urls: images
    });
  },

  // 复制地址
  copyAddress() {
    const { house } = this.data;
    wx.setClipboardData({
      data: house.address,
      success: () => {
        wx.showToast({
          title: '地址已复制',
          icon: 'success'
        });
      }
    });
  },

  // 导航到房源位置
  navigateToLocation() {
    const { house } = this.data;
    const location = house.location || {};
    
    if (location.latitude && location.longitude) {
      wx.openLocation({
        latitude: location.latitude,
        longitude: location.longitude,
        name: house.title,
        address: house.address
      });
    } else {
      wx.showToast({
        title: '暂无位置信息',
        icon: 'none'
      });
    }
  },

  // 返回上一页
  goBack() {
    wx.navigateBack();
  },

  // 页面分享
  onShareAppMessage() {
    const { house } = this.data;
    return {
      title: house.title || '优质房源推荐',
      path: `/pages/tenant/detail/detail?id=${house.id}`
    };
  }
});