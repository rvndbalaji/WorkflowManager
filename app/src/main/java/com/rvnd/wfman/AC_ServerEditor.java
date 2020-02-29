package com.rvnd.wfman;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.orhanobut.hawk.Hawk;

import java.util.HashMap;
import java.util.Map;

public class AC_ServerEditor extends AppCompatActivity implements AsyncQueryExecutor.AsyncInterface
{
    My my;
    ProgressDialog waitdialog;
    SQLServer newServer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        
        my = new My(this);
    
        Fresco.initialize(this);
    
        //Make StatusBar Transparent
        my.setStatusBarTransparent();
    
        setContentView(R.layout.activity_servereditor);
        Hawk.init(getApplicationContext()).build();
        final SimpleDraweeView backy = findViewById(R.id.backy);
        backy.setActualImageResource(R.drawable.backy);
    
        
        final CheckBox use_q_pass = findViewById(R.id.useQ);
        final LinearLayout use_pass = findViewById(R.id.use_pass);
        final EditText server_name_txt = findViewById(R.id.server_name);
        final EditText nickname_txt = findViewById(R.id.nickname);
        final RadioButton prod_btn = findViewById(R.id.prod_btn);
        final RadioButton win_btn = findViewById(R.id.win_btn);
        
        Button save_btn = findViewById(R.id.save);
        save_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                
                String server_name = server_name_txt.getText().toString().toUpperCase();
                String nickname = nickname_txt.getText().toString();
                if(nickname.equals(""))
                {
                    nickname = server_name;
                }
                boolean isprod = prod_btn.isChecked();
                boolean iswin  = win_btn.isChecked();
                boolean isQ = use_q_pass.isChecked();
                
                if(isQ)
                {
                    String pass_str =Hawk.get("USER_SECRET","");
                    char[] pass = pass_str.toCharArray();
                    newServer = new SQLServer(server_name,null,"QUAERO",Strt.USER_NAME,pass,iswin,isprod,nickname);
                    pass = new char[0];
                }
                else
                {
                    EditText un  =   findViewById(R.id.username);
                    EditText pw  =   findViewById(R.id.password);
                    String un_string = un.getText().toString();
                    char[] pw_string = pw.getText().toString().toCharArray();
                    if(un_string.equals(""))
                    {
                        new AlertDialog.Builder(AC_ServerEditor.this)
                                .setTitle("Empty field")
                                .setMessage("Empty username field.\nIf you wish to use Quaero credentials, please 'check' the box")
                                .setCancelable(false)
                                .setPositiveButton("OK",null)
                                .show();
                        return;
                    }
                    newServer = new SQLServer(server_name,null,"QUAERO",un_string,pw_string,iswin,isprod,nickname);
                }
    
                //Testing Server connection
                waitdialog = new ProgressDialog(AC_ServerEditor.this);
                waitdialog.setMessage("Testing connection & saving...");
                waitdialog.setCancelable(false);
                new AsyncQueryExecutor(300,waitdialog,AC_ServerEditor.this,false,newServer, true).execute();
            }
        });
        
        use_q_pass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    use_pass.setVisibility(View.GONE);
                }
                else
                {
                    use_pass.setVisibility(View.VISIBLE);
                }
            }
        });
        
        
    }
    
    
    
    
    @Override
    public void sendResults(SQLResult sqlResult, int caller_id)
    {
        if(caller_id==300)
        {
            if (sqlResult.error != null)
            {
                Strt.displayError(this, sqlResult.error,false);
            } else
            {
                //Prepare safe object
                SafeObject object = new SafeObject();
                object.domain = newServer.getDomainName();
                object.is_prod_server = newServer.is_prod_server;
                object.nickname = newServer.nickname;
                object.password = newServer.getPassword();
                object.serverOrIP = newServer.getServerName();
                object.username = newServer.getUsername();
                object.win_auth = newServer.isWin_auth();
                
                //Get Servers List
                Map<String,SafeObject> server_list =  Hawk.get("SERVER_LIST",new HashMap<String, SafeObject>());
                
                //Add Server to list;
                server_list.put(object.serverOrIP,object);
                //Encrypt, and store it in Hawk ondisk
                Hawk.put("SERVER_LIST",server_list);
                newServer.disconnect();
                newServer = null;
                finish();
            }
            
        }
    }
}
