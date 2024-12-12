package com.example.todoapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.api.PostApi;
import com.example.todoapp.apiClient.ApiClient;
import com.example.todoapp.model.TodoModel;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextDescription, date;
    private Spinner spinnerTodotype;
    private RadioGroup radioGroupPriority;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // Initialize views
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        spinnerTodotype = findViewById(R.id.spinnerTodotype);
        date = findViewById(R.id.date);
        radioGroupPriority = findViewById(R.id.radioGroupPriority);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Set up the spinner (dropdown) with values
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.todo_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTodotype.setAdapter(adapter);

        // Set date picker on EditText click
        date.setOnClickListener(v -> showDatePickerDialog());

        // Handle submit button click
        btnSubmit.setOnClickListener(v -> submitTodo());
    }

    private void showDatePickerDialog() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, (view, selectedYear, selectedMonth, selectedDay) -> {
            // Set the selected date in the EditText
            String dateString = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
            date.setText(dateString);
        }, year, month, day);

        // Show the dialog
        datePickerDialog.show();
    }

    private void submitTodo() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String selectedTodotype = spinnerTodotype.getSelectedItem().toString();

        // Get selected priority from RadioGroup
        int selectedPriorityId = radioGroupPriority.getCheckedRadioButtonId();
        String selectedPriority = "";
        if (selectedPriorityId != -1) {
            RadioButton selectedPriorityButton = findViewById(selectedPriorityId);
            selectedPriority = selectedPriorityButton.getText().toString();
        } else {
            Toast.makeText(this, "Please select a priority", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Title and Description are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create TodoModel object
        TodoModel todo = new TodoModel(0, title, description, date.getText().toString(), selectedTodotype, selectedPriority);

        // Call API to submit the todo
        PostApi api = ApiClient.getRetrofit().create(PostApi.class);
        Call<Void> call = api.createTodo(todo);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PostActivity.this, "Todo created successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity after submission
                } else {
                    Toast.makeText(PostActivity.this, "Failed to create Todo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(PostActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
