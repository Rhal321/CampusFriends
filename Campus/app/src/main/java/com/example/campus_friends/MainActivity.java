package com.example.campus_friends;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 사용자 이름 표시
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "사용자");
        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText(username + "님 환영합니다");

        // 상단 "학교 기능" 4개
        findViewById(R.id.gridHomepage).findViewWithTag("홈").setOnClickListener(v ->
                openUrl("https://www.sungkyul.ac.kr/skukr/index.do"));

        findViewById(R.id.gridHomepage).findViewWithTag("길").setOnClickListener(v ->
                openUrl("https://www.sungkyul.ac.kr/skukr/262/subview.do"));

        findViewById(R.id.gridHomepage).findViewWithTag("공지").setOnClickListener(v ->
                openUrl("https://www.sungkyul.ac.kr/skukr/343/subview.do"));

        findViewById(R.id.gridHomepage).findViewWithTag("일정").setOnClickListener(v ->
                openUrl("https://www.sungkyul.ac.kr/skukr/313/subview.do"));

        // 하단 "학생 생활 도우미" 4개
        findViewById(R.id.gridHelper).findViewWithTag("시간표").setOnClickListener(v ->
                startActivity(new Intent(this, Timetable.class)));

        findViewById(R.id.gridHelper).findViewWithTag("일정관리").setOnClickListener(v ->
        {}); // TODO

        findViewById(R.id.gridHelper).findViewWithTag("전공책").setOnClickListener(v ->
        {}); // TODO

        findViewById(R.id.gridHelper).findViewWithTag("학식").setOnClickListener(v ->
        {}); // TODO

        setupBottomNav();
    }

    private void openUrl(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.nav_timetable) {
                    startActivity(new Intent(MainActivity.this, Timetable.class));
                    return true;
                } else if (id == R.id.nav_todo) {
                    // TODO
                    return true;
                } else if (id == R.id.nav_books) {
                    // TODO
                    return true;
                } else if (id == R.id.nav_meal) {
                    // TODO
                    return true;
                }

                return false;
            }
        });
    }
}
