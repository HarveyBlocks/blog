# WebView

不跳转浏览器, 显示网络页面, 原理是内置了Chromium作为内核

申请权限

```xml
<!--level: normal-->
<uses-permission android:name="android.permission.INTERNET" />
```

编写布局

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

    <LinearLayout
            android:orientation="horizontal"
            android:layout_width="match_parent"
            android:layout_height="wrap_content">

        <EditText
                android:id="@+id/urlEdit"
                android:layout_width="0dp"
                android:layout_weight="1"
                android:layout_height="wrap_content"
                android:hint="please input url..."
                android:inputType="textUri" />

        <Button
                android:id="@+id/loadUrl"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="go" />
    </LinearLayout>

    <WebView
            android:id="@+id/webView"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />
</LinearLayout>
```

代码逻辑

```kotlin
binding.run {
    // 有XSS漏洞, 不建议
    webView.settings.javaScriptEnabled = true
    webView.webViewClient = WebViewClient()
    loadUrl.setOnClickListener {
        val urlText = urlEdit.text.toString()
        webView.loadUrl(urlText)
    }
}
```

一个大约摸的浏览器就OK了

