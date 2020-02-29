package com.rvnd.wfman;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class AC_Confirm extends AppCompatActivity
{
    My my;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        my = new My(this);
        my.setStatusBarTransparent();
        
        
        setContentView(R.layout.activity_confirm);
        RadioGroup modify_option = findViewById(R.id.modify_action);
    
        TextView action_text = findViewById(R.id.action_text);
        String type = getIntent().getStringExtra("type");
        if(type.equals("modify"))
        {
            modify_option.setVisibility(View.VISIBLE);
            action_text.setText("Modify Workflow Instance Status");
        }
        else
        {
            modify_option.setVisibility(View.GONE);
            action_text.setText(type.toUpperCase() + " Workflow\n" + getIntent().getStringExtra("wf_name"));
        }
    }
    
    public void proceed(View view)
    {
        String type = getIntent().getStringExtra("type");
        if(type.equals("modify"))
        {
            RadioGroup modify_option = findViewById(R.id.modify_action);
            int check_id = modify_option.getCheckedRadioButtonId();
            if (check_id == -1)
            {
                Toast.makeText(this, "Please choose an option, or cancel", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton clicked_btn = findViewById(modify_option.getCheckedRadioButtonId());
            getIntent().putExtra("modify_option", clicked_btn.getText().toString().toUpperCase());
        }
        
        setResult(RESULT_OK,getIntent());
        finish();
    }
    
    
    public void cancel(View view)
    {
        setResult(RESULT_CANCELED);
        finish();
    }
    
    @Override
    public void onBackPressed()
    {
        cancel(null);
    }
}
