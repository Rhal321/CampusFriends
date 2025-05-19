package com.example.campus_friends;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.campus_friends.models.Lecture;
import com.google.firebase.firestore.FirebaseFirestore;

public class TimetableUI {

    public static void drawGrid(FrameLayout frame, int cellWidth, int cellHeight, int startX, int startY, String[] days, int[] times) {
        Context context = frame.getContext();

        // 요일 라벨
        for (int i = 0; i < days.length; i++) {
            TextView dayView = createLabel(context, days[i], cellWidth, startY);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(cellWidth, startY);
            params.leftMargin = startX + i * cellWidth;
            params.topMargin = 0;
            frame.addView(dayView, params);
        }

        // 시간 라벨
        for (int i = 0; i < times.length; i++) {
            TextView timeView = createLabel(context, times[i] + ":00", startX, cellHeight);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(startX, cellHeight);
            params.leftMargin = 0;
            params.topMargin = startY + i * cellHeight;
            frame.addView(timeView, params);
        }

        // 격자 셀
        for (int row = 0; row < times.length; row++) {
            for (int col = 0; col < days.length; col++) {
                View cell = new View(context);
                cell.setBackgroundColor(0xFFE0E0E0);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(cellWidth - 2, cellHeight - 2);
                params.leftMargin = startX + col * cellWidth;
                params.topMargin = startY + row * cellHeight;
                frame.addView(cell, params);
            }
        }
    }

    private static TextView createLabel(Context context, String text, int width, int height) {
        TextView label = new TextView(context);
        label.setText(text);
        label.setBackgroundColor(0xFFCCCCCC);
        label.setGravity(Gravity.CENTER);
        label.setTextSize(14);
        label.setTextColor(Color.BLACK);
        label.setWidth(width);
        label.setHeight(height);
        return label;
    }

    public static void addLectureBlock(FrameLayout frame, Lecture lec, int cellWidth, int cellHeight, int startX, int startY, int[] times) {
        int dayIndex = getDayIndex(lec.day);
        double rowOffset = lec.startHour - times[0];
        int topMargin = (int) (startY + rowOffset * cellHeight);

        Context context = frame.getContext();
        TextView block = new TextView(context);
        block.setText(lec.name + " " + lec.getPeriodLabel() + "\n" + lec.room);
        block.setBackgroundColor(Color.parseColor(lec.colorHex));
        block.setTextColor(Color.WHITE);
        block.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        block.setPadding(8, 8, 8, 8);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                cellWidth - 4,
                (int) (lec.duration * cellHeight) - 4
        );
        params.leftMargin = startX + dayIndex * cellWidth;
        params.topMargin = topMargin;
        frame.addView(block, params);

        block.setOnClickListener(v -> new AlertDialog.Builder(context)
                .setTitle(lec.name)
                .setMessage("[" + lec.day + "] " + lec.startHour + "시부터 " +
                        lec.duration + "시간\n" + lec.room)
                .setPositiveButton("삭제", (dialog, which) -> {
                    frame.removeView(block);
                    String userName = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                            .getString("username", null);
                    if (userName != null) {
                        FirebaseFirestore.getInstance()
                                .collection("timetables")
                                .document(userName)
                                .collection("entries")
                                .document(lec.id)
                                .delete();
                    }
                })
                .setNegativeButton("닫기", null)
                .show());
    }

    private static int getDayIndex(String day) {
        switch (day) {
            case "월": return 0;
            case "화": return 1;
            case "수": return 2;
            case "목": return 3;
            case "금": return 4;
            default: return 0;
        }
    }
}
