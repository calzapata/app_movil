package com.example.porcicarnes_apps;
import android.view.Window;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {
    WebView miVisorWeb;
    private String url = "https://magenta-sunshine-8dc2dc.netlify.app/";
    private ValueCallback<Uri[]> fileUploadCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = this.getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        WindowInsetsControllerCompat insetsController = new WindowInsetsControllerCompat(window, window.getDecorView());
        insetsController.setAppearanceLightStatusBars(false); // false = texto blanco, true = texto negro



        miVisorWeb = findViewById(R.id.visorWeb);
        miVisorWeb.setWebContentsDebuggingEnabled(true); // Habilitar depuración

        configurarWebView();
        cargarUrlEnWebView(url);
    }

    private void configurarWebView() {
        WebSettings ajustesVisorWeb = miVisorWeb.getSettings();
        ajustesVisorWeb.setJavaScriptEnabled(true); // Habilita JavaScript
        ajustesVisorWeb.setDomStorageEnabled(true); // Habilita almacenamiento local
        ajustesVisorWeb.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); // Usa caché si está disponible
        ajustesVisorWeb.setUseWideViewPort(true); // Habilita vista ajustada
        ajustesVisorWeb.setLoadWithOverviewMode(true); // Ajusta el zoom automático
        ajustesVisorWeb.setJavaScriptCanOpenWindowsAutomatically(true); // Permite redirecciones
        ajustesVisorWeb.setSupportMultipleWindows(true); // Soporte para ventanas múltiples

        // Habilitar cookies
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(miVisorWeb, true);

        miVisorWeb.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.startsWith("http") || url.startsWith("https")) {
                    view.loadUrl(url);
                    return true;
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                view.loadUrl("file:///android_asset/error.html"); // Página personalizada de error
            }
        });

        miVisorWeb.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (fileUploadCallback != null) {
                    fileUploadCallback.onReceiveValue(null);
                }
                fileUploadCallback = filePathCallback;
                abrirSelectorDeArchivos();
                return true;
            }
        });

        // Manejo de descargas
        miVisorWeb.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
    }

    private void abrirSelectorDeArchivos() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        archivoSeleccionado.launch(intent);
    }

    private final ActivityResultLauncher<Intent> archivoSeleccionado = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (fileUploadCallback != null) {
                    Uri resultUri = (result.getResultCode() == RESULT_OK && result.getData() != null) ? result.getData().getData() : null;
                    fileUploadCallback.onReceiveValue(resultUri != null ? new Uri[]{resultUri} : null);
                    fileUploadCallback = null;
                }
            }
    );

    private void cargarUrlEnWebView(String url) {
        miVisorWeb.loadUrl(url);
    }

    // Impedir que el botón Atrás cierre la aplicación
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
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
