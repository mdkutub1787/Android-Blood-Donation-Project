package com.example.todoapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.adapter.TodoAdapter;
import com.example.todoapp.api.GetApi;
import com.example.todoapp.apiClient.ApiClient;
import com.example.todoapp.model.TodoModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetActivity extends AppCompatActivity {


    private RecyclerView notificationList;
    private TodoAdapter notificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        notificationList=findViewById(R.id.noticeList);

        notificationList.setLayoutManager(new LinearLayoutManager(this));

        GetApi notificationApi = ApiClient.getRetrofit().create(GetApi.class);

        Call<List<TodoModel>> call = notificationApi.getTodo();


        call.enqueue(new Callback<List<TodoModel>>() {
            @Override
            public void onResponse(Call<List<TodoModel>> call, Response<List<TodoModel>> response) {

                if (response.isSuccessful()) {
                    List<TodoModel> noticeList = response.body();

                    // Set up RecyclerView with the adapter
                    notificationAdapter = new TodoAdapter(noticeList, getApplicationContext() );
                    notificationList.setAdapter(notificationAdapter);
                }

            }

            @Override
            public void onFailure(Call<List<TodoModel>> call, Throwable t) {

            }
        });






    }
}