package com.operit.netimageviewer.data.local;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * API 配置管理器
 * 将 API 完整接口地址持久化到 SharedPreferences
 * 用户设置什么地址，就直接使用什么地址，不做任何拼接
 */
public class ApiConfigManager {
    private static final String PREF_NAME = "api_config";
    private static final String KEY_BASE_URL = "base_url";
    private final SharedPreferences prefs;

    public ApiConfigManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 获取保存的 API 完整接口地址
     */
    public String getBaseUrl() {
        return prefs.getString(KEY_BASE_URL, "");
    }

    /**
     * 设置 API 完整接口地址
     */
    public void setBaseUrl(String url) {
        prefs.edit().putString(KEY_BASE_URL, url).apply();
    }

    /**
     * 重置为默认（空字符串，让用户自行设置）
     */
    public void resetToDefault() {
        prefs.edit().remove(KEY_BASE_URL).apply();
    }
}
