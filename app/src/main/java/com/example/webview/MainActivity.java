package com.example.webview;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    private static final String URL_Main = "https://heron-good-curiously.ngrok-free.app/cam";
    private static final int MY_PERMISSION_REQUEST_LOCATION = 0;
    private static final int MY_PERMISSION_REQUEST_STORAGE = 1;
    private static final int REQ_CAPTURE_IMAGE = 2001; // 카메라 촬영 요청 코드

    private WebView webView;
    private ValueCallback<Uri[]> filePathCallback;
    private Uri imageUri; // 촬영한 이미지의 Uri

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        permissionCheck(); // 권한 체크
    }

    private void initWebView() {
        // 자바스크립트 사용 허용
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);

        // 새로운 브라우저 창 대신 WebView에서 열기
        webView.setWebViewClient(new WebViewClient());

        // 파일 선택기 설정
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                super.onGeolocationPermissionsShowPrompt(origin, callback);
                callback.invoke(origin, true, false);
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                MainActivity.this.filePathCallback = filePathCallback;

                // 카메라로 이미지를 촬영하기 위한 Intent 실행
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 이미지 파일을 저장할 URI 준비
                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, REQ_CAPTURE_IMAGE);

                return true;
            }
        });

        // URL 로드
        webView.loadUrl(URL_Main);
    }

    private void permissionCheck() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initWebView();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            }, MY_PERMISSION_REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSION_REQUEST_LOCATION || requestCode == MY_PERMISSION_REQUEST_STORAGE) {
            boolean locationGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean storageGranted = grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED;
            boolean cameraGranted = grantResults.length > 2 && grantResults[2] == PackageManager.PERMISSION_GRANTED;

            if (locationGranted && storageGranted && cameraGranted) {
                initWebView();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CAPTURE_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (filePathCallback != null) {
                    filePathCallback.onReceiveValue(new Uri[]{imageUri});
                    filePathCallback = null;
                }
            } else {
                if (filePathCallback != null) {
                    filePathCallback.onReceiveValue(null);
                    filePathCallback = null;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack(); // 이전 페이지로 이동
        } else {
            super.onBackPressed(); // 기본 동작 (앱 종료)
        }
    }
}
