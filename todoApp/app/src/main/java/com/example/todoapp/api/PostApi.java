package com.example.todoapp.api;

import com.example.todoapp.model.TodoModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostApi {

    // POST request to create a new Todo
    @POST("todoapps")
    Call<Void> createTodo(@Body TodoModel todo);

}
