package com.example.campus_friends;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private EditText etName, etPassword;
    private Button btnRegister;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.google.firebase.FirebaseApp.initializeApp(this);

        setContentView(R.layout.register);  // register.xml

        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);

        db = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "이름과 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        // 중복 사용자 확인
        db.collection("users")
                .whereEqualTo("name", name)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        Toast.makeText(this, "이미 존재하는 이름입니다", Toast.LENGTH_SHORT).show();
                    } else {
                        saveUserToFirestore(name, password);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "사용자 확인 실패", Toast.LENGTH_SHORT).show();
                    Log.e("REGISTER", "중복 확인 실패", e);
                });
    }

    private void saveUserToFirestore(String name, String password) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("password", password);
        user.put("createdAt", FieldValue.serverTimestamp());

        db.collection("users")
                .add(user)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "회원가입 완료", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Register.this, Login.class));

                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                    Log.e("REGISTER", "저장 실패", e);
                });
    }
}
