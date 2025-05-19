package com.example.campus_friends.models;

import com.google.firebase.firestore.PropertyName;

public class Lecture {

    public String id;

    @PropertyName("lecture_name")
    public String name;

    public String day;
    public double startHour;
    public double duration;
    public String room;
    public String colorHex;

    // 기본 생성자 (Firebase에서 객체로 파싱할 때 필요)
    public Lecture() {}

    // 모든 필드 포함 생성자
    public Lecture(String id, String name, String day, double startHour, double duration, String room, String colorHex) {
        this.id = id;
        this.name = name;
        this.day = day;
        this.startHour = startHour;
        this.duration = duration;
        this.room = room;
        this.colorHex = colorHex;
    }

    // Getter/Setter with @PropertyName (Firestore 필드명과 매핑)
    @PropertyName("lecture_name")
    public String getName() {
        return name;
    }

    @PropertyName("lecture_name")
    public void setName(String name) {
        this.name = name;
    }

    // 수업 시작 시간에 따른 교시 매핑 (예: 9.0 → 1-3교시)
    public String getPeriodLabel() {
        if (startHour == 9.0) return "1-3";
        if (startHour == 11.75) return "4-6";
        if (startHour == 14.5) return "7-9";
        return "";
    }
}
