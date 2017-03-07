package com.example.lab0;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LabZeroActivity extends AppCompatActivity {

    private TextView m_nameTextView;
    private boolean m_isOldText = true;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_zero);
        m_nameTextView = (TextView) findViewById(R.id.textView);
        Button btn = (Button) findViewById(R.id.button);

        btn.setOnClickListener(onClickListener);

        SetTextViewState(m_isOldText);
    }

    public void SetTextViewState(boolean value)
    {
        m_isOldText = value;
        if(m_isOldText)
        {
            m_nameTextView.setText(R.string.textView_1_true);
        }
        else
        {
            m_nameTextView.setText(R.string.textView_1_false);
        }
    }


    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            SetTextViewState(!m_isOldText);
        }
    };
}
