const app = getApp()
const BASE_URL = app.globalData.baseUrl

Page({
  data: {
    list: [],
    loading: true
  },

  onLoad() {
    this.fetchLeaderboard()
  },

  onPullDownRefresh() {
    this.fetchLeaderboard()
  },

  fetchLeaderboard() {
    const that = this
    wx.request({
      url: BASE_URL + '/leaderboard?limit=20',
      method: 'GET',
      success(res) {
        that.setData({
          list: res.data,
          loading: false
        })
        wx.stopPullDownRefresh()
      },
      fail() {
        that.setData({ loading: false })
        wx.stopPullDownRefresh()
        wx.showToast({ title: '网络请求失败', icon: 'none' })
      }
    })
  }
})
