package com.sparkleside.ui.activities;

import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.io.IOException;
import java.io.InputStream;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.sparkleside.R;
import com.sparkleside.databinding.ActivityHtmlViewerBinding;

public class HtmlViewerActivity extends AppCompatActivity {
  private ActivityHtmlViewerBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    binding = ActivityHtmlViewerBinding.inflate(getLayoutInflater());
    binding.coordinator.setTransitionName("html");
    setEnterSharedElementCallback(new MaterialContainerTransformSharedElementCallback());
    getWindow()
        .setSharedElementEnterTransition(
            new MaterialContainerTransform().addTarget(R.id.coordinator).setDuration(400));
    getWindow()
        .setSharedElementReturnTransition(
            new MaterialContainerTransform().addTarget(R.id.coordinator).setDuration(350));
    super.onCreate(savedInstanceState);
    setContentView(binding.getRoot());
    setSupportActionBar(binding.toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    setupWebView();
    binding.webview.loadUrl("data:text/html,".concat(getIntent().getStringExtra("html")));
  }

  private void injectEruda() {
        try {
            InputStream is = getAssets().open("eruda.min.js");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsCode = new String(buffer);
            binding.webview.loadUrl("javascript:(function() {" + jsCode + " eruda.init(); })();");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
  
  private void setupWebView() {
        WebSettings webSettings = binding.webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        binding.webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                injectEruda();
            }
        });
    }
}
