# 动漫知识问答 - 微信小程序

一个基于微信小程序的动漫知识答题挑战，前端使用微信小程序原生框架，后端使用 Spring Boot + JPA + H2。

## 功能

-  **随机出题** — 从题库中随机抽取 10 道动漫知识题，每题限时 15 秒
-  **实时得分** — 答对 +1 分，超时不得分，选项高亮正确/错误
-  **排行榜** — 提交成绩查看排名，支持按分数降序排列
-  **昵称记忆** — 本地缓存昵称，下次无需重新输入

## 技术栈

| 层次 | 技术 |
|------|------|
| 小程序前端 | 微信小程序原生框架（WXML + WXSS + JS） |
| 后端框架 | Spring Boot 3.3.5 + Maven |
| 数据层 | Spring Data JPA + H2 内存数据库 |
| 构建 | Maven Wrapper（无需安装 Maven） |

## 项目结构

```
├── miniprogram/          # 微信小程序前端
│   ├── app.js/json/wxss  # 应用入口与全局配置
│   ├── pages/
│   │   ├── index/        # 首页（输入昵称）
│   │   ├── quiz/         # 答题页
│   │   ├── result/       # 结果页
│   │   └── leaderboard/  # 排行榜
│   └── utils/api.js      # API 请求封装
├── backend/              # Spring Boot 后端
│   └── src/main/
│       ├── java/com/quiz/
│       │   ├── controller/  # REST 接口
│       │   ├── service/     # 业务逻辑
│       │   ├── model/       # JPA 实体
│       │   ├── repository/  # 数据访问层
│       │   └── config/      # CORS 配置
│       └── resources/
│           ├── application.yml  # 应用配置
│           └── data.sql         # 题库数据（21 道动漫题）
└── promo-images/         # 宣传图素材
```

## 快速开始

### 1. 启动后端

```bash
cd backend

# Windows
mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

服务启动后运行在 `http://localhost:8080`，题库从 `data.sql` 自动导入。

### 2. 打开小程序

1. 下载 [微信开发者工具](https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html)
2. 导入项目，选择 `miniprogram` 目录
3. 填写自己的 AppID（或使用测试号）
4. 在模拟器中预览

### 3. 配置 API 地址

修改 `miniprogram/app.js` 中的 `baseUrl`：

```js
baseUrl: 'http://localhost:8080/api'
```

手机预览时改为电脑局域网 IP（如 `http://192.168.x.x:8080/api`）。

## API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/questions?count=10` | 随机获取 N 道题 |
| POST | `/api/scores` | 提交成绩 `{nickname, score, total}` |
| GET | `/api/leaderboard?limit=20` | 获取排行榜 |

## 题库

当前包含 21 道动漫知识题，涵盖：海贼王、火影忍者、进击的巨人、鬼灭之刃、名侦探柯南、咒术回战、EVA、灌篮高手、龙珠、死神、一拳超人、吉卜力、间谍过家家等。

## 许可证

MIT
