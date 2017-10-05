package com.example.lab0;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LabZeroActivity extends AppCompatActivity {

    private TextView mNameTextView;
    private boolean mIsOldText = true;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_zero);
        mNameTextView = (TextView) findViewById(R.id.textView);
        Button btn = (Button) findViewById(R.id.button);

        btn.setOnClickListener(onClickListener);
    }

    private void switchTextViewState()
    {
        mIsOldText = !mIsOldText;
        if(mIsOldText)
        {
            mNameTextView.setText(R.string.authors_name_label_text);
        }
        else
        {
            mNameTextView.setText(R.string.hello_world_label_text);
        }
    }


    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            switchTextViewState();
        }
    };
}
