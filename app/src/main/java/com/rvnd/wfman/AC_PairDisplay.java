package com.rvnd.wfman;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class AC_PairDisplay extends AppCompatActivity implements AsyncQueryExecutor.AsyncInterface
{
    ProgressDialog dialog;
    My my;
    String wfi_id;
    String event_gid;
    boolean precompile = false;
    List<KVPair> FLYING_KVPair;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        my = new My(this);
        my.setStatusBarTransparent();
        setContentView(R.layout.activity_pair_display);
    
        final SimpleDraweeView backy = findViewById(R.id.backy);
        backy.setActualImageResource(R.drawable.backy);
    
        wfi_id = getIntent().getStringExtra("wfi_id");
        event_gid = getIntent().getStringExtra("event_gid");
        precompile = (getIntent().getStringExtra("type").equals("precompile"));
        
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        
        TextView title = findViewById(R.id.title);
        title.setText((precompile)?"Precompile":"Event Log");
        
        TextView search_bar = findViewById(R.id.srch);
        search_bar.setHint((precompile)?"Search by PARAM_NAME":"Search by EVENT_MSG");
        
        
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!precompile)
                    fetchEventLog();
                else
                    fetchPrecompile();
            }
        });
        fab.performClick();
        
        FLYING_KVPair = new ArrayList<>();
        
    }
    
    private void fetchEventLog()
    {
        //select * from M_TRACK_EVENT_LOG where EVENT_GROUP_ID =17233
        QueryBuilder builder = new QueryBuilder()
                .setDatabase(AC_Monitor.current_sql.getDatabaseName())
                .setTable("M_TRACK_EVENT_LOG")
                .setReqCols("UPDATE_DT,EVENT_MSG")
                .setPrimarySearchColumn("EVENT_GROUP_ID")
                .setPrimarySearchCodition(event_gid)
                .setOrder_by("EVENT_ID")
                .setDesc(true);
    
        dialog.setMessage("Refreshing...");
    
        //Call an async task which inturn executes query asynchronously!
        dialog.setMessage("Fetching event logs...\n\n(This might take a few seconds)");
        String query_string =  builder.build();
        new AsyncQueryExecutor(100,dialog, AC_PairDisplay.this,true,AC_Monitor.current_sql, true).execute(query_string);
    
    }
    
    private void fetchPrecompile()
    {
        //Call an async task which inturn executes query asynchronously!
        dialog.setMessage("Precompiling...\n\n(This might take a few seconds)");
        String query_string =  "exec " + AC_Monitor.current_sql.getDatabaseName() + ".dbo." + "USP_PRECOMPILE_WORKFLOW_PACKAGE_MANIFEST " + wfi_id;
        new AsyncQueryExecutor(200,dialog, AC_PairDisplay.this,true,AC_Monitor.current_sql, true).execute(query_string);
    }
    
    
    
    @Override
    public void sendResults(SQLResult sqlresult, int caller_id)
    {
        
        if (sqlresult != null && sqlresult.error != null)
        {
            Strt.displayError(this, sqlresult.error,true);
            return;
        }
    
        
        if(caller_id==100)
        {
            
            FLYING_KVPair.clear();
            try
            {
        
                while (sqlresult != null && sqlresult.resultSet.next())
                {
                    String KEY;
                    String VALUE;
                    if (precompile)
                    {
                        KEY = sqlresult.resultSet.getString("PARAM_NAME");
                        VALUE = sqlresult.resultSet.getString("PARAM_VALUE");
                    } else
                    {
                        VALUE = sqlresult.resultSet.getString("EVENT_MSG");
                        KEY = sqlresult.resultSet.getString("UPDATE_DT");
                    }
    
    
                    KVPair KVPair = new KVPair(VALUE,KEY);
                    FLYING_KVPair.add(KVPair);
                }
                
                if(FLYING_KVPair.isEmpty())
                {
                    String title;
                    String msg;
                    if (precompile)
                    {
                        title = "Invalid Instance";
                        msg = "The precompile performed on this instance returned no param names or values. Its possible that the workflow never ran, or an invalid instance was supplied";
                    } else
                    {
                        title = "No events recorded";
                        msg = "This workflow instance does not have any event logs associated with it. Make sure the workflow has run atleast once";
                    }
    
                    new AlertDialog.Builder(this)
                            .setTitle(title)
                            .setMessage(msg)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                    finish();
                                }
                            })
                            .show();
                    
                }
                else
                {
                    //Prepare recycler view
                    final RecyclerView event_list = findViewById(R.id.pair_list);
                    event_list.setLayoutManager(new LinearLayoutManager(this));
                    event_list.setAdapter(new Pair_Adapter(this, new ArrayList<>(FLYING_KVPair)));
                    
                    //Prepare search bar
                    EditText srch = findViewById(R.id.srch);
                    srch.addTextChangedListener(new TextWatcher()
                    {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after)
                        {
        
                        }
    
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count)
                        {
                            String srch_string = s.toString().toLowerCase();
                            if(srch_string.equals(""))
                            {
                                //No search, display all results
                                event_list.setAdapter(new Pair_Adapter(AC_PairDisplay.this,new ArrayList<>(FLYING_KVPair)));
                                return;
                            }
                            List<KVPair> temp_kv = new ArrayList<>();
                            for ( KVPair item : FLYING_KVPair )
                            {
                                //If precompile, saerch against key. If event log, search against value
                                String match_against = ((precompile)?item.key:item.value).toLowerCase();
                                
                                if(match_against.contains(srch_string))
                                    temp_kv.add(item);
                            }
                            event_list.setAdapter(new Pair_Adapter(AC_PairDisplay.this,new ArrayList<>(temp_kv)));
                        }
    
                        @Override
                        public void afterTextChanged(Editable s)
                        {
        
                        }
                    });
                }
        
            }
            catch(Exception ex)
            {
                Log.e("rvndb", ex.toString());
            }
        }
    
    
        if(caller_id==200)
        {
            //To perform precompiling
            try
            {
                String PRECOMPILED_TEMP_TABLE_NAME = "";
                while (sqlresult != null && sqlresult.resultSet.next())
                {
                    PRECOMPILED_TEMP_TABLE_NAME = sqlresult.resultSet.getString("PRECOMPILED_TEMP_TABLE_NAME");
                }
                
                
                //Now we have the precompiled table name, we shall perform a SELECT on this and fetch all param names and values
                if(!PRECOMPILED_TEMP_TABLE_NAME.equals(""))
                {
                    dialog.setMessage("Displaying results...\n\n(This might take a few seconds)");
                    String query_string = "select PARAM_NAME,PARAM_VALUE from " + PRECOMPILED_TEMP_TABLE_NAME + " order by WORKFLOW_PACKAGE_MAP_ID desc";
                    new AsyncQueryExecutor(100, dialog, AC_PairDisplay.this, true, AC_Monitor.current_sql, true).execute(query_string);
                }
                else
                {
                    new AlertDialog.Builder(this)
                            .setTitle("Something went wrong!")
                            .setMessage("This workflow instance did not return any precompiled temp table name :\\")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                    finish();
                                }
                            })
                            .show();
                }
    
    
            }
            catch (NetworkOnMainThreadException nomt)
            {
                SQLError sqlError = new SQLError();
                sqlError.err_title = "Whoa! Too much data";
                sqlError.err_desc = "A large amount of data was requested that the app was unable to handle. This should not really happen and is an edge case. Please contact the app developer immediately";
                sqlError.err_text = nomt.getMessage();
                Strt.displayError(AC_PairDisplay.this,sqlError,false);
            }
            catch(Exception ex)
            {
                Log.e("rvndb", ex.toString());
            }
            
        }
    }
}
