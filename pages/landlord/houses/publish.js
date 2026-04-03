Page({
  data: {
    form: {
      title: '',
      price: '',
      area: '',
      bedroom: '1',
      livingroom: '1',
      district: '',
      address: '',
      description: '',
      contactName: '',
      contactPhone: ''
    },
    isSubmitting: false
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    
    this.setData({
      [`form.${field}`]: value
    });
  },

  validateForm() {
    const requiredFields = ['title', 'price', 'area', 'district'];
    const missingFields = [];
    
    requiredFields.forEach(field => {
      if (!this.data.form[field] || this.data.form[field].trim() === '') {
        missingFields.push(field);
      }
    });
    
    if (missingFields.length > 0) {
      wx.showToast({
        title: '请填写必填字段',
        icon: 'error'
      });
      return false;
    }
    
    if (isNaN(this.data.form.price) || this.data.form.price <= 0) {
      wx.showToast({
        title: '价格必须大于0',
        icon: 'error'
      });
      return false;
    }
    
    return true;
  },

  saveHouse(status) {
    if (status === 'active' && !this.validateForm()) {
      return;
    }
    
    this.setData({ isSubmitting: true });
    
    // 生成房源ID
    const houseId = 'house_' + Date.now();
    
    const houseData = {
      id: houseId,
      landlordId: 'landlord_1',
      title: this.data.form.title,
      price: parseFloat(this.data.form.price),
      area: parseFloat(this.data.form.area),
      bedroom: parseInt(this.data.form.bedroom) || 1,
      livingroom: parseInt(this.data.form.livingroom) || 1,
      district: this.data.form.district,
      address: this.data.form.address || '',
      description: this.data.form.description || '',
      contactName: this.data.form.contactName || '',
      contactPhone: this.data.form.contactPhone || '',
      status: status,
      createTime: new Date().toISOString(),
      publishTime: status === 'active' ? new Date().toISOString() : null
    };
    
    // 保存到本地存储
    let allHouses = wx.getStorageSync('houses') || [];
    allHouses.push(houseData);
    wx.setStorageSync('houses', allHouses);
    
    // 显示成功消息
    const message = status === 'active' ? '发布成功，等待审核' : '已保存为草稿';
    
    wx.showToast({
      title: message,
      icon: 'success',
      complete: () => {
        setTimeout(() => {
          wx.navigateBack();
          // 通知其他页面更新
          const pages = getCurrentPages();
          pages.forEach(page => {
            if (page.loadHousesData) {
              page.loadHousesData();
            }
          });
        }, 1500);
      }
    });
  },

  saveAsDraft() {
    this.saveHouse('draft');
  },

  submitHouse() {
    this.saveHouse('pending');
  },

  goBack() {
    wx.navigateBack();
  }
});