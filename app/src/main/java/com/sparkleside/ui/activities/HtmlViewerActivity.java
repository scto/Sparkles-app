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
import java.io.*;
import java.net.*;
import java.util.concurrent.Executors;

public class HtmlViewerActivity extends AppCompatActivity {
    private ActivityHtmlViewerBinding binding;
    private ServerSocket serverSocket;
    private boolean isRunning = true;

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
        startHttpServer();
        setupWebView();
        binding.webview.loadUrl("http://127.0.0.1:11001");
    }

    private void startHttpServer() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                serverSocket = new ServerSocket(11001);
                while (isRunning) {
                    Socket socket = serverSocket.accept();
                    new Thread(() -> handleRequest(socket)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void handleRequest(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             OutputStream out = socket.getOutputStream()) {
            in.readLine();
            String html = getIntent().getStringExtra("html");
            String response = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\nContent-Length: " + html.length() + "\r\n\r\n" + html;
            out.write(response.getBytes());
            out.flush();
            socket.close();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
