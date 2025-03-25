package com.sparkleside.ui.activities;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.*;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.sparkleside.R;
import com.sparkleside.databinding.ActivityHtmlViewerBinding;
import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.io.InputStream;
import android.graphics.Bitmap;
import java.io.OutputStream;
import android.content.Context;

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
        server = new WebServer(this, 11001, htmlContent);
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
        binding.webview.loadUrl("javascript:(function(){var s=document.createElement('script');s.type='application/javascript';s.src='/eruda.js';document.head.appendChild(s);})();");
        binding.webview.loadUrl("javascript:eruda.init();");
    }
    
    private void setupWebView() {
        WebSettings webSettings = binding.webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }
        binding.webview.setWebChromeClient(new WebChromeClient());
        binding.webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                injectEruda();
            }
        });
    }
    
public static class WebServer extends NanoHTTPD {
    private final String htmlContent;
    private final Context context;

    public WebServer(Context context, int port, String htmlContent) {
        super(port);
        this.context = context;
        this.htmlContent = htmlContent;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();

        if (uri.equals("/eruda.js")) {
            try {
                InputStream inputStream = context.getAssets().open("eruda.min.js");
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
                inputStream.close();
                return newFixedLengthResponse(Response.Status.OK, "application/javascript", new String(buffer));
            } catch (IOException e) {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "File not found");
            }
        }

        return newFixedLengthResponse(Response.Status.OK, "text/html", htmlContent);
    }
}

}
