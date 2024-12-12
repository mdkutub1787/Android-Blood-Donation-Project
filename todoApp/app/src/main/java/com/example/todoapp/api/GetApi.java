package com.example.todoapp.api;

import com.example.todoapp.model.TodoModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
public interface GetApi {

    @GET("todoapps")
    Call<List<TodoModel>> getTodo();

}
