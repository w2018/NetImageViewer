package com.operit.netimageviewer.ui.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.operit.netimageviewer.R;
import com.operit.netimageviewer.data.local.ApiConfigManager;
import com.operit.netimageviewer.data.remote.RetrofitClient;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SettingsActivity extends AppCompatActivity {
    private EditText etBaseUrl;
    private TextView tvCurrentUrl;
    private Button btnSave;
    private Button btnReset;
    private Button btnTest;
    private TextView tvTestResult;
    private ApiConfigManager configManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.settings_title);

        configManager = new ApiConfigManager(this);

        tvCurrentUrl = findViewById(R.id.tv_current_url);
        etBaseUrl = findViewById(R.id.et_base_url);
        btnSave = findViewById(R.id.btn_save);
        btnReset = findViewById(R.id.btn_reset);
        btnTest = findViewById(R.id.btn_test);
        tvTestResult = findViewById(R.id.tv_test_result);

        // 显示当前地址
        String currentUrl = configManager.getBaseUrl();
        tvCurrentUrl.setText(getString(R.string.settings_current_url, currentUrl));
        etBaseUrl.setText(currentUrl);
        etBaseUrl.setSelection(currentUrl.length());

        btnSave.setOnClickListener(v -> saveBaseUrl());
        btnReset.setOnClickListener(v -> resetToDefault());
        btnTest.setOnClickListener(v -> testConnection());
    }

    private void saveBaseUrl() {
        String newUrl = etBaseUrl.getText().toString().trim();
        if (TextUtils.isEmpty(newUrl)) {
            etBaseUrl.setError(getString(R.string.settings_url_empty_error));
            return;
        }
        configManager.setBaseUrl(newUrl);
        RetrofitClient.getInstance(this).updateBaseUrl(newUrl);
        tvCurrentUrl.setText(getString(R.string.settings_current_url, newUrl));

        // 清理缓存
        com.operit.netimageviewer.data.repository.ImageRepository repo =
                new com.operit.netimageviewer.data.repository.ImageRepository(this);
        repo.clearHttpCache();
        repo.clearAllRoomCaches();

        Snackbar.make(btnSave, R.string.settings_save_success, Snackbar.LENGTH_SHORT).show();

        // 重启 HomeActivity 刷新数据
        restartHomeActivity();
    }

    private void resetToDefault() {
        configManager.resetToDefault();
        String emptyUrl = "";
        RetrofitClient.getInstance(this).updateBaseUrl(emptyUrl);
        etBaseUrl.setText(emptyUrl);
        tvCurrentUrl.setText(getString(R.string.settings_current_url, emptyUrl));
        Snackbar.make(btnReset, R.string.settings_reset_success, Snackbar.LENGTH_SHORT).show();
    }

    private void testConnection() {
        String url = etBaseUrl.getText().toString().trim();
        if (TextUtils.isEmpty(url)) {
            tvTestResult.setText(R.string.settings_test_url_empty);
            return;
        }
        tvTestResult.setText(R.string.settings_testing);
        btnTest.setEnabled(false);

        final String testUrl = url;
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
                        .readTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
                        .build();
                Request request = new Request.Builder()
                        .url(testUrl)
                        .get()
                        .build();
                Response response = client.newCall(request).execute();
                int code = response.code();
                String bodyStr = "";
                String contentType = "";
                long contentLength = 0;
                if (response.body() != null) {
                    contentType = response.body().contentType() != null ? response.body().contentType().toString() : "";
                    contentLength = response.body().contentLength();
                    // 读取前500字符作为预览
                    okhttp3.ResponseBody body = response.body();
                    String fullBody = body.string();
                    bodyStr = fullBody.length() > 500 ? fullBody.substring(0, 500) + "\n... (truncated)" : fullBody;
                }
                response.close();
                final String resultBody = bodyStr;
                final String resultContentType = contentType;
                final long resultContentLength = contentLength;
                runOnUiThread(() -> {
                    String detail = "HTTP " + code;
                    if (resultContentType.length() > 0) {
                        detail += "\nContent-Type: " + resultContentType;
                    }
                    if (resultContentLength > 0) {
                        detail += "\nContent-Length: " + resultContentLength + " bytes";
                    }
                    if (resultBody.length() > 0) {
                        detail += "\n\nResponse Preview:\n" + resultBody;
                    }
                    tvTestResult.setText(detail);
                    btnTest.setEnabled(true);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    tvTestResult.setText(getString(R.string.settings_test_failed, e.getMessage()));
                    btnTest.setEnabled(true);
                });
            }
        }).start();
    }

    private void restartHomeActivity() {
        android.content.Intent intent = new android.content.Intent(this,
                com.operit.netimageviewer.ui.home.HomeActivity.class);
        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
