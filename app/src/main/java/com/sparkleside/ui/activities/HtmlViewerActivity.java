package com.sparkleside.ui.activities;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.sparkleside.R;
import com.sparkleside.databinding.ActivityHtmlViewerBinding;
import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HtmlViewerActivity extends AppCompatActivity {
    private ActivityHtmlViewerBinding binding;
    private WebServer server;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityHtmlViewerBinding.inflate(getLayoutInflater());
        binding.coordinator.setTransitionName("html");
        setEnterSharedElementCallback(new MaterialContainerTransformSharedElementCallback());
        getWindow().setSharedElementEnterTransition(new MaterialContainerTransform().addTarget(R.id.coordinator).setDuration(400));
        getWindow().setSharedElementReturnTransition(new MaterialContainerTransform().addTarget(R.id.coordinator).setDuration(350));
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        
        String htmlContent = getIntent().getStringExtra("html");
        server = new WebServer(11001, htmlContent);
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        setupWebView();
        binding.webview.loadUrl("http://127.0.0.1:11001");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (server != null) {
            server.stop();
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
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageFinished(view, url);
                injectEruda();
            }
        });
    }
    
    public static class WebServer extends NanoHTTPD {
        private final String htmlContent;
        
        public WebServer(int port, String htmlContent) {
            super(port);
            this.htmlContent = htmlContent;
        }
        
        @Override
        public Response serve(IHTTPSession session) {
            return newFixedLengthResponse(Response.Status.OK, "text/html", htmlContent); // TODO: make it dynamic mime types instead of just text/html
        }
    }
}
