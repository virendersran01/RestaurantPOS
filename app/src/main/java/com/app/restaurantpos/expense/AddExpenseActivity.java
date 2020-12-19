package com.app.restaurantpos.expense;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.app.restaurantpos.R;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.utils.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class AddExpenseActivity extends BaseActivity {


    String dateTime = "";
    int mYear, mMonth, mDay, mHour, mMinute;


    EditText etxtExpenseName, etxtExpenseNote, etxtExpenseAmount, etxtDate, etxtTime;
    TextView txtAddExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.add_expense);

        etxtExpenseName = findViewById(R.id.etxt_expense_name);
        etxtExpenseNote = findViewById(R.id.etxt_expense_note);
        etxtExpenseAmount = findViewById(R.id.etxt_expense_amount);
        etxtDate = findViewById(R.id.etxt_date);
        etxtTime = findViewById(R.id.etxt_time);

        txtAddExpense = findViewById(R.id.txt_add_expense);


        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
        //H denote 24 hours and h denote 12 hour hour format
        String currentTime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date()); //HH:mm:ss a

        etxtDate.setText(currentDate);
        etxtTime.setText(currentTime);

        etxtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePicker();
            }
        });


        etxtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timePicker();
            }
        });

        txtAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String expenseName = etxtExpenseName.getText().toString();
                String expenseNote = etxtExpenseNote.getText().toString();
                String expenseAmount = etxtExpenseAmount.getText().toString();
                String expenseDate = etxtDate.getText().toString();
                String expenseTime = etxtTime.getText().toString();


                if (expenseName.isEmpty()) {
                    etxtExpenseName.setError(getString(R.string.expense_name_cannot_be_empty));
                    etxtExpenseName.requestFocus();
                } else if (expenseAmount.isEmpty()) {
                    etxtExpenseAmount.setError(getString(R.string.expense_amount_cannot_be_empty));
                    etxtExpenseAmount.requestFocus();
                } else {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddExpenseActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.addExpense(expenseName, expenseAmount, expenseNote, expenseDate, expenseTime);

                    if (check) {
                        Toasty.success(AddExpenseActivity.this, R.string.expense_successfully_added, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddExpenseActivity.this, ExpenseActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    } else {

                        Toasty.error(AddExpenseActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

                    }


                }


            }


        });


    }


    private void datePicker() {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AddExpenseActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        String fm = "" + month;
                        String fd = "" + dayOfMonth;

                        if (monthOfYear < 10) {
                            fm = "0" + month;
                        }
                        if (dayOfMonth < 10) {
                            fd = "0" + dayOfMonth;
                        }
                        dateTime = year + "-" + (fm) + "-" + fd;


                        etxtDate.setText(dateTime);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    private void timePicker() {


        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(AddExpenseActivity.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String amPm;
                        mHour = hourOfDay;
                        mMinute = minute;

                        if (mHour < 12) {
                            amPm = "AM";

                        } else {
                            amPm = "PM";
                            mHour = hourOfDay - 12;
                        }

                        etxtTime.setText(mHour + ":" + minute + " " + amPm);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }


    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
