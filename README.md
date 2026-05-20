#  动漫知识问答 - 微信小程序

一个基于微信小程序的动漫知识答题挑战，前端使用微信小程序原生框架，后端使用 Spring Boot + JPA + H2。

---

## 目录

- [项目结构一览](#项目结构一览)
- [知识点全景图](#知识点全景图)
- [后端详解](#后端详解)
  - [1. 项目入口 - Spring Boot 启动](#1-项目入口---spring-boot-启动)
  - [2. 数据模型 - JPA Entity](#2-数据模型---jpa-entity)
  - [3. 数据访问层 - Repository](#3-数据访问层---repository)
  - [4. 业务逻辑层 - Service](#4-业务逻辑层---service)
  - [5. 控制器层 - Controller](#5-控制器层---controller)
  - [6. 配置层 - CORS 跨域](#6-配置层---cors-跨域)
  - [7. 数据库初始化 - data.sql & application.yml](#7-数据库初始化---datasql--applicationyml)
- [前端详解](#前端详解)
  - [8. 小程序入口 - app.js / app.json](#8-小程序入口---appjs--appjson)
  - [9. 网络请求封装 - utils/api.js](#9-网络请求封装---utilsapijs)
  - [10. 首页](#10-首页)
  - [11. 答题页](#11-答题页)
  - [12. 结果页](#12-结果页)
  - [13. 排行榜页](#13-排行榜页)
- [数据如何流动](#数据如何流动)
- [快速开始](#快速开始)

---

## 项目结构一览

```
quiz-miniprogram/
├── backend/                          # 后端 - Spring Boot 项目
│   ├── pom.xml                       # Maven 依赖配置
│   ├── mvnw / mvnw.cmd               # Maven Wrapper（无需安装 Maven）
│   └── src/main/
│       ├── java/com/quiz/
│       │   ├── QuizApplication.java       # Spring Boot 启动类
│       │   ├── model/
│       │   │   ├── Question.java          # 题目实体（JPA → 数据库表映射）
│       │   │   └── Score.java             # 成绩实体
│       │   ├── repository/
│       │   │   ├── QuestionRepository.java # 题目数据访问（接口即实现）
│       │   │   └── ScoreRepository.java    # 成绩数据访问
│       │   ├── service/
│       │   │   ├── QuestionService.java    # 题目业务逻辑
│       │   │   └── ScoreService.java       # 成绩业务逻辑
│       │   ├── controller/
│       │   │   ├── QuestionController.java # 题目 REST API
│       │   │   └── ScoreController.java    # 成绩 REST API
│       │   └── config/
│       │       └── WebConfig.java          # 跨域配置（让前端能访问后端）
│       └── resources/
│           ├── application.yml             # 应用配置（数据库、端口等）
│           └── data.sql                    # 题库数据
├── miniprogram/                      # 前端 - 微信小程序
│   ├── app.js                        # 小程序入口（全局数据）
│   ├── app.json                      # 小程序配置（页面路由、窗口样式）
│   ├── app.wxss                      # 全局样式
│   ├── utils/api.js                  # API 请求封装
│   └── pages/
│       ├── index/                    # 首页（输入昵称）
│       ├── quiz/                     # 答题页（核心页面）
│       ├── result/                   # 结果页（展示成绩）
│       └── leaderboard/              # 排行榜页
└── promo-images/                     # 宣传图素材
```

---

## 知识点全景图

| 层次 | 涉及技术 | 在这个项目里的具体体现 |
|------|---------|----------------------|
| **后端框架** | Spring Boot 3.3 | 快速搭建 Java Web 服务，内置 Tomcat 运行在 8080 端口 |
| **数据持久化** | Spring Data JPA + Hibernate | 写接口就能操作数据库，不用写 SQL；自动建表 |
| **数据库** | H2 内存数据库 | 程序启动时在内存中创建，关闭就消失，适合开发测试 |
| **REST API** | Spring MVC | `@GetMapping` / `@PostMapping` 定义 HTTP 接口 |
| **跨域** | CORS | 允许小程序从任意域名请求后端 |
| **依赖注入** | Spring IoC | `@Service` / `@Repository` / 构造函数注入 |
| **前端框架** | 微信小程序原生 | Page()、数据绑定、事件处理、生命周期 |
| **网络请求** | wx.request | 小程序内置 API，发起 HTTP 请求到后端 |

---

## 后端详解

### 1. 项目入口 - Spring Boot 启动

```java
// backend/src/main/java/com/quiz/QuizApplication.java
@SpringBootApplication
public class QuizApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuizApplication.class, args);
    }
}
```

**知识点：**

- **`@SpringBootApplication`** — 这个注解是三个注解的组合：
  - `@Configuration` — 标记这是一个配置类
  - `@EnableAutoConfiguration` — Spring Boot 自动配置，会根据 pom.xml 里的依赖自动设置（比如检测到 `spring-boot-starter-web` 就自动启动 Tomcat）
  - `@ComponentScan` — 扫描当前包及子包中的所有组件（`@Service`、`@Controller`、`@Repository` 等），自动注册到 Spring 容器中

- **`SpringApplication.run()`** — 启动 Spring 容器。内部做了三件事：
  1. 创建 ApplicationContext（IoC 容器）
  2. 扫描并注册所有 Bean
  3. 启动内嵌的 Tomcat 服务器

---

### 2. 数据模型 - JPA Entity

```java
// backend/src/main/java/com/quiz/model/Question.java
@Entity                          // 标记这是一个 JPA 实体，对应数据库的一张表
@Table(name = "questions")       // 指定表名（不写则默认类名小写）
public class Question {

    @Id                           // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增
    private Long id;

    @Column(nullable = false, length = 500)  // 非空，最大长度500
    private String content;       // 题目内容

    @Column(nullable = false, length = 500)
    private String options;       // 选项 JSON，如 '["A. 娜美","B. 索隆"...]'

    @Column(nullable = false, length = 1)
    private String answer;        // 正确答案：A / B / C / D

    @Column(length = 50)
    private String category;      // 分类
}
```

**知识点：**

- **ORM（对象关系映射）** — JPA 把 Java 对象和数据库表自动关联。`Question` 类 ↔ `questions` 表，`content` 字段 ↔ `content` 列。你不用写 INSERT/UPDATE 语句，JPA 自动生成
- **`@Entity`** — 告诉 JPA：这个类对应一张表，请自动管理它
- **`@Id` + `@GeneratedValue`** — 标记主键并设置为自增。`IDENTITY` 表示由数据库自动生成
- **`@Column`** — 定义列的约束：是否可空、最大长度等。JPA 启动时会根据这些注解自动建表（`ddl-auto: update`）
- **字段类型映射**：`String` → `VARCHAR`，`Long` → `BIGINT`，`int` → `INTEGER`

```java
// backend/src/main/java/com/quiz/model/Score.java
@Entity
@Table(name = "scores")
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nickname;           // 玩家昵称

    @Column(nullable = false)
    private int score;                 // 得分

    @Column(nullable = false)
    private int total;                 // 总题数

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 提交时间
}
```

**知识点：**

- **`name = "created_at"`** — Java 用驼峰命名 `createdAt`，但数据库习惯用下划线 `created_at`。`@Column(name = ...)` 做映射
- **`LocalDateTime.now()`** — Java 8 的日期时间类，比旧的 `Date` 更好用。JPA 自动映射到数据库的 `TIMESTAMP` 类型

---

### 3. 数据访问层 - Repository

```java
// backend/src/main/java/com/quiz/repository/QuestionRepository.java
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query(value = "SELECT * FROM questions ORDER BY RANDOM() LIMIT :count",
           nativeQuery = true)
    List<Question> findRandomQuestions(int count);
}
```

**知识点：**

- **接口即实现** — 你只需要写一个 `interface`，继承 `JpaRepository<实体类, 主键类型>`，Spring Data JPA 会在运行时自动生成实现类。不用写一行实现代码，CRUD 操作就已经可用了
- **`JpaRepository<Question, Long>`** — 泛型参数：第一个是实体类，第二个是主键类型。继承后自动拥有 `save()`、`findAll()`、`findById()`、`deleteById()` 等方法
- **自定义查询** — `@Query` 注解写原生 SQL。`nativeQuery = true` 表示用数据库原生 SQL，而非 JPQL
- **`RANDOM()`** — H2 数据库的随机排序函数。MySQL 用 `RAND()`，不同数据库函数名不同
- **`:count`** — 命名参数，对应方法参数 `int count`

```java
// backend/src/main/java/com/quiz/repository/ScoreRepository.java
public interface ScoreRepository extends JpaRepository<Score, Long> {

    @Query(value = "SELECT * FROM scores ORDER BY score DESC, created_at ASC LIMIT :limit",
           nativeQuery = true)
    List<Score> findTopScores(int limit);
}
```

**知识点：**

- **`score DESC, created_at ASC`** — 按分数降序（高的在前），同分则按提交时间升序（先提交的在前），这是排行榜的标准排序逻辑

---

### 4. 业务逻辑层 - Service

```java
// backend/src/main/java/com/quiz/service/QuestionService.java
@Service  // 标记为 Service 组件，Spring 会自动扫描并管理
public class QuestionService {

    private final QuestionRepository questionRepository;

    // 构造函数注入（推荐方式，无需 @Autowired）
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<Question> getRandomQuestions(int count) {
        return questionRepository.findRandomQuestions(count);
    }
}
```

**知识点：**

- **`@Service`** — Spring 的组件注解。被扫描后会创建单例 Bean 放入 IoC 容器
- **构造函数注入** — Spring 发现构造函数需要 `QuestionRepository`，会自动从容器中找到它并传进来。相比 `@Autowired` 字段注入的好处：不可变性（`final`）、更易于单元测试
- **分层职责** — Controller 只处理 HTTP 请求/响应，Service 处理业务逻辑，Repository 只操作数据库。各层职责清晰，修改任何一层不影响其他层

```java
// backend/src/main/java/com/quiz/service/ScoreService.java
@Service
public class ScoreService {

    private final ScoreRepository scoreRepository;

    public ScoreService(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    public Score submitScore(String nickname, int score, int total) {
        Score s = new Score(nickname, score, total);
        return scoreRepository.save(s);    // JPA 的 save 方法，自动 INSERT
    }

    public List<Score> getLeaderboard(int limit) {
        return scoreRepository.findTopScores(limit);
    }
}
```

---

### 5. 控制器层 - Controller

```java
// backend/src/main/java/com/quiz/controller/QuestionController.java
@RestController                     // = @Controller + @ResponseBody
@RequestMapping("/api")             // 这个类下所有接口路径都以 /api 开头
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/questions")       // GET 请求：/api/questions
    public List<Question> getQuestions(
        @RequestParam(defaultValue = "10") int count  // ?count=10，默认10
    ) {
        return questionService.getRandomQuestions(count);
    }
}
```

**知识点：**

- **`@RestController`** — 告诉 Spring：这个类的每个方法返回值都直接写入 HTTP 响应体（JSON 格式）
- **`@RequestMapping("/api")`** — 类级别的路径前缀，所有方法路径都拼在 `/api` 后面
- **`@GetMapping("/questions")`** — 只处理 GET 请求，完整路径为 `/api/questions`
- **`@RequestParam(defaultValue = "10")`** — 从 URL 查询参数中取值。`/api/questions?count=5` → count=5；不传则默认 10
- **返回 `List<Question>`** — Spring MVC 自动将 Java 对象序列化为 JSON 数组返回。原理是 Jackson 库在背后工作

```java
// backend/src/main/java/com/quiz/controller/ScoreController.java
@RestController
@RequestMapping("/api")
public class ScoreController {

    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @PostMapping("/scores")        // POST 请求：/api/scores
    public Score submitScore(@RequestBody Map<String, Object> body) {
        // @RequestBody 把请求体 JSON 解析为 Java Map
        String nickname = (String) body.get("nickname");
        int score = (int) body.get("score");
        int total = (int) body.get("total");
        return scoreService.submitScore(nickname, score, total);
    }

    @GetMapping("/leaderboard")
    public List<Score> getLeaderboard(
        @RequestParam(defaultValue = "20") int limit
    ) {
        return scoreService.getLeaderboard(limit);
    }
}
```

**知识点：**

- **`@PostMapping`** — 处理 POST 请求，通常用于提交数据（创建成绩记录）
- **`@RequestBody`** — 把 HTTP 请求体的 JSON 数据自动解析成 Java 对象。这里用了 `Map<String, Object>` 接收任意键值对
- **RESTful 风格**：GET 用来获取资源（/questions），POST 用来创建资源（/scores）。同一个路径 `/api/scores`，GET 和 POST 含义完全不同

---

### 6. 配置层 - CORS 跨域

```java
// backend/src/main/java/com/quiz/config/WebConfig.java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")        // 匹配所有 /api/ 开头的路径
                .allowedOrigins("*")          // 允许任何来源访问
                .allowedMethods("GET", "POST"); // 允许的 HTTP 方法
    }
}
```

**知识点：**

- **CORS（跨域资源共享）** — 浏览器的安全机制，默认禁止网页请求不同域名/端口的服务器。小程序运行在微信客户端，也受此限制
- **`@Configuration`** — 标记这是一个配置类，Spring Boot 启动时会加载
- **`WebMvcConfigurer`** — Spring MVC 的配置接口，实现它的方法可以定制 Web 行为
- **`allowedOrigins("*")`** — 开发阶段允许所有来源。**上线后应该改为具体的域名**

---

### 7. 数据库初始化 - data.sql & application.yml

```yaml
# backend/src/main/resources/application.yml
server:
  port: 8080                        # 服务端口

spring:
  datasource:
    url: jdbc:h2:mem:quizdb        # H2 内存数据库，进程结束数据就消失
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update              # 自动建表，实体类改了表结构也跟着变
    defer-datasource-initialization: true  # 等 Hibernate 建表后再执行 data.sql
  sql:
    init:
      mode: always                  # 每次启动都执行 data.sql
      encoding: UTF-8               # 用 UTF-8 读取 SQL 文件（否则中文乱码）
```

**知识点：**

- **H2 内存数据库** — 数据存在内存中，不写磁盘。程序关闭数据全丢，适合开发测试。要换 MySQL 只需改 `url` 一行的配置
- **`ddl-auto: update`** — JPA 启动时会检查实体类，如果表不存在就创建；如果实体类新增了字段，表也会新增列。已有数据不会被删除
- **`defer-datasource-initialization: true`** — 关键配置！让 Hibernate 先建表，然后才执行 `data.sql`。否则 `data.sql` 执行时表还不存在
- **`encoding: UTF-8`** — Windows 系统默认不是 UTF-8，不设置这个 SQL 文件里的中文会变成乱码

```sql
-- backend/src/main/resources/data.sql（节选）
DELETE FROM questions;
INSERT INTO questions (content, options, answer, category) VALUES
('《海贼王》中路飞的第一个伙伴是谁？',
 '["A. 娜美","B. 索隆","C. 乌索普","D. 山治"]', 'B', '海贼王'),
...
```

**知识点：**

- **`DELETE FROM questions`** — 每次启动先清空旧数据再插入，避免 `mode: always` 导致数据重复
- **选项存储为 JSON 字符串** — 数据库只有 4 列（content, options, answer, category），options 存成 JSON 字符串，前端拿到后 `JSON.parse()` 解析成数组。这比建一张单独的选项表更简单

---

## 前端详解

### 8. 小程序入口 - app.js / app.json

```javascript
// miniprogram/app.js
App({
  globalData: {
    baseUrl: 'http://127.0.0.1:8080/api',  // 后端地址
    questions: [],
    score: 0,
    total: 0,
    answers: []
  }
})
```

**知识点：**

- **`App()`** — 小程序的应用级入口，全局只调用一次。整个小程序只有一个 App 实例
- **`globalData`** — 全局共享数据，任何页面通过 `getApp().globalData` 访问。这里用来在答题页和结果页之间传递分数
- **数据流向**：答题页设置 `score` 和 `total` → 跳转结果页 → 结果页从 `globalData` 读取

```json
// miniprogram/app.json
{
  "pages": [
    "pages/index/index",            // 第 1 项 = 首页
    "pages/quiz/quiz",
    "pages/result/result",
    "pages/leaderboard/leaderboard"
  ],
  "window": {
    "navigationBarBackgroundColor": "#1aad19",
    "navigationBarTitleText": "动漫知识问答",
    "navigationBarTextStyle": "white"
  }
}
```

**知识点：**

- **`pages` 数组** — 声明所有页面路径。**第一项就是小程序启动时显示的页面**
- 新建页面后必须在 `pages` 中注册，否则小程序找不到
- **`window`** — 全局导航栏样式：微信绿背景、白字、标题"动漫知识问答"

---

### 9. 网络请求封装 - utils/api.js

```javascript
// miniprogram/utils/api.js
const BASE_URL = 'http://127.0.0.1:8080/api'

function request(path, method, data) {
  return new Promise(function (resolve, reject) {
    wx.request({
      url: BASE_URL + path,               // 拼接完整 URL
      method: method,                     // GET / POST
      data: data,                         // 请求体（POST 时用）
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
    return request('/scores', 'POST', { nickname, score, total })
  },
  getLeaderboard: function (limit) {
    return request('/leaderboard?limit=' + (limit || 20), 'GET')
  }
}
```

**知识点：**

- **`Promise`** — JavaScript 异步编程的核心概念。`request()` 返回 Promise，调用方用 `.then()` / `await` 获取结果。把回调式的 `wx.request` 包装成 Promise，代码更清晰
- **`module.exports`** — Node.js 的模块导出语法，其他文件通过 `require('./utils/api')` 引入
- **`wx.request`** — 微信小程序发起网络请求的唯一方式。不能直接用 `fetch` 或 `axios`，必须用这个
- **封装的好处**：所有页面的网络请求代码复用这一个文件，改 URL 只改一处

---

### 10. 首页

```html
<!-- miniprogram/pages/index/index.wxml -->
<view class="container">
  <view class="header">
    <text class="title">动漫知识问答</text>
    <text class="subtitle">测测你是不是真正的二次元</text>
  </view>

  <view class="card">
    <text class="label">你的昵称</text>
    <input class="input"
           placeholder="请输入昵称"
           value="{{nickname}}"
           bindinput="onNicknameInput"
           maxlength="20"/>
  </view>

  <button class="start-btn" bindtap="startQuiz">开始答题</button>

  <view class="leaderboard-entry" bindtap="goToLeaderboard">
    <text class="entry-text">查看排行榜 →</text>
  </view>
</view>
```

**知识点：**

- **`{{nickname}}`** — 数据绑定。双花括号里的变量来自 `Page.data`。data 变了，视图自动更新
- **`bindinput="onNicknameInput"`** — 事件绑定。输入框内容变化时调用 JS 中的 `onNicknameInput` 方法
- **`bindtap="startQuiz"`** — 点击事件。点击按钮触发 `startQuiz` 方法
- **`wx:if` / `wx:for`** — 条件渲染和列表渲染，后面答题页会用到

```javascript
// miniprogram/pages/index/index.js
Page({
  data: { nickname: '' },             // 页面数据，视图中的 {{nickname}} 绑定到这里

  onLoad() {
    const saved = wx.getStorageSync('nickname')  // 读取本地缓存
    if (saved) this.setData({ nickname: saved }) // 有缓存就自动填入
  },

  onNicknameInput(e) {
    this.setData({ nickname: e.detail.value })   // 输入框的值同步到 data
  },

  startQuiz() {
    const nickname = this.data.nickname.trim()
    if (!nickname) {
      wx.showToast({ title: '请输入昵称', icon: 'none' })
      return
    }
    wx.setStorageSync('nickname', nickname)      // 存到本地
    wx.navigateTo({ url: '/pages/quiz/quiz' })   // 跳转答题页
  },

  goToLeaderboard() {
    wx.navigateTo({ url: '/pages/leaderboard/leaderboard' })
  }
})
```

**知识点：**

- **`Page()`** — 定义页面。每个页面一个文件，调用 `Page({...})` 注册
- **`data`** — 页面的响应式数据。**修改 data 必须用 `setData()`**，直接赋值不会触发视图更新
- **`onLoad()`** — 页面生命周期，页面加载时自动调用一次。适合做初始化操作
- **`wx.getStorageSync()`** — 同步读取本地缓存（类似 localStorage）。小程序数据存在微信客户端里，删除小程序缓存会丢失
- **`wx.navigateTo()`** — 页面跳转，会保留当前页面。跳转后可以点左上角返回
- **`e.detail.value`** — 从事件对象中取输入框的当前值

---

### 11. 答题页

答题页是项目最复杂的页面，核心逻辑都在这里。

```javascript
// miniprogram/pages/quiz/quiz.js（关键部分）

data: {
  questions: [],        // 所有题目
  currentIndex: 0,      // 当前题号（从0开始）
  currentQuestion: null,// 当前题目对象
  options: [],          // 当前题目的选项数组
  selectedOption: '',   // 用户选择的选项
  score: 0,             // 得分
  total: 0,             // 总题数
  timer: 15,            // 倒计时秒数
  loading: true,        // 是否加载中
  answered: false       // 当前题是否已作答
},

fetchQuestions() {
  wx.request({
    url: BASE_URL + '/questions?count=10',   // 请求 10 道题
    success(res) {
      this.setData({ questions: res.data, total: res.data.length })
      this.loadQuestion(0)                   // 加载第一题
    }
  })
},

loadQuestion(index) {
  const q = this.data.questions[index]
  const options = JSON.parse(q.options)      // 把 JSON 字符串解析为数组
  this.setData({
    currentIndex: index,
    currentQuestion: q,
    options,
    selectedOption: '',
    timer: 15,
    answered: false
  })
  this.startTimer()                          // 开始倒计时
},

startTimer() {
  this._timer = setInterval(() => {          // 每秒执行一次
    const timer = this.data.timer - 1
    if (timer <= 0) {
      clearInterval(this._timer)             // 时间到，停止计时器
      this.handleTimeout()
    }
    this.setData({ timer })
  }, 1000)
},

selectOption(e) {
  if (this.data.answered) return             // 已答过，忽略

  const option = e.currentTarget.dataset.option  // 取 data-option 的值
  clearInterval(this._timer)                 // 停止计时

  const isCorrect = option === this.data.currentQuestion.answer
  this.setData({
    selectedOption: option,
    answered: true,
    score: this.data.score + (isCorrect ? 1 : 0)  // 答对 +1 分
  })
},

nextQuestion() {
  if (this.data.currentIndex + 1 >= this.data.total) {
    this.finishQuiz()                        // 所有题目答完
  } else {
    this.loadQuestion(this.data.currentIndex + 1)  // 加载下一题
  }
}
```

**知识点：**

- **`setInterval` / `clearInterval`** — JavaScript 定时器。`setInterval(fn, 1000)` 每隔 1000ms 执行 fn。用 `clearInterval` 停止
- **`e.currentTarget.dataset.option`** — 从 WXML 的 `data-option="{{item[0]}}"` 中取值。这是小程序传参的方式
- **`JSON.parse(q.options)`** — 把数据库中存的 JSON 字符串 `'["A. xxx","B. xxx"]'` 转成真正的数组 `["A. xxx", "B. xxx"]`
- **`this` 指向** — 小程序 Page 方法中的 `this` 指向当前页面实例。在定时器回调中如果不用箭头函数，`this` 会丢失，需要用 `const that = this` 保存引用
- **条件渲染** — 答对高亮绿色、答错高亮红色、超时灰色，通过 CSS class 切换实现

```html
<!-- 关键 WXML：选项列表 -->
<view class="option {{answered ? (currentQuestion.answer === 'A' ? 'correct' : (selectedOption === 'A' ? 'wrong' : '')) : ''}}"
      wx:for="{{options}}"             <!-- 遍历选项数组 -->
      wx:key="*this"                   <!-- 唯一标识（数组元素本身） -->
      data-option="{{item[0]}}"        <!-- 传首字母（A/B/C/D）给 JS -->
      bindtap="selectOption">
  <text class="option-letter">{{item[0]}}</text>      <!-- 只显示首字母 A -->
  <text class="option-text">{{item}}</text>            <!-- 显示 "A. 娜美" -->
</view>
```

**知识点：**

- **`wx:for="{{options}}"`** — 遍历数组，自动生成多个 `<view>`。当前元素用 `item` 引用
- **`wx:key="*this"`** — 每个循环项的唯一标识，帮助框架高效更新。`*this` 表示用元素本身作为 key
- **`item[0]`** — 取选项字符串的第一个字符，即 A / B / C / D
- **`{{ answered ? ... : ... }}`** — WXML 中的三元表达式，根据不同状态切换样式类名

---

### 12. 结果页

```javascript
// miniprogram/pages/result/result.js
onLoad() {
  const score = app.globalData.score        // 从全局取答题结果
  const total = app.globalData.total
  const percentage = total > 0 ? Math.round((score / total) * 100) : 0

  let message = '继续加油！'
  if (percentage === 100) message = '满分！你是天才！'
  else if (percentage >= 80) message = '非常棒！'
  else if (percentage >= 60) message = '还不错，还有提升空间！'

  this.setData({ score, total, percentage, message })
  this.submitScore()                         // 自动提交成绩到后端
}
```

**知识点：**

- **跨页面数据传递** — 结果页的分数是从 `app.globalData` 读取的。答题页在 `finishQuiz()` 时写入，然后跳转结果页
- **另一种传参方式**：`wx.navigateTo({ url: '/pages/result/result?score=8&total=10' })`，但 URL 长度有限制。大数据量用 `globalData`

---

### 13. 排行榜页

```javascript
// miniprogram/pages/leaderboard/leaderboard.js
fetchLeaderboard() {
  wx.request({
    url: BASE_URL + '/leaderboard?limit=20',
    success(res) {
      this.setData({ list: res.data })        // 后端返回的 JSON 数组直接放入 data
      wx.stopPullDownRefresh()                // 停止下拉刷新动画
    }
  })
},

onPullDownRefresh() {                         // 下拉刷新回调
  this.fetchLeaderboard()
}
```

**知识点：**

- **`wx.stopPullDownRefresh()`** — 收到数据后停止下拉刷新的 loading 动画
- **`onPullDownRefresh()`** — 页面生命周期，用户下拉时触发。需要在页面 json 中开启 `"enablePullDownRefresh": true`

---

## 数据如何流动

```
用户输入昵称 → 点击"开始答题"
    │
    ▼
[首页] wx.navigateTo → [答题页]
    │ wx.request GET /api/questions?count=10
    ▼
[后端 QuestionController] → [QuestionService] → [QuestionRepository]
    │                                                    │
    │                                         @Query("SELECT ... RANDOM()")
    │                                                    │
    ▼                                                    ▼
[答题页 接收 JSON] ←────────────────────── [H2 数据库 随机返回10题]
    │
    │ 用户逐题作答（15秒倒计时、得分累计）
    │
    ▼
[答题页 finishQuiz()] → globalData.score = 8
    │ wx.redirectTo → [结果页]
    ▼
[结果页] 显示成绩 + wx.request POST /api/scores
    │
    ▼
[后端 ScoreController] → [ScoreService] → [ScoreRepository.save()]
    │
    ▼
[排行榜页] wx.request GET /api/leaderboard → 展示 TOP 20
```

---

## 快速开始

### 启动后端

```bash
cd backend

# Windows
mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

看到 `Started QuizApplication` 即启动成功，监听 `http://localhost:8080`。

### 打开小程序

1. 下载 [微信开发者工具](https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html)
2. 导入项目 → 选择 `miniprogram` 目录
3. AppID 填写自己的（或选测试号）
4. 扫码或模拟器预览

### 手机预览

修改 `app.js` 和 `utils/api.js` 中的 `127.0.0.1` 为电脑局域网 IP，确保同一 WiFi。

```javascript
baseUrl: 'http://10.137.47.11:8080/api'  // 改成你的 IP
```

---

## 题库

| 分类 | 题目数 |
|------|--------|
| 海贼王 | 1 |
| 火影忍者 | 1 |
| 进击的巨人 | 1 |
| 鬼灭之刃 | 1 |
| 名侦探柯南 | 1 |
| 咒术回战 | 1 |
| 钢之炼金术师 | 1 |
| EVA | 1 |
| 灌篮高手 | 1 |
| 龙珠 | 1 |
| 死神 | 1 |
| 一拳超人 | 1 |
| 吉卜力 | 2 |
| 间谍过家家 | 1 |
| 叛逆的鲁路修 | 1 |
| 犬夜叉 | 1 |
| Re:Zero | 1 |
| 东京喰种 | 1 |
| 银魂 | 1 |
| 动漫电影 | 1 |
| **合计** | **21 题** |

新增题目只需在 `data.sql` 中添加一行 INSERT，重启后端即可。

---

## 许可证

MIT
