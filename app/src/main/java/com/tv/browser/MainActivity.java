package com.tv.browser;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    private WebView webView;
    private FrameLayout fullscreenContainer;
    private LinearLayout addressBar;
    private LinearLayout navBar;
    private LinearLayout navContainer;
    private EditText urlInput;
    private Button btnGo;
    private Button btnRefresh;

    private View[] navButtons;
    private int focusedNavIndex = -1;
    private boolean isAddressBarVisible = false;
    private boolean isNavBarVisible = false;
    private View customView;
    private WebChromeClient.CustomViewCallback customViewCallback;

    private final String[][] PRESET_SITES = {
        {"\uD83C\uDFAC 人人影视", "https://rrys.lv/"},
        {"\uD83D\uDD0D 百度", "https://www.baidu.com"},
        {"\uD83D\uDCFA Bilibili", "https://www.bilibili.com"},
        {"\u25B6\uFE0F YouTube", "https://www.youtube.com"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        initViews();
        setupWebView();
        setupNavBar();
        setupAddressBar();

        webView.loadUrl("https://rrys.lv/");
    }

    private void initViews() {
        webView = findViewById(R.id.webview);
        fullscreenContainer = findViewById(R.id.fullscreen_container);
        addressBar = findViewById(R.id.address_bar);
        navBar = findViewById(R.id.nav_bar);
        navContainer = findViewById(R.id.nav_container);
        urlInput = findViewById(R.id.url_input);
        btnGo = findViewById(R.id.btn_go);
        btnRefresh = findViewById(R.id.btn_refresh);
    }

    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setMediaPlaybackRequiresUserGesture(false);

        String ua = settings.getUserAgentString();
        settings.setUserAgentString(ua + " TVBrowser/1.0");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                urlInput.setText(url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (customView != null) {
                    onHideCustomView();
                    return;
                }
                customView = view;
                customViewCallback = callback;
                fullscreenContainer.setVisibility(View.VISIBLE);
                fullscreenContainer.addView(view);
                webView.setVisibility(View.GONE);
                navBar.setVisibility(View.GONE);
                addressBar.setVisibility(View.GONE);
            }

            @Override
            public void onHideCustomView() {
                if (customView == null) return;
                fullscreenContainer.removeView(customView);
                fullscreenContainer.setVisibility(View.GONE);
                customViewCallback.onCustomViewHidden();
                customView = null;
                webView.setVisibility(View.VISIBLE);
            }
        });

        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
    }

    private void setupNavBar() {
        navButtons = new View[PRESET_SITES.length + 1];

        for (int i = 0; i < PRESET_SITES.length; i++) {
            Button btn = createNavButton(PRESET_SITES[i][0]);
            final String url = PRESET_SITES[i][1];
            btn.setOnClickListener(v -> {
                webView.loadUrl(url);
                hideNavBar();
            });
            navContainer.addView(btn);
            navButtons[i] = btn;
        }

        Button btnInput = createNavButton("\uD83C\uDF10 输入网址");
        btnInput.setOnClickListener(v -> {
            hideNavBar();
            showAddressBar();
        });
        navContainer.addView(btnInput);
        navButtons[PRESET_SITES.length] = btnInput;
    }

    private Button createNavButton(String text) {
        Button btn = new Button(this);
        btn.setText(text);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(130), dpToPx(50));
        params.setMargins(dpToPx(6), dpToPx(4), dpToPx(6), dpToPx(4));
        btn.setLayoutParams(params);
        btn.setBackgroundResource(R.drawable.btn_selector);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(13);
        btn.setFocusable(true);
        btn.setFocusableInTouchMode(true);
        btn.setAllCaps(false);
        return btn;
    }

    private void setupAddressBar() {
        btnGo.setOnClickListener(v -> navigate());
        btnRefresh.setOnClickListener(v -> webView.reload());

        urlInput.setOnEditorActionListener((v, actionId, event) -> {
            navigate();
            return true;
        });
    }

    private void navigate() {
        String input = urlInput.getText().toString().trim();
        if (input.isEmpty()) return;

        String url;
        if (input.startsWith("http://") || input.startsWith("https://")) {
            url = input;
        } else if (input.contains(".") && !input.contains(" ")) {
            url = "https://" + input;
        } else {
            url = "https://www.baidu.com/s?wd=" + input;
        }

        webView.loadUrl(url);
        hideAddressBar();
    }

    private void showAddressBar() {
        if (isAddressBarVisible) return;
        isAddressBarVisible = true;
        addressBar.setVisibility(View.VISIBLE);
        urlInput.requestFocus();
        urlInput.selectAll();
    }

    private void hideAddressBar() {
        if (!isAddressBarVisible) return;
        isAddressBarVisible = false;
        addressBar.setVisibility(View.GONE);
    }

    private void showNavBar() {
        if (isNavBarVisible) return;
        isNavBarVisible = true;
        navBar.setVisibility(View.VISIBLE);
        if (navButtons.length > 0) {
            focusedNavIndex = 0;
            navButtons[0].requestFocus();
        }
    }

    private void hideNavBar() {
        if (!isNavBarVisible) return;
        isNavBarVisible = false;
        navBar.setVisibility(View.GONE);
        focusedNavIndex = -1;
    }

    private void hideBars() {
        hideAddressBar();
        hideNavBar();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (customView != null) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                webView.getWebChromeClient().onHideCustomView();
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                if (isNavBarVisible) {
                    hideNavBar();
                    showAddressBar();
                } else if (!isAddressBarVisible) {
                    showAddressBar();
                }
                return true;

            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (isAddressBarVisible) {
                    hideAddressBar();
                    showNavBar();
                } else if (!isNavBarVisible) {
                    showNavBar();
                }
                return true;

            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (isNavBarVisible && focusedNavIndex > 0) {
                    focusedNavIndex--;
                    navButtons[focusedNavIndex].requestFocus();
                    return true;
                } else if (!isAddressBarVisible && !isNavBarVisible) {
                    return false;
                }
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (isNavBarVisible && focusedNavIndex < navButtons.length - 1) {
                    focusedNavIndex++;
                    navButtons[focusedNavIndex].requestFocus();
                    return true;
                } else if (!isAddressBarVisible && !isNavBarVisible) {
                    return false;
                }
                break;

            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                break;

            case KeyEvent.KEYCODE_BACK:
                if (isAddressBarVisible || isNavBarVisible) {
                    hideBars();
                    return true;
                } else if (webView.canGoBack()) {
                    webView.goBack();
                    return true;
                } else {
                    showExitConfirm();
                    return true;
                }

            case KeyEvent.KEYCODE_MENU:
                if (isNavBarVisible) hideNavBar();
                else showNavBar();
                return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void showExitConfirm() {
        new AlertDialog.Builder(this)
                .setTitle("退出应用")
                .setMessage("确定要退出 TV Browser 吗？")
                .setPositiveButton("退出", (d, w) -> finish())
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        if (customView != null) {
            webView.getWebChromeClient().onHideCustomView();
        } else if (isAddressBarVisible || isNavBarVisible) {
            hideBars();
        } else if (webView.canGoBack()) {
            webView.goBack();
        } else {
            showExitConfirm();
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.loadUrl("about:blank");
            webView.removeAllViews();
            webView.destroy();
        }
        super.onDestroy();
    }
}
