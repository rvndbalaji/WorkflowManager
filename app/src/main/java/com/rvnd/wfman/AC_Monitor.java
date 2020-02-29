package com.rvnd.wfman;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AC_Monitor extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AsyncQueryExecutor.AsyncInterface
{
    
    List<WF> FLYING_WF;
    QueryBuilder builder;
    public static SQLServer current_sql;
    ProgressDialog dialog;
    My my;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
    
        my= new My(this);
        
        my.setStatusBarTransparent();
        
        setContentView(R.layout.activity_wf_monitor);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
    
        dialog = new ProgressDialog(AC_Monitor.this);
        dialog.setCancelable(false);
        
        final SimpleDraweeView backy = findViewById(R.id.backy);
        backy.setActualImageResource(R.drawable.backy);
        
        //Initialize Empty WF List
        FLYING_WF = new ArrayList<>();
        
        //Get safe object
        builder = new QueryBuilder();
    
        SafeObject safeObject = new Gson().fromJson(getIntent().getExtras().getString("SAFE_OBJECT"),SafeObject.class);
        InitConnection(safeObject);
        
        
    }
    
    
    public void keepReady()
    {
    
        //Fetch Server Name
        TextView server_name = findViewById(R.id.server_name);
        server_name.setText(current_sql.getServerName().toUpperCase());
    
        //Fetch Database Name
        TextView db_name = findViewById(R.id.db_name);
        db_name.setText(current_sql.getDatabaseName());
    
        //Fetch Primary Search Column Name
        Button primary_col = findViewById(R.id.primary_col);
        primary_col.setText("WORKFLOW_NAME");
    
        //Fetch Update User Filter
        Button updated_by = findViewById(R.id.updated_by);
        updated_by.setText("QUAERO\\balajia");
    
        //Fetch Order by Column
        Button order_by = findViewById(R.id.order_by);
        order_by.setText("1");
    
        //Prepare recycler view
        final RecyclerView wf_list = findViewById(R.id.wf_list);
        final WF_Adapter wf_adapter = new WF_Adapter(this,new ArrayList<>(FLYING_WF));
    
        final FloatingActionButton fab = findViewById(R.id.fab);
    
    
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dialog.setMessage("Fetching workflows...");
                Button primary_col = findViewById(R.id.primary_col);
                Button updated_by = findViewById(R.id.updated_by);
                Button order_by = findViewById(R.id.order_by);
                EditText condition = findViewById(R.id.cond);
                EditText limit = findViewById(R.id.limit);
                String limit_val = limit.getText().toString();
                if(limit_val.equals(""))
                    limit_val = "0";
                builder.setDatabase(current_sql.getDatabaseName())
                        .setTable("M_WORKFLOW")
                        .setReqCols("WORKFLOW_ID,WORKFLOW_NAME,WORKFLOW_DESC,ACTIVE_FLG,UPDATE_USER,UPDATE_DT")
                        .setPrimarySearchColumn(primary_col.getText().toString())
                        .setPrimarySearchCodition(condition.getText().toString())
                        .setOrder_by(order_by.getText().toString())
                        .setLimit(Integer.parseInt(limit_val));
            
                //ResultSet resultSet = current_sql.execute(builder.build());
                //Call an async task which inturn executes query asynchronously!
                String query_string =  builder.build();
                
                new AsyncQueryExecutor(100,dialog, AC_Monitor.this,true,current_sql, true).execute(query_string);
            }
        });
    
        if (wf_list != null)
        {
            wf_list.setLayoutManager(new LinearLayoutManager(this));
            wf_list.setAdapter(wf_adapter);
        }
    
        EditText condition = findViewById(R.id.cond);
        EditText limit = findViewById(R.id.limit);
        TextView.OnEditorActionListener listener = new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    fab.performClick();
                }
                return false;
            }
        };
        condition.setOnEditorActionListener(listener);
        limit.setOnEditorActionListener(listener);
    }
    
    private void InitConnection(SafeObject safeObject)
    {
        current_sql = new SQLServer(safeObject.serverOrIP,null,"QUAERO",safeObject.username,safeObject.password,safeObject.win_auth,safeObject.is_prod_server,safeObject.nickname);
        //Testing Server connection
        dialog.setMessage("Connecting to server...");
        new AsyncQueryExecutor(300,dialog,AC_Monitor.this,false,current_sql, true).execute();
    }
    
    @Override
    public void sendResults(SQLResult sqlresult,int caller_id)
    {
    
        if (sqlresult.error != null)
        {
            Strt.displayError(AC_Monitor.this, sqlresult.error,(caller_id==300 || caller_id==400));
            return;
        }
        
        if (caller_id==100)
        {
            final RecyclerView wf_list = findViewById(R.id.wf_list);
            ResultSet resultSet = sqlresult.resultSet;
    
            //Clear wf list before adding new rows
            FLYING_WF.clear();
    
            try
            {
                //Fetch rows, prepare the result and add it to recycler view
                while (resultSet != null && resultSet.next())
                {
                    String ID = resultSet.getString("WORKFLOW_ID");
                    String NAME = resultSet.getString("WORKFLOW_NAME");
                    String DESC = resultSet.getString("WORKFLOW_DESC");
                    boolean ACTIVE = resultSet.getBoolean("ACTIVE_FLG");
                    String UPDATE_USER = resultSet.getString("UPDATE_USER");
                    String UPDATE_DT = resultSet.getString("UPDATE_dt");
            
                    //String status_query = "select top 1 WORKFLOW_INSTANCE_STATUS from VW_WORKFLOW_EXECUTION_STATUS where WORKFLOW_ID = " + ID + " order by WORKFLOW_INSTANCE_STATUS desc";
                    WF wf = new WF(ID, NAME, DESC, ACTIVE, UPDATE_USER, UPDATE_DT);
            
                    //Add object to list
                    FLYING_WF.add(wf);
                }
        
                //Refresh recycler view
                if (wf_list != null)
                {
                    wf_list.setAdapter(new WF_Adapter(AC_Monitor.this, new ArrayList<>(FLYING_WF)));
                }
        
            }
            catch (NetworkOnMainThreadException nomt)
            {
                SQLError sqlError = new SQLError();
                sqlError.err_title = "Too many rows";
                sqlError.err_desc = "A large amount of data was requested that the app was unable to handle. Please filter your workflows or set a lower limit";
                sqlError.err_text = nomt.getMessage();
                Strt.displayError(AC_Monitor.this,sqlError,false);
            }
            catch (Exception e)
            {
                SQLError sqlError = new SQLError();
                sqlError.err_title = "Query failed";
                sqlError.err_desc = "Server returned the following error";
                sqlError.err_text = e.getMessage();
                Strt.displayError(AC_Monitor.this,sqlError,false);
            }
            my.HideKeyboard(this);
            //EditText condition = findViewById(R.id.cond);
            //codition.requestFocus();
        }
        
        else if(caller_id==200)
        {
            final Button prim_srch = (Button) temp_view;
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.dialog_single_choice);
         
            try
            {
                if(temp_extra!=null)
                    adapter.add(temp_extra);
                while (sqlresult.resultSet.next())
                {
                    adapter.add(sqlresult.resultSet.getString("COLUMN_NAME"));
                }
            } catch (Exception e)
            {
                new AlertDialog.Builder(this)
                        .setTitle("Column fetch failed")
                        .setMessage("Unable to fetch all columns in " +temp_table_name + "\n\nError : " + e.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("OK",null)
                        .show();
                return;
            }
            new AlertDialog.Builder(this)
                    .setTitle(temp_title)
                    .setAdapter(adapter, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            String selected_column = adapter.getItem(which);
                            prim_srch.setText(selected_column);
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
        else if (caller_id==300)
        {
           showDatabases();
        }
        else if(caller_id==400)
        {
           final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.dialog_single_choice);
           try
           {
               while (sqlresult.resultSet.next())
               {
                   adapter.add(sqlresult.resultSet.getString("NAME"));
               }
               if(adapter.isEmpty())
               {
                   new AlertDialog.Builder(this)
                           .setTitle("No Metastores")
                           .setMessage("The server contained no metastore databases (Tables ending with '_metastore' )")
                           .setCancelable(false)
                           .setPositiveButton("OK",null)
                           .show();
                   finish();
                   return;
               }
           }
            catch (Exception ex)
            {
                new AlertDialog.Builder(this)
                        .setTitle("Metastore table fetch failed")
                        .setMessage("Unable to fetch all metastores in " + current_sql.getServerName() + "\n\nError : " + ex.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("OK",null)
                        .show();
                finish();
                return;
            }
    
            new AlertDialog.Builder(this)
                    .setTitle("Select Metastore")
                    .setCancelable(false)
                    .setAdapter(adapter, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            String selected_db = adapter.getItem(which);
                            current_sql.setDatabase(selected_db);
                            keepReady();
                            dialog.dismiss();
                        }
                    })
                    .show();
           
        }
    }
    
    public void setOrder(View view)
    {
        ImageView iv = (ImageView)view ;
        boolean val = builder.isDescOrder();
        builder.setDesc(!val);
        if(val)
        {
            iv.setImageResource(R.drawable.ic_up);
        }
        else
        {
            iv.setImageResource(R.drawable.ic_down);
        }
        
    }
    public void primary_srch_col(View view)
    {
        showColumnsForTable(view,"M_WORKFLOW",null,"Select a column to search");
    }
    
    public void order_by_col(View view)
    {
        showColumnsForTable(view,"M_WORKFLOW","1","Order by...");
    }
    
    View temp_view;
    String temp_table_name;
    String temp_extra;
    String temp_title;
    
    
    public void showDatabases()
    {
        //The server has successfully made the connection, so we'll fetch the databases
        //by executing a query
        QueryBuilder builder_temp = new QueryBuilder();
        builder_temp.setDatabase("master")
                .setTable("sysdatabases")
                .setReqCols("NAME")
                .setPrimarySearchColumn("NAME")
                .setPrimarySearchCodition("%_metastore");
    
        //Call an async task which inturn executes query asynchronously!
        String query_string =  builder_temp.build();
        dialog.setMessage("Fetching databases...");
        new AsyncQueryExecutor(400,dialog, AC_Monitor.this,true,current_sql, true).execute(query_string);
    }
    
    public void showColumnsForTable(View view,String table_name,String extra,String title)
    {
        temp_extra = extra;
        temp_table_name = table_name;
        temp_title = title;
        temp_view = view;
        
        
        dialog.setMessage("Fetching columns...");
        //Fetch column names from M_WORKFLOW
        String REQ_COL = "COLUMN_NAME";
        String DB_NAME  = current_sql.getDatabaseName();
        String SCHEMA_NAME  = "INFORMATION_SCHEMA";
        String TBL_NAME  = "COLUMNS";
        new AsyncQueryExecutor(200,dialog,AC_Monitor.this,true,current_sql, true).execute("select " + REQ_COL + " from " + DB_NAME + "." + SCHEMA_NAME + "." + TBL_NAME + " where TABLE_NAME='" + table_name + "'");
    }
    
    boolean quit = false;
    @Override
    public void onBackPressed()
    {
        if(quit)
        {
            super.onBackPressed();
            return;
        }
        
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        } else
        {
            //Disconnect from server on confirmtion
            new AlertDialog.Builder(this)
            .setTitle("Disconnect?")
            .setMessage("Do you wish to disconnect from this server and go to the main screen?")
            .setCancelable(false)
            .setPositiveButton("DISCONNECT", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    quit = true;
                    current_sql.disconnect();
                    current_sql = null;
                    onBackPressed();
                }
            }).setNegativeButton("Cancel",null)
            .show();
            
            
        }
    }
    
    
    public void showMenu(View view)
    {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.openDrawer(Gravity.START);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wf_monitor, menu);
        return true;
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
    
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        /*
        if (id == R.id.nav_home)
        {
            // Handle the camera action
        } else if (id == R.id.nav_gallery)
        {
        
        } else if (id == R.id.nav_slideshow)
        {
        
        } else if (id == R.id.nav_tools)
        {
        
        } else if (id == R.id.nav_share)
        {
        
        } else if (id == R.id.nav_send)
        {
        
        }
        */
        
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    
  
}
