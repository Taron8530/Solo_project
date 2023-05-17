package com.example.solo_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.os.Bundle;

public class UsedSearchActivity extends AppCompatActivity {
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_used_search);
        searchView = findViewById(R.id.used_search_widget);
        searchView.setIconified(false);
    }
}