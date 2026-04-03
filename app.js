App({
  onLaunch: function () {
    const houses = wx.getStorageSync('houses');
    if (!houses) {
      const houseList = [];
      // 前5个配置多张图片实现轮播
      houseList.push({ id: 1, title: "静安区·豪派大平层", type: "四室两厅", price: 25000, area: 180, desc: "全落地窗看江景，全屋大金空调，尊享私密梯控。", images: ["https://tse1-mm.cn.bing.net/th/id/OIP-C.yd941rK8DDKyMZxThSVbPQHaE-?w=298&h=200&c=7&r=0&o=7&cb=ucfimg2&dpr=1.8&pid=1.7&rm=3&ucfimg=1", "https://tse2-mm.cn.bing.net/th/id/OIP-C.H6t9BVsdj1-YrgDBUDfT8wHaFj?w=267&h=200&c=7&r=0&o=7&cb=ucfimg2&dpr=1.8&pid=1.7&rm=3&ucfimg=1", "https://tse3-mm.cn.bing.net/th/id/OIP-C.JoQAAy60B8zDxAcYS_5lmAHaFt?w=259&h=200&c=7&r=0&o=7&cb=ucfimg2&dpr=1.8&pid=1.7&rm=3&ucfimg=1"], status: "approved", comments: ["物业很棒"], landlordId: "landlord_1" });
      houseList.push({ id: 2, title: "徐汇·温馨北欧两居", type: "二室一厅", price: 8500, area: 75, desc: "清新装修，距离地铁站200米，拎包入住。", images: ["https://img95.699pic.com/photo/60028/2638.jpg_wh860.jpg", "https://img95.699pic.com/photo/60107/6829.jpg_wh860.jpg"], status: "approved", comments: ["装修很漂亮"], landlordId: "landlord_1" });
      houseList.push({ id: 3, title: "黄浦·复古老洋房", type: "一室三厅", price: 15000, area: 120, desc: "历史保护建筑，独门独院，带屋顶露台。", images: ["https://ts1.tc.mm.bing.net/th/id/OIP-C.sMAdIzyb1SMSFzJYyVzD-AHaEK?cb=ucfimg2&ucfimg=1&rs=1&pid=ImgDetMain&o=7&rm=3", "https://ts1.tc.mm.bing.net/th/id/R-C.10022ba90c1e76fd98295cc8fc90fe1c?rik=X2DT9TZONg1efA&riu=http%3a%2f%2fi1.cdn.yzz.cn%2fpub%2fimgx2020%2f09%2f30%2f503_154815_63f18.jpg&ehk=LtVl1WQYs4cY3BzIbm5xeg72mCzg7LG7E38alWIL7Aw%3d&risl=&pid=ImgRaw&r=0", "https://upload-bbs.mihoyo.com/upload/2020/03/17/81977977/d4a3cc0ee6e658c377f6095cd0b6d65a_1686754843809249213.png?x-oss-process=image/resize,s_600/quality,q_80/auto-orient,0/interlace,1/format,png"], status: "approved", comments: ["很有情调"], landlordId: "landlord_2" });
      houseList.push({ id: 4, title: "长宁·森林氧吧公寓", type: "三室二厅", price: 12000, area: 140, desc: "正对中山公园，负氧离子充沛，采光极佳。", images: ["https://upload-bbs.mihoyo.com/upload/2022/08/20/325961053/9699f5d362309fd55583a28317695bd6_5637139327811429041.png?x-oss-process=image/resize,s_600/quality,q_80/auto-orient,0/interlace,1/format,png", "https://upload-bbs.miyoushe.com/upload/2021/08/12/271993388/69b17391d210ad2e0a2b022f0ab9e725_7273023720114352673.jpg"], status: "approved", comments: [], landlordId: "landlord_2" });
      houseList.push({ id: 5, title: "普陀·高新智选LOFT", type: "一室一卫", price: 4500, area: 45, desc: "层高4.5米，时尚动感，适合SOHO办公居住。", images: ["https://picsum.photos/id/50/800/600", "https://picsum.photos/id/51/800/600", "https://picsum.photos/id/52/800/600"], status: "approved", comments: ["科技感强"], landlordId: "landlord_3" });
      
      // 后15个为独立单图案例
      houseList.push({ id: 6, title: "虹口·标准一居室", type: "一室一厅", price: 3800, area: 40, desc: "极简实用，生活配套齐全。", image: "https://picsum.photos/id/60/400/300", status: "approved", comments: [], landlordId: "landlord_1" });
      houseList.push({ id: 7, title: "杨浦·学生友好套房", type: "二室一厅", price: 5500, area: 65, desc: "邻近复旦大学，学区氛围浓厚。", image: "https://picsum.photos/id/70/400/300", status: "approved", comments: [], landlordId: "landlord_3" });
      houseList.push({ id: 8, title: "闵行·产业园白领房", type: "一室一厅", price: 3200, area: 50, desc: "出门即是公交枢纽，覆盖周边园区。", image: "https://picsum.photos/id/80/400/300", status: "approved", comments: [], landlordId: "landlord_1" });
      houseList.push({ id: 9, title: "宝山·乐活社区三房", type: "三室一厅", price: 6000, area: 110, desc: "南大版块，停车位充足，社区环境好。", image: "https://picsum.photos/id/90/400/300", status: "approved", comments: [], landlordId: "landlord_2" });
      houseList.push({ id: 10, title: "浦东·陆家嘴金融宅", type: "二室二厅", price: 18000, area: 95, desc: "步行至明珠塔，精英阶层首选。", image: "https://picsum.photos/id/100/400/300", status: "approved", comments: [], landlordId: "landlord_3" });
      houseList.push({ id: 11, title: "嘉定·汽车城宜居房", type: "一室一厅", price: 2800, area: 48, desc: "安亭新镇，宁静的德式风情小镇。", image: "https://picsum.photos/id/110/400/300", status: "approved", comments: [], landlordId: "landlord_1" });
      houseList.push({ id: 12, title: "松江·大学城悦寓", type: "三室二厅", price: 5200, area: 130, desc: "广富林遗址旁，风景如画。", image: "https://picsum.photos/id/120/400/300", status: "approved", comments: [], landlordId: "landlord_2" });
      houseList.push({ id: 13, title: "金山·海景度假房", type: "二室一厅", price: 2500, area: 88, desc: "海风轻抚，远离城市喧嚣。", image: "https://picsum.photos/id/130/400/300", status: "approved", comments: [], landlordId: "landlord_3" });
      houseList.push({ id: 14, title: "奉贤·南桥中心居", type: "一室一厅", price: 2200, area: 42, desc: "地铁5号线终点站旁，交通便利。", image: "https://picsum.photos/id/140/400/300", status: "approved", comments: [], landlordId: "landlord_1" });
      houseList.push({ id: 15, title: "青浦·奥莱轻奢馆", type: "四室两厅", price: 9000, area: 160, desc: "购物中心对面，极致方便。", image: "https://picsum.photos/id/150/400/300", status: "approved", comments: [], landlordId: "landlord_2" });
      houseList.push({ id: 16, title: "静安·苏河湾壹号", type: "一室一厅", price: 11000, area: 60, desc: "俯瞰苏河湾，时尚地标。", image: "https://picsum.photos/id/160/400/300", status: "approved", comments: [], landlordId: "landlord_3" });
      houseList.push({ id: 17, title: "崇明·生态岛别墅", type: "五室二厅", price: 13000, area: 300, desc: "独栋大户型，私家超大果园。", image: "https://picsum.photos/id/170/400/300", status: "approved", comments: [], landlordId: "landlord_1" });
      houseList.push({ id: 18, title: "徐汇·田林阳光平层", type: "二室一厅", price: 7800, area: 72, desc: "采光无死角，实木地板。", image: "https://picsum.photos/id/180/400/300", status: "approved", comments: [], landlordId: "landlord_2" });
      houseList.push({ id: 19, title: "长宁·武夷路文艺房", type: "一室一厅", price: 6500, area: 55, desc: "梧桐树下，满屋文学气息。", image: "https://picsum.photos/id/190/400/300", status: "approved", comments: [], landlordId: "landlord_3" });
      houseList.push({ id: 20, title: "虹口·北外滩展望阁", type: "三室两厅", price: 16000, area: 135, desc: "城市天际线映入眼帘。", image: "https://picsum.photos/id/200/400/300", status: "approved", comments: [], landlordId: "landlord_1" });

      wx.setStorageSync('houses', houseList);
    }
  }
});