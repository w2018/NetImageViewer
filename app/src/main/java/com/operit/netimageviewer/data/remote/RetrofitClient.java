package com.operit.netimageviewer.data.remote;

import android.content.Context;

import com.operit.netimageviewer.data.local.ApiConfigManager;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit 单例管理
 * 配置 OkHttp 缓存（10MB）、超时、日志拦截器
 *
 * 注意：不再包含任何默认的 BASE_URL 硬编码。
 * 所有请求的 base URL 由用户在设置页面自行输入，
 * 通过 ApiConfigManager 持久化存储，调用方必须先确保已设置有效的 URL。
 */
public class RetrofitClient {

    private static final long CACHE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final int CONNECT_TIMEOUT = 15; // 秒
    private static final int READ_TIMEOUT = 30;    // 秒
    private static final int WRITE_TIMEOUT = 30;   // 秒

    private static volatile RetrofitClient instance;
    private Retrofit retrofit;
    private ApiService apiService;
    private final ApiConfigManager configManager;
    private final Context appContext;
    private OkHttpClient okHttpClient; // 保存 OkHttpClient 引用，替代已移除的 retrofit.client()

    private RetrofitClient(Context context) {
        this.appContext = context.getApplicationContext();
        this.configManager = new ApiConfigManager(appContext);

        // 日志拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // OkHttp 缓存目录
        File cacheDir = new File(appContext.getCacheDir(), "http_cache");
        Cache cache = new Cache(cacheDir, CACHE_SIZE);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addNetworkInterceptor(chain -> {
                    okhttp3.Request originalRequest = chain.request();
                    okhttp3.Request request = originalRequest.newBuilder()
                            .header("Cache-Control", "public, max-age=3600")
                            .build();
                    okhttp3.Response response = chain.proceed(request);
                    return response.newBuilder()
                            .header("Cache-Control", "public, max-age=3600")
                            .removeHeader("Pragma")
                            .build();
                })
                .build();

        // 保存 OkHttpClient 引用，供后续重建 Retrofit 时使用
        this.okHttpClient = okHttpClient;

        // 用用户已设置的 URL 初始化；如果尚未设置则为空字符串
        String savedUrl = configManager.getBaseUrl();
        String baseUrl = ensureTrailingSlash(savedUrl);
        retrofit = buildRetrofit(baseUrl, okHttpClient);
    }

    public static synchronized RetrofitClient getInstance(Context context) {
        if (instance == null) {
            instance = new RetrofitClient(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * 获取 ApiService 实例
     * 每次调用都会检查 URL 是否已配置；若无则抛出明确异常引导用户设置
     * 如果用户更改了 URL 但未调用 updateBaseUrl，会自动检测并重建
     */
    public synchronized ApiService getApiService() {
        String currentUrl = configManager.getBaseUrl();
        if (currentUrl == null || currentUrl.trim().isEmpty()) {
            apiService = null;
            throw new IllegalStateException(
                    "API 服务器地址未设置，请在「设置」页面中配置有效的 API 地址后再使用。");
        }

        // 检查 URL 是否变化，若变化则自动重建（支持动态切换）
        String normalizedUrl = ensureTrailingSlash(currentUrl.trim());
        if (apiService == null || !retrofit.baseUrl().toString().equals(normalizedUrl)) {
            retrofit = buildRetrofit(normalizedUrl, okHttpClient);
            apiService = retrofit.create(ApiService.class);
        }

        return apiService;
    }

    /**
     * 更新 Retrofit 的 baseUrl（由用户设置页面触发）
     * 允许传入空字符串来清空 baseUrl（重置后让用户重新输入）
     */
    public void updateBaseUrl(String newBaseUrl) {
        String url;
        if (newBaseUrl == null || newBaseUrl.trim().isEmpty()) {
            url = "";
        } else {
            url = ensureTrailingSlash(newBaseUrl.trim());
        }
        retrofit = buildRetrofit(url, okHttpClient);
        apiService = null; // 下次使用 getApiService 时重新创建
    }

    /**
     * 检查当前是否已配置有效的 API 地址
     */
    public boolean hasBaseUrl() {
        String url = configManager.getBaseUrl();
        return url != null && !url.trim().isEmpty();
    }

    /**
     * 获取当前 API 地址（来自 SharedPreferences）
     */
    public String getCurrentBaseUrl() {
        return configManager.getBaseUrl();
    }

    private static Retrofit buildRetrofit(String baseUrl, OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl.isEmpty() ? "http://localhost/" : baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static String ensureTrailingSlash(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }
        if (!url.endsWith("/")) {
            return url + "/";
        }
        return url;
    }
}
