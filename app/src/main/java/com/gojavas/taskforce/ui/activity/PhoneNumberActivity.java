package com.gojavas.taskforce.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.gojavas.taskforce.R;

/**
 * Created by GJS280 on 10/4/2015.
 */
public class PhoneNumberActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mPhoneNumberEditText;
    private Button mDoneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);

        registerViews();
        registerListeners();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.phone_number_done_button:
                doneButtonClicked();
                break;
            default:
                break;
        }
    }

    /**
     * Register all the views
     */
    private void registerViews() {
        mPhoneNumberEditText = (EditText) findViewById(R.id.phone_number_edittext);
        mDoneButton = (Button) findViewById(R.id.phone_number_done_button);
    }

    /**
     * Register listener on all the views
     */
    private void registerListeners() {
        mDoneButton.setOnClickListener(this);
    }

    /**
     * Done button is clicked
     */
    private void doneButtonClicked() {
        Intent validationIntent = new Intent(PhoneNumberActivity.this, PhoneNumberValidation.class);
        startActivity(validationIntent);
    }
}
