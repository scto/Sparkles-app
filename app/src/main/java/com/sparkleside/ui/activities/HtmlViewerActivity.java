package com.sparkleside.ui.activities;

import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.sparkleside.R;
import com.sparkleside.databinding.ActivityHtmlViewerBinding;
import androidx.appcompat.app.AppCompatActivity;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class HtmlViewerActivity extends AppCompatActivity {
  private ActivityHtmlViewerBinding binding;
  private HttpServer server;

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
    startServer();
    setupWebView();
    binding.webview.loadUrl("http://127.0.0.1:11001");
  }

  private void startServer() {
    try {
      server = HttpServer.create(new InetSocketAddress(11001), 0);
      server.createContext("/", new HttpHandler() {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
          String html = getIntent().getStringExtra("html");
          byte[] response = html.getBytes(StandardCharsets.UTF_8);
          exchange.sendResponseHeaders(200, response.length);
          OutputStream os = exchange.getResponseBody();
          os.write(response);
          os.close();
        }
      });
      server.setExecutor(Executors.newSingleThreadExecutor());
      server.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
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

  @Override
  protected void onDestroy() {
    if (server != null) {
      server.stop(0);
    }
    super.onDestroy();
  }
}
