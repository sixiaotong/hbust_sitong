// pages/admin/profile/profile.js
Page({
  /**
   * 页面的初始数据
   */
  data: {
    totalApprove: 0,
    totalReject: 0,
    totalPending: 0,
    isPlaying: false // 音乐播放状态控制
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    // 1. 初始化音频上下文
    this.audioCtx = wx.createInnerAudioContext();
    
    // 2. 设置音频线上地址 (XSimple 提醒：请确保网址有效)
    this.audioCtx.src = 'https://m801.music.126.net/20251225173506/4842693f76f36e1fc0d3de2cd2ef5ef0/jdymusic/obj/wo3DlMOGwrbDjj7DisKw/28481711284/6dc0/f58f/bc29/cd16662177fd431d137cb5837c1be8d1.mp3?vuutv=RfPmOYls9D9GpS5ocZLDiw/p928VhvNoDTFJvjLOi7omtxQeGHPnQx9DWJmRaMfJ7pb1abVCCi9ZTv9TwIt0n3ho08nKpDayLX4N6K8l4ZQ='; 
    
    // 3. 监听音频事件
    this.audioCtx.onEnded(() => {
      this.setData({ isPlaying: false });
    });
    
    this.audioCtx.onError((res) => {
      console.error('音频播放错误：', res);
      wx.showToast({ title: '音频加载失败', icon: 'none' });
    });
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    // 刷新房源统计数据
    const all = wx.getStorageSync('houses') || [];
    this.setData({
      totalApprove: all.filter(h => h.status === 'approved').length,
      totalReject: all.filter(h => h.status === 'rejected').length,
      totalPending: all.filter(h => h.status === 'pending').length
    });
  },

  /**
   * 自定义方法：切换音乐播放状态
   */
  toggleMusic: function () {
    if (this.data.isPlaying) {
      this.audioCtx.pause();
      this.setData({ isPlaying: false });
    } else {
      this.audioCtx.play();
      this.setData({ isPlaying: true });
    }
  },

  /**
   * 自定义方法：根据状态跳转房源列表
   */
  goList: function (e) {
    const status = e.currentTarget.dataset.status;
    // 重定向到首页并携带状态参数
    wx.reLaunch({ url: `/pages/admin/index/index?status=${status}` });
  },

  /**
   * 自定义方法：底部导航跳转
   */
  navTo: function (e) {
    const url = e.currentTarget.dataset.url;
    wx.reLaunch({ url: url });
  },

  /**
   * 自定义方法：退出登录
   */
  logout: function () {
    wx.showModal({
      title: '提示',
      content: '确定要退出管理系统吗？',
      success: (res) => {
        if (res.confirm) {
          wx.reLaunch({ url: '/pages/login/login' });
        }
      }
    });
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {
    // 页面销毁时一定要销毁音频实例，释放资源
    if (this.audioCtx) {
      this.audioCtx.destroy();
    }
  }
})