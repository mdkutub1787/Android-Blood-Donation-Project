package com.example.todoapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.model.TodoModel;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder>{

    private List<TodoModel> notificationList;
    private Context context;

    public TodoAdapter(List<TodoModel> notificationList, Context context) {
        this.notificationList = notificationList;
        this.context = context;
    }

    @NonNull
    @Override
    public TodoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(context).inflate(com.example.todoapp.R.layout.todo_card, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull TodoAdapter.ViewHolder holder, int position) {

        TodoModel notification = notificationList.get(position);
        holder.textDate.setText(notification.getDate());
        holder.textDescription.setText(notification.getTitel());
        holder.textTitle.setText(notification.getDescription());
        holder.textTodotype.setText(notification.getTodotype());
        holder.textPriority.setText(notification.getPriority());

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textDate;
        TextView textDescription;
        TextView textTitle;
        TextView textTodotype;
        TextView textPriority;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textDate=itemView.findViewById(R.id.noticeDate);
            textDescription=itemView.findViewById(R.id.noticeDescription);
            textTitle=itemView.findViewById(R.id.noticeTitle);
            textTodotype=itemView.findViewById(R.id.noticeTodotype);
            textPriority=itemView.findViewById(R.id.noticePriority);
        }


    }

}
