package com.dm.assignment_tpm_world;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class PdfView_Activity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view_);
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progress_bar);
        String url = "https://www.esa.int/esapub/br/br184/br184_4.pdf";
        String finalUrl = "https://docs.google.com/gview?embedded=true&url=" + url;

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().getBuiltInZoomControls();
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);

        webView.setVisibility(View.GONE);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.loadUrl("javascript:(function() { " +
                        "document.querySelector('[role=\"toolbar\"]').remove();})()");
                webView.setVisibility(View.VISIBLE);
            }
        });


        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                    getSupportActionBar();

                }
            }
        });

        webView.loadUrl(finalUrl);
        //to secure pdf from screenshots and screen recorder
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PdfView_Activity.this, MainActivity.class);
        startActivity(intent);
    }
}