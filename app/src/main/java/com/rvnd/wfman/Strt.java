package com.rvnd.wfman;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.orhanobut.hawk.Hawk;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Strt extends AppCompatActivity implements AsyncQueryExecutor.AsyncInterface
{
    
    My my;
    public static String USER_NAME;
    public char[] PASSWORD;
    
    Button sign_in_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        my = new My(this);
        Hawk.init(getApplicationContext()).build();
        Fresco.initialize(this);
        
        //Make StatusBar Transparent
        my.setStatusBarTransparent();
        
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        
        final SimpleDraweeView backy = findViewById(R.id.backy);
        backy.setActualImageResource(R.drawable.backy);
    
        EditText un = findViewById(R.id.username);
        EditText pw = findViewById(R.id.password);
        USER_NAME = Hawk.get("USER_NAME","");
        un.setText(USER_NAME);
        String pass = Hawk.get("USER_SECRET","");
        PASSWORD = pass.toCharArray();
        pw.setText(String.valueOf(PASSWORD));
        sign_in_btn = findViewById(R.id.signin);
        
        sign_in_btn.performClick();
    }
    public boolean isVPNActive()
    {
        List<String> networkList = new ArrayList<>();
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp())
                    networkList.add(networkInterface.getName());
            }
        } catch (Exception ex) {
            Log.e("rvndb","Unable to receive network list");
        }
        
        return networkList.contains("tun0");
    }
    
   public void perform_login()
   {
       Hawk.put("USER_SECRET",String.valueOf(PASSWORD));
       Hawk.put("USER_NAME",USER_NAME);
       PASSWORD = new char[0];
       
       if(waitdialog!=null)
           waitdialog.dismiss();
       
       
       sign_in_btn.setEnabled(false);
       sign_in_btn.setText("Signing in...");
    
       Intent intent = new Intent(Strt.this, AC_Servers.class);
       startActivity(intent);
       finish();
       
   }
    
    @Override
    protected void onStop()
    {
        /*
        String vpn_conn_string = "anyconnect://disconnect";
        Intent login = new Intent(Intent.ACTION_VIEW);
        login.setData(Uri.parse(vpn_conn_string));
        startActivity(login);
        */
        super.onStop();
    }
    
    ProgressDialog waitdialog;
    
    public void sign_in(View view)
   {
       
       EditText un = findViewById(R.id.username);
       EditText pw = findViewById(R.id.password);
       
       USER_NAME = un.getText().toString();
       PASSWORD = pw.getText().toString().toCharArray();
       
       if(PASSWORD.length==0)
       {
           Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
           return;
       }
       
       sign_in_btn.setEnabled(false);
       sign_in_btn.setText("VPN Connect...");
       
       if(!isVPNActive())
       {
           waitdialog = new ProgressDialog(this);
           waitdialog.setMessage("Please wait while we connect you to VPN...");
           waitdialog.setCancelable(false);
           waitdialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener()
           {
               @Override
               public void onClick(DialogInterface dialog, int which)
               {
                   VPNLoginMonitor = false;
                   dialog.dismiss();
                   sign_in_btn.setEnabled(true);
                   sign_in_btn.setText("Sign in");
               }
           });
           waitdialog.show();
           
           String vpn_conn_string = "anyconnect://connect/?name=Quaero3&host=connect.quaero.com/PC_Clients_AC&prefill_username=" + USER_NAME + "&prefill_password=" + String.valueOf(PASSWORD);
           Intent login = new Intent(Intent.ACTION_VIEW);
           login.setData(Uri.parse(vpn_conn_string));
           startActivity(login);
           checkVPNLoop();
       }
       else
       {
           perform_login();
       }
   }
   
   volatile boolean VPNLoginMonitor = false;
    private void checkVPNLoop()
    {
        VPNLoginMonitor = true;
        Thread  T = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while(VPNLoginMonitor)
                {
                    boolean res = isVPNActive();
                    try
                    {
                        Thread.sleep(500);
                    } catch (InterruptedException e)
                    {
                       Log.e("rvndblogin","Sleep interrupted");
                    }
                    
                    if(res)
                    {
                        perform_login();
                        break;
                    }
                }
            }
        });
        
        T.start();
        
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    
    public static void displayError(final Context context, SQLError sqlError, final boolean closeActivity)
    {
        new AlertDialog.Builder(context)
                .setTitle(sqlError.err_title)
                .setMessage(Html.fromHtml(sqlError.err_desc + "<br><br>" + "<font color='#F44336'>Error : " + sqlError.err_text + "</font>"))
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(closeActivity)
                        {
                            ((Activity)context).finish();
                        }
                        else
                        {
                            dialog.dismiss();
                        }
                    }
                })
                .show();
        
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void sendResults(SQLResult sqlResult, int caller_id)
    {
        if(caller_id==300)
        {
            if (sqlResult.error != null)
            {
                displayError(this, sqlResult.error,false);
            } else
            {
                Intent intent = new Intent(Strt.this, AC_Monitor.class);
                startActivity(intent);
                finish();
            }
            
        }
    }
}
