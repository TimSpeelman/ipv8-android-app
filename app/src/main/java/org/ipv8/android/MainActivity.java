package org.ipv8.android;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceError;
import android.os.Handler;
import java.lang.Runnable;

import org.ipv8.android.service.IPV8Service;

public class MainActivity extends BaseActivity {

    private WebView mWebView;
    public static final int WRITE_STORAGE_PERMISSION_REQUEST_CODE = 110;
    public static final String url = "http://127.0.0.1:8085/gui";

    static {
        // Backwards compatibility for vector graphics
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private void shutdown() {
        killService();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

    protected void startService() {
        IPV8Service.start(this); // Run normally
    }

    protected void killService() {
        IPV8Service.stop(this);
    }

    @Override
    protected void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }
        switch (action) {

            case Intent.ACTION_MAIN:
                // Handle intent only once
                intent.setAction(null);
                return;

            case Intent.ACTION_SHUTDOWN:
                // Handle intent only once
                intent.setAction(null);
                shutdown();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Write permissions on sdcard?
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_PERMISSION_REQUEST_CODE);
        } else {
            startService();
        }

        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // Load the GUI
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                // Show custom error page
                if (view != null){
                    String htmlData ="<html><body><div align=\"center\" >Please wait while loading backend..</div></body>";
                    view.loadDataWithBaseURL(null, htmlData, "text/html", "UTF-8", null);
                }

                // Reload after 1s. Should happen only when IPv8 is still loading.
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl(url);
                    }
                }, 1000);
            }
        });
        mWebView.loadUrl(url);
    }
}