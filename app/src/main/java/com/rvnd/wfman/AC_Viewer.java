package com.rvnd.wfman;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

public class AC_Viewer extends AppCompatActivity implements AsyncQueryExecutor.AsyncInterface
{
    My my;
    WF workflow;
    ProgressDialog dialog;
    String wfi_id;
    String event_gid;
    boolean child_view_open = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        my = new My(this);
        my.setStatusBarTransparent();
        setContentView(R.layout.activity_workflow_viewer);
    
        final SimpleDraweeView backy = findViewById(R.id.backy);
        backy.setActualImageResource(R.drawable.backy);
        workflow = WF_Adapter.flying_wf;
    
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                fetchWorkflowDetails();
            }
        });
        dialog = new ProgressDialog(AC_Viewer.this);
        dialog.setCancelable(false);
        fetchWorkflowStatus();
    }
    
    private void fetchWorkflowDetails()
    {
        QueryBuilder builder = new QueryBuilder()
                .setDatabase(AC_Monitor.current_sql.getDatabaseName())
                .setTable("M_WORKFLOW")
                .setReqCols("WORKFLOW_ID,WORKFLOW_NAME,WORKFLOW_DESC,ACTIVE_FLG,UPDATE_USER,UPDATE_DT")
                .setPrimarySearchColumn("WORKFLOW_ID")
                .setPrimarySearchCodition(workflow.wf_id);
        
        dialog.setMessage("Refreshing...");
        
        
        //Call an async task which inturn executes query asynchronously!
        String query_string =  builder.build();
        new AsyncQueryExecutor(200,dialog, this,true,AC_Monitor.current_sql, true).execute(query_string);
    }
    
    public void viewEventLog(View view)
    {
        Intent intent = new Intent(AC_Viewer.this, AC_PairDisplay.class);
        if(wfi_id==null || event_gid==null || wfi_id.equals("") || event_gid.equals(""))
        {
            new AlertDialog.Builder(this)
                    .setTitle("No events recorded")
                    .setMessage("This workflow instance does not have any event logs associated with it. Make sure the workflow has run atleast once")
                    .setPositiveButton("OK",null)
                    .show();
        }
        else
        {
            intent.putExtra("wfi_id", wfi_id);
            intent.putExtra("event_gid", event_gid);
            intent.putExtra("type","event");
            startActivity(intent);
        }
    }
    
    public void viewConfig(View view)
    {
        new AlertDialog.Builder(this)
                .setTitle("Coming soon")
                .setMessage("This feature is currently unavailable and will be coming soon")
                .setPositiveButton("OK",null)
                .show();
    }
    
    
    public void modifyStatus(View view)
    {
        Intent intent = new Intent(AC_Viewer.this,AC_Confirm.class);
        intent.putExtra("type","modify");
        startActivityForResult(intent,500);
    }
    
    public void activateWF(View view)
    {
        Intent intent = new Intent(AC_Viewer.this,AC_Confirm.class);
        intent.putExtra("type",workflow.active?"Deactivate":"Activate");
        intent.putExtra("wf_name",workflow.wf_name);
        startActivityForResult(intent,500);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        
        if(resultCode==RESULT_OK)
        {
            if(requestCode==500)
            {
                String type = data.getStringExtra("type");
                String query_string;
                if(type.equals("modify"))
                {
                    //Modify Workflow Instance Status
                    String chosen_option = data.getStringExtra("modify_option");
                    dialog.setMessage("Modifying workflow status to : " + chosen_option);
                    query_string = "exec " + AC_Monitor.current_sql.getDatabaseName() + ".dbo." + "USP_MODIFY_WORKFLOW_INSTANCE_STATUS " + wfi_id + ",'" + chosen_option + "'";
                }
                else
                {
                    //Activate\Deactivate Workflow
                    if(type.equals("Activate"))
                    {
                        dialog.setMessage("Activating Workflow...");
                        query_string = "exec " + AC_Monitor.current_sql.getDatabaseName() + ".dbo." + "USP_ACTIVATE_WORKFLOW " + workflow.wf_id;
                    }
                    else
                    {
                        dialog.setMessage("Deactivating Workflow...");
                        query_string = "exec " + AC_Monitor.current_sql.getDatabaseName() + ".dbo." + "USP_DEACTIVATE_WORKFLOW " + workflow.wf_id;
                    }
                    
                }
                new AsyncQueryExecutor(0,dialog, this,true,AC_Monitor.current_sql, false).execute(query_string);
            }
        }
    }
    
    public void viewPrecompile(View view)
    {
        Intent intent = new Intent(AC_Viewer.this, AC_PairDisplay.class);
        if(wfi_id==null || event_gid==null || wfi_id.equals("") || event_gid.equals(""))
        {
            new AlertDialog.Builder(this)
                    .setTitle("Precompile Unavailable")
                    .setMessage("This workflow instance does not have precompile package manifest. Make sure the workflow has run atleast once")
                    .setPositiveButton("OK",null)
                    .show();
        }
        else
        {
            intent.putExtra("wfi_id", wfi_id);
            intent.putExtra("event_gid", event_gid);
            intent.putExtra("type","precompile");
            startActivity(intent);
        }
    }
    
    
    private void fetchWorkflowStatus()
    {
        TextView wf_id = findViewById(R.id.wf_id);
        TextView wf_name = findViewById(R.id.wf_name);
        TextView wf_updated_on = findViewById(R.id.wf_updated_on);
        TextView wf_updated_by = findViewById(R.id.wf_updated_by);
        TextView active = findViewById(R.id.wf_active);    
        
        Button button = findViewById(R.id.activate_btn);
        
        
        wf_id.setText(workflow.wf_id);
        wf_name.setText(workflow.wf_name);
        wf_updated_by.setText(workflow.update_user);
        wf_updated_on.setText(workflow.update_dt);
        
        
        active.setText(workflow.active?"ACTIVE":"INACTIVE");
        button.setText(workflow.active?"DEACTIVATE":"ACTIVATE");
        if(workflow.active)
        {
            button.setBackground(getResources().getDrawable(R.drawable.red_round,getTheme()));
        }
        else
        {
            button.setBackground(getResources().getDrawable(R.drawable.green_round,getTheme()));
        }
        
        String execution_status_query = new QueryBuilder()
                .setDatabase(AC_Monitor.current_sql.getDatabaseName())
                .setTable("VW_WORKFLOW_EXECUTION_STATUS")
                .setReqCols("WORKFLOW_INSTANCE_ID,WORKFLOW_TYPE,START_DT,END_DT,WORKFLOW_INSTANCE_STATUS,EVENT_GROUP_ID")
                .setLimit(1)
                .setPrimarySearchColumn("WORKFLOW_ID")
                .setPrimarySearchCodition(workflow.wf_id)
                .setOrder_by("WORKFLOW_INSTANCE_ID")
                .setDesc(true)
                .build();
    
                dialog.setMessage("Fetching workflow details...");
        try
        {
             new AsyncQueryExecutor(100,dialog,this,true,AC_Monitor.current_sql, true).execute(execution_status_query);
        } catch (Exception e){Log.e("rvndb","error querying status");}
       
    }
    
    
  
    @Override
    public void sendResults(SQLResult sqlresult,int caller_id)
    {
    
        if (sqlresult != null && sqlresult.error != null)
        {
            Strt.displayError(this, sqlresult.error,false);
            return;
        }
        if(caller_id==0)
        {
            FloatingActionButton fab = findViewById(R.id.fab);
            fab.performClick();
            return;
        }
        
        if(caller_id==100)
        {
            TextView wf_status = findViewById(R.id.wf_status);
            TextView wf_inst = findViewById(R.id.wf_inst);
            TextView wf_started = findViewById(R.id.wf_started);
            TextView wf_ended = findViewById(R.id.wf_ended);
            TextView wf_type = findViewById(R.id.wf_type);
    
            //Fetch rows, prepare the result and add it to recycler view
            try
            {
                while (sqlresult != null && sqlresult.resultSet.next())
                {
                    String STATUS = sqlresult.resultSet.getString("WORKFLOW_INSTANCE_STATUS");
                    wfi_id = sqlresult.resultSet.getString("WORKFLOW_INSTANCE_ID");
                    String STARTED = sqlresult.resultSet.getString("START_DT");
                    String ENDED = sqlresult.resultSet.getString("END_DT");
                    String TYPE = sqlresult.resultSet.getString("WORKFLOW_TYPE");
                    event_gid = sqlresult.resultSet.getString("EVENT_GROUP_ID");
                    
    
                    String color = "#000000";
                    
                    if(STATUS.startsWith("COMPLETE"))
                        color = "#4CAF50";
                    
                    else if(STATUS.startsWith("FAILED"))
                        color ="#F44336";
                        
                    wf_status.setText(Html.fromHtml("<b><font color='" + color + "'>" + STATUS + "</font></b>"));
                    
                    wf_inst.setText(wfi_id);
                    wf_started.setText(STARTED);
                    wf_ended.setText(ENDED);
                    wf_type.setText(TYPE);
                }
            } catch (Exception ex)
            {
                Log.e("rvndb", ex.toString());
            }
        }
        else if(caller_id==200)
        {
            //Fetch rows, prepare the result and add it to recycler view
            try
            {
    
                while (sqlresult != null && sqlresult.resultSet.next())
                {
                    String ID = sqlresult.resultSet.getString("WORKFLOW_ID");
                    String NAME = sqlresult.resultSet.getString("WORKFLOW_NAME");
                    String DESC = sqlresult.resultSet.getString("WORKFLOW_DESC");
                    boolean ACTIVE = sqlresult.resultSet.getBoolean("ACTIVE_FLG");
                    String UPDATE_USER = sqlresult.resultSet.getString("UPDATE_USER");
                    String UPDATE_DT = sqlresult.resultSet.getString("UPDATE_dt");
    
                    workflow = new WF(ID, NAME, DESC, ACTIVE, UPDATE_USER, UPDATE_DT);
                    fetchWorkflowStatus();
                }
                
            }
            catch(Exception ex)
            {
                Log.e("rvndb", ex.toString());   
            }
            //String status_query = "select top 1 WORKFLOW_INSTANCE_STATUS from VW_WORKFLOW_EXECUTION_STATUS where WORKFLOW_ID = " + ID + " order by WORKFLOW_INSTANCE_STATUS desc";
            
        }
    }
}
