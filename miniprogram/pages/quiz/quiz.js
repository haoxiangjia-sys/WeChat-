const app = getApp()
const BASE_URL = app.globalData.baseUrl

Page({
  data: {
    questions: [],
    currentIndex: 0,
    currentQuestion: null,
    options: [],
    selectedOption: '',
    score: 0,
    total: 0,
    timer: 15,
    timerRunning: false,
    loading: true,
    progress: 0,
    answered: false,
    errorMsg: ''
  },

  _timer: null,

  onLoad() {
    this.fetchQuestions()
  },

  onUnload() {
    if (this._timer) clearInterval(this._timer)
  },

  fetchQuestions() {
    const that = this
    wx.request({
      url: BASE_URL + '/questions?count=10',
      method: 'GET',
      timeout: 10000,
      success(res) {
        const questions = res.data
        if (questions && questions.length > 0) {
          that.setData({
            questions,
            total: questions.length,
            loading: false,
            errorMsg: ''
          })
          that.loadQuestion(0)
        } else {
          that.setData({
            loading: false,
            errorMsg: '后端返回数据为空，请检查 data.sql 是否加载'
          })
        }
      },
      fail(err) {
        console.error('Request failed:', JSON.stringify(err))
        that.setData({
          loading: false,
          errorMsg: '请求失败: ' + (err.errMsg || '未知错误') + ' | URL: ' + BASE_URL + '/questions?count=10'
        })
      }
    })
  },

  loadQuestion(index) {
    if (index >= this.data.total) {
      this.finishQuiz()
      return
    }

    const q = this.data.questions[index]
    const options = JSON.parse(q.options)
    this.setData({
      currentIndex: index,
      currentQuestion: q,
      options,
      selectedOption: '',
      timer: 15,
      timerRunning: true,
      progress: Math.round((index / this.data.total) * 100),
      answered: false
    })
    this.startTimer()
  },

  startTimer() {
    if (this._timer) clearInterval(this._timer)
    const that = this
    this._timer = setInterval(function () {
      const timer = that.data.timer - 1
      if (timer <= 0) {
        clearInterval(that._timer)
        that.setData({ timer: 0 })
        that.handleTimeout()
      } else {
        that.setData({ timer })
      }
    }, 1000)
  },

  selectOption(e) {
    if (this.data.answered) return

    const option = e.currentTarget.dataset.option
    clearInterval(this._timer)

    const isCorrect = option === this.data.currentQuestion.answer
    const newScore = this.data.score + (isCorrect ? 1 : 0)

    this.setData({
      selectedOption: option,
      answered: true,
      score: newScore,
      timerRunning: false
    })
  },

  handleTimeout() {
    this.setData({
      answered: true,
      timerRunning: false
    })
  },

  nextQuestion() {
    this.loadQuestion(this.data.currentIndex + 1)
  },

  finishQuiz() {
    app.globalData.score = this.data.score
    app.globalData.total = this.data.total
    wx.redirectTo({ url: '/pages/result/result' })
  },

  goBack() {
    wx.navigateBack()
  }
})
