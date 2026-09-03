# TV Browser - GitHub 自动构建 APK

> 无需安装 Android Studio，用 GitHub 免费服务器自动编译 APK！

## 原理
GitHub 提供免费的云服务器（GitHub Actions），我们上传代码后，服务器会自动安装 Android SDK、编译源码、生成 APK，你直接下载即可。

---

## 第一步：注册 GitHub 账号
1. 打开 https://github.com
2. 点击右上角 **Sign up**
3. 用邮箱注册（免费，不需要绑卡）

---

## 第二步：创建仓库
1. 登录后点击右上角 **+** → **New repository**
2. 仓库名称填：`tv-browser`
3. 选择 **Public**（公开）
4. 勾选 **Add a README file**
5. 点击底部 **Create repository**

---

## 第三步：上传项目文件

### 方法 A：网页直接上传（最简单，推荐）
1. 进入你刚创建的 `tv-browser` 仓库
2. 点击 **Add file** → **Upload files**
3. 把本项目的所有文件和文件夹拖拽到网页上传框
4. 等待上传完成，点击 **Commit changes**

> 注意：需要保持文件夹结构，`.github/workflows/build.yml` 必须放在这个路径！

### 方法 B：使用 Git（如果你熟悉）
```bash
git clone https://github.com/你的用户名/tv-browser.git
cd tv-browser
# 把本项目所有文件复制进来
git add .
git commit -m "init"
git push origin main
```

---

## 第四步：触发自动构建
上传完成后，GitHub 会自动开始构建：

1. 点击仓库顶部 **Actions** 标签
2. 你会看到 **Build TV Browser APK** 工作流正在运行（黄色圆圈）
3. 等待约 **3-5 分钟**（绿色勾号 = 成功，红色叉 = 失败）

---

## 第五步：下载 APK
构建成功后：

1. 点击最新的成功构建记录
2. 页面下方找到 **Artifacts** 区域
3. 点击 **TV-Browser-APK** 下载 zip 文件
4. 解压后得到 `app-debug.apk`

---

## 第六步：安装到电视盒子

### 方式 1：U 盘安装
1. APK 拷贝到 U 盘
2. U 盘插入电视盒子
3. 用文件管理器找到 APK 安装
4. 如提示"未知来源"，去设置 → 安全 → 允许未知来源

### 方式 2：ADB 无线安装
```bash
# 1. 电视盒子开启开发者模式 + ADB 调试
# 2. 查看盒子 IP（设置 → 网络）
adb connect 192.168.x.x:5555
adb install app-debug.apk
```

---

## 遥控器操作

| 按键 | 功能 |
|------|------|
| ⬆️ 上键 | 显示顶部地址栏 |
| ⬇️ 下键 | 显示底部导航栏 |
| ⬅️➡️ 左右键 | 切换按钮焦点 / 网页滚动 |
| 确认/OK 键 | 点击当前焦点按钮 |
| 返回键 | 隐藏控制栏 → 后退 → 退出确认 |
| 菜单键 | 显示/隐藏导航栏 |

---

## 常见问题

**Q: Actions 构建失败了怎么办？**  
A: 点击失败的构建记录 → 查看日志，通常是文件路径不对。确保 `.github/workflows/build.yml` 在正确位置。

**Q: 可以修改默认首页吗？**  
A: 可以！修改 `app/src/main/java/com/tv/browser/MainActivity.java` 中的 `webView.loadUrl("https://rrys.lv/");`，然后重新 push，Actions 会自动重新构建。

**Q: 需要付费吗？**  
A: 完全免费。GitHub Actions 对公开仓库无限免费使用。

**Q: 构建好的 APK 能保存多久？**  
A: 默认 30 天，建议下载后保存到本地。

---

## 项目文件结构
```
tv-browser/
├── .github/
│   └── workflows/
│       └── build.yml          ← 自动构建配置（核心）
├── build.gradle
├── settings.gradle
├── gradle.properties
└── app/
    ├── build.gradle
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/tv/browser/MainActivity.java
        └── res/
            ├── layout/activity_main.xml
            ├── drawable/
            ├── values/
            └── xml/
```
