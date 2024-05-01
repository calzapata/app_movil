package com.example.porcicarnes_apps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {
    WebView miVisorWeb;
    private String url = "https://mttopv.netlify.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        miVisorWeb = findViewById(R.id.visorWeb);
        configurarWebView();
        cargarUrlEnWebView(url);
    }

    private void configurarWebView() {
        WebSettings ajustesVisorWeb = miVisorWeb.getSettings();
        ajustesVisorWeb.setJavaScriptEnabled(true); // Habilita JavaScript si es necesario

        // Establece un WebViewClient para gestionar las redirecciones dentro del WebView
        miVisorWeb.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Si la URL comienza con "http" o "https", carga la URL en el WebView
                if (url.startsWith("http") || url.startsWith("https")) {
                    view.loadUrl(url);
                    return true;
                } else {
                    // Si la URL no es una URL HTTP/HTTPS, intenta abrirla con una aplicación externa
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
            }
        });
    }

    private void cargarUrlEnWebView(String url) {
        miVisorWeb.loadUrl(url);
    }

    // Impedir que el botón Atrás cierre la aplicación
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (miVisorWeb.canGoBack()) {
                        miVisorWeb.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}