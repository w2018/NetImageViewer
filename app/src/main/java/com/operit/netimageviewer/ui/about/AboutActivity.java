package com.operit.netimageviewer.ui.about;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.operit.netimageviewer.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.about_title);

        // === 基本信息 ===
        TextView tvAppName = findViewById(R.id.tv_app_name);
        TextView tvVersion = findViewById(R.id.tv_version);
        TextView tvAuthor = findViewById(R.id.tv_author);
        TextView tvGithub = findViewById(R.id.tv_github);

        tvAppName.setText(R.string.app_name);

        // 从 PackageManager 获取版本信息，避免 BuildConfig 依赖问题
        String versionText = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionText = getString(R.string.about_version_format, pInfo.versionName, pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            versionText = getString(R.string.about_version_format, "?", 0);
        }
        tvVersion.setText(versionText);

        tvAuthor.setText(R.string.about_author_text);
        tvGithub.setText(R.string.about_github_url);

        tvGithub.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_github_url)));
            startActivity(intent);
        });

        // === 数据结构区域（8个） ===
        TextView tvHelpDataIntro = findViewById(R.id.tv_help_data_intro);
        TextView tvHelpDataModelTitle = findViewById(R.id.tv_help_data_model_title);
        TextView tvHelpDataModelContent = findViewById(R.id.tv_help_data_model_content);
        TextView tvHelpDataClassifyTitle = findViewById(R.id.tv_help_data_classify_title);
        TextView tvHelpDataClassifyContent = findViewById(R.id.tv_help_data_classify_content);
        TextView tvHelpDataChapterTitle = findViewById(R.id.tv_help_data_chapter_title);
        TextView tvHelpDataChapterContent = findViewById(R.id.tv_help_data_chapter_content);
        TextView tvHelpDataPictureTitle = findViewById(R.id.tv_help_data_picture_title);
        TextView tvHelpDataPictureContent = findViewById(R.id.tv_help_data_picture_content);
        // 内容已在 layout 中通过 android:text 设置，无需额外 setText

        // === API 结构区域（8个） ===
        TextView tvHelpApiIntro = findViewById(R.id.tv_help_api_intro);
        TextView tvHelpApiEndpointIntro = findViewById(R.id.tv_help_api_endpoint_intro);
        TextView tvHelpApiEndpoint1Title = findViewById(R.id.tv_help_api_endpoint1_title);
        TextView tvHelpApiEndpoint1Content = findViewById(R.id.tv_help_api_endpoint1_content);
        TextView tvHelpApiEndpoint2Title = findViewById(R.id.tv_help_api_endpoint2_title);
        TextView tvHelpApiEndpoint2Content = findViewById(R.id.tv_help_api_endpoint2_content);
        TextView tvHelpApiEndpoint3Title = findViewById(R.id.tv_help_api_endpoint3_title);
        TextView tvHelpApiEndpoint3Content = findViewById(R.id.tv_help_api_endpoint3_content);

        // === 网络层配置区域（1个） ===
        TextView tvHelpNetworkContent = findViewById(R.id.tv_help_network_content);

        // === 数据库结构区域（7个） ===
        TextView tvHelpDbIntro = findViewById(R.id.tv_help_db_intro);
        TextView tvHelpDbTable1 = findViewById(R.id.tv_help_db_table1);
        TextView tvHelpDbTable2 = findViewById(R.id.tv_help_db_table2);
        TextView tvHelpDbTable3 = findViewById(R.id.tv_help_db_table3);
        TextView tvHelpDbTable4 = findViewById(R.id.tv_help_db_table4);
        TextView tvHelpDbTable5 = findViewById(R.id.tv_help_db_table5);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
