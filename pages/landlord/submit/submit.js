Page({
  formSubmit(e) {
    const val = e.detail.value;
    // 简单校验
    if (!val.title || !val.price) {
      return wx.showToast({ title: '请填写完整信息', icon: 'none' });
    }

    let all = wx.getStorageSync('houses') || [];
    const newHouse = {
      id: Date.now(), // 唯一ID
      title: val.title,
      price: parseInt(val.price),
      area: parseInt(val.area),
      type: val.type,
      desc: val.desc,
      landlordId: 'landlord_1', // 对应当前房东
      status: 'pending', // 提交后进入待审核状态
      image: 'https://picsum.photos/id/1/400/300', // 默认图
      comments: []
    };

    all.unshift(newHouse); // 放在列表最前面
    wx.setStorageSync('houses', all);

    wx.showToast({ title: '已提交，请等待审核' });
    setTimeout(() => {
      wx.navigateBack();
    }, 1500);
  }
})