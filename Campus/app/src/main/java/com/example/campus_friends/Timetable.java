package com.example.campus_friends;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.campus_friends.models.Lecture;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class Timetable extends AppCompatActivity {

    private FrameLayout scheduleFrame;
    private Button btnAddLecture;
    private FirebaseFirestore db;
    private List<Lecture> lectureList = new ArrayList<>();
    private AlertDialog dialog;

    // 그리드 크기 관련
    private int cellWidth, cellHeight, startX, startY;
    private final String[] days = {"월", "화", "수", "목", "금"};
    private final int[] times = {9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable);

        scheduleFrame = findViewById(R.id.scheduleFrame);
        btnAddLecture = findViewById(R.id.btnAddLecture);
        db = FirebaseFirestore.getInstance();

        // ✅ username 표시
        TextView usernameText = findViewById(R.id.username);
        String username = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("username", null);
        if (username != null) {
            usernameText.setText(username + "님의 시간표");
        } else {
            usernameText.setText("사용자 정보를 불러올 수 없습니다");
        }

        // ✅ 화면 크기 기반 cell 계산
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        startX = 120;
        startY = 160;
        int usableWidth = screenWidth - startX - 40;
        int usableHeight = screenHeight - startY - 400;
        cellWidth = usableWidth / days.length;
        cellHeight = usableHeight / times.length;

        TimetableUI.drawGrid(scheduleFrame, cellWidth, cellHeight, startX, startY, days, times);

        btnAddLecture.setOnClickListener(v -> showLectureDialog());

        loadLecturesFromFirestore(); // ⏱ 강의 먼저 로드한 뒤 시간표 불러오기
    }

    private void loadLecturesFromFirestore() {
        db.collection("lectures")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    lectureList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Lecture lec = doc.toObject(Lecture.class);
                        lec.id = doc.getId();
                        lectureList.add(lec);
                    }
                    loadUserTimetable(); // ✅ 강의 리스트 채운 뒤 불러오기
                });
    }

    private void loadUserTimetable() {
        String userName = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("username", null);
        if (userName == null) return;

        db.collection("timetables")
                .document(userName)
                .collection("entries")
                .get()
                .addOnSuccessListener(snapshot -> {
                    for (QueryDocumentSnapshot entry : snapshot) {
                        String lectureId = entry.getString("lectureId");
                        if (lectureId == null) continue;
                        for (Lecture lec : lectureList) {
                            if (lectureId.equals(lec.id)) {
                                TimetableUI.addLectureBlock(scheduleFrame, lec, cellWidth, cellHeight, startX, startY, times);
                                break;
                            }
                        }
                    }
                });
    }

    private void showLectureDialog() {
        ScrollView scrollView = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(layout);

        for (Lecture lec : lectureList) {
            Button btn = new Button(this);
            btn.setText(lec.name + " (" + lec.day + " " + lec.getPeriodLabel() + ")");
            btn.setOnClickListener(v -> {
                TimetableUI.addLectureBlock(scheduleFrame, lec, cellWidth, cellHeight, startX, startY, times);
                saveLectureToUserTimetable(lec.id);
                dialog.dismiss();
            });
            layout.addView(btn);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("강의 선택").setView(scrollView).setNegativeButton("닫기", null);
        dialog = builder.create();
        dialog.show();
    }

    private void saveLectureToUserTimetable(String lectureId) {
        String userName = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("username", null);
        if (userName == null) return;

        db.collection("timetables")
                .document(userName)
                .collection("entries")
                .document(lectureId)  // ✅ lectureId를 문서 ID로 사용 (중복 방지)
                .set(new TimetableEntry(lectureId));
    }

    public static class TimetableEntry {
        public String lectureId;
        public Timestamp addedAt;

        public TimetableEntry() {}

        public TimetableEntry(String lectureId) {
            this.lectureId = lectureId;
            this.addedAt = Timestamp.now();
        }
    }


}
