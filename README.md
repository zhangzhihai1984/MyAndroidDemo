
## **状态栏相关**
- ### 全屏（不隐藏StatusBar）
`SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN`与`SYSTEM_UI_FLAG_FULLSCREEN`(全屏且无StatusBar)类似，但是它是在不隐藏StatusBar的情况下，将页面的显示范围延伸到StatusBar下面，当然页面顶部内容会被StatusBar遮挡，我们会使用fitsSystemWindows来规避。
一般来说，我们会将该Flag与`SYSTEM_UI_FLAG_LAYOUT_STABLE`一起使用：
```
window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
```
一般情况下，我们会同时将StatusBar设为透明，视觉上达到隐藏StatusBar的效果：
```xml
<item name="android:statusBarColor">@android:color/transparent</item>
```

- ### 隐藏NavigationBar
`SYSTEM_UI_FLAG_HIDE_NAVIGATION`会隐藏NavigationBar，但触摸屏幕后NavigationBar会出现，如果想让NavigationBar始终隐藏（通过屏幕底部上拉才可出现），需“或”上`SYSTEM_UI_FLAG_IMMERSIVE`：
```
window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE
```

- ### 动态设置StatusBarColor
styles中允许修改StatusBar颜色：
```xml
<item name="android:windowDrawsSystemBarBackgrounds">true</item>
```
设置StatusBar颜色：
```
window.statusBarColor = Color.TRANSPARENT
window.statusBarColor = ContextCompat.getColor(context(), R.color.custom_color)
```