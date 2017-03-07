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
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            if(m_isOldText)
            {
                m_nameTextView.setText("Колчин Илья Андреевич");
                m_isOldText = false;
            }
            else
            {
                m_nameTextView.setText("Hello World!");
                m_isOldText = true;
            }
        }
    };
}
