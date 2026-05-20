const BASE_URL = 'http://10.137.47.11:8080/api'

function request(path, method, data) {
  return new Promise(function (resolve, reject) {
    wx.request({
      url: BASE_URL + path,
      method: method,
      data: data,
      header: data ? { 'Content-Type': 'application/json' } : {},
      success: function (res) { resolve(res.data) },
      fail: function (err) { reject(err) }
    })
  })
}

module.exports = {
  getQuestions: function (count) {
    return request('/questions?count=' + (count || 10), 'GET')
  },
  submitScore: function (nickname, score, total) {
    return request('/scores', 'POST', { nickname: nickname, score: score, total: total })
  },
  getLeaderboard: function (limit) {
    return request('/leaderboard?limit=' + (limit || 20), 'GET')
  }
}
