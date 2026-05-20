Page({
  data: {
    nickname: ''
  },

  onLoad() {
    const saved = wx.getStorageSync('nickname')
    if (saved) {
      this.setData({ nickname: saved })
    }
  },

  onNicknameInput(e) {
    this.setData({ nickname: e.detail.value })
  },

  startQuiz() {
    const nickname = this.data.nickname.trim()
    if (!nickname) {
      wx.showToast({ title: '请输入昵称', icon: 'none' })
      return
    }
    wx.setStorageSync('nickname', nickname)
    wx.navigateTo({ url: '/pages/quiz/quiz' })
  },

  goToLeaderboard() {
    wx.navigateTo({ url: '/pages/leaderboard/leaderboard' })
  }
})
