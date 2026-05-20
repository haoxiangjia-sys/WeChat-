const app = getApp()
const BASE_URL = app.globalData.baseUrl

Page({
  data: {
    score: 0,
    total: 0,
    percentage: 0,
    message: '',
    nickname: ''
  },

  onLoad() {
    const score = app.globalData.score
    const total = app.globalData.total
    const percentage = total > 0 ? Math.round((score / total) * 100) : 0
    const nickname = wx.getStorageSync('nickname') || ''

    let message = '继续加油！'
    if (percentage === 100) message = '满分！你是天才！'
    else if (percentage >= 80) message = '非常棒！'
    else if (percentage >= 60) message = '还不错，还有提升空间！'

    this.setData({ score, total, percentage, message, nickname })
    this.submitScore()
  },

  submitScore() {
    const nickname = this.data.nickname
    if (!nickname) return

    wx.request({
      url: BASE_URL + '/scores',
      method: 'POST',
      header: { 'Content-Type': 'application/json' },
      data: {
        nickname: nickname,
        score: this.data.score,
        total: this.data.total
      },
      success() {
        console.log('成绩已提交')
      }
    })
  },

  playAgain() {
    wx.redirectTo({ url: '/pages/quiz/quiz' })
  },

  goHome() {
    wx.redirectTo({ url: '/pages/index/index' })
  },

  goToLeaderboard() {
    wx.navigateTo({ url: '/pages/leaderboard/leaderboard' })
  }
})
