package com.rvnd.wfman;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;

public class AsyncQueryExecutor extends AsyncTask<String,Integer, SQLResult>
{
    AsyncInterface asyncallback;
    int caller_id;
    ProgressDialog prog;
    boolean quer_mode = true;
    SQLServer sqls;
    boolean resultExpected = true;
    AsyncQueryExecutor(int caller_id, ProgressDialog prog, Context context, boolean query_mode, SQLServer sqls, boolean resultExpected)
    {
        this.asyncallback = (AsyncInterface) context;
        this.prog = prog;
        this.caller_id = caller_id;
        this.quer_mode = query_mode;
        this.sqls =sqls;
        this.resultExpected = resultExpected;
    }
    
    
    @Override
    protected void onPreExecute()
    {
        if(prog!=null)
            prog.show();
    
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onPreExecute();
    }
    
    @Override
    protected SQLResult doInBackground(String... strings)
    {
        if(quer_mode)
            return sqls.execute(strings[0],resultExpected);
        else
            return sqls.connect();
        
    }
    
    
    interface AsyncInterface
    {
        void sendResults(SQLResult sqlResult,int caller_id);
    }
    
    @Override
    protected void onPostExecute(final SQLResult sqlResult)
    {
        asyncallback.sendResults(sqlResult,caller_id);
        if(prog!=null)
            prog.dismiss();
        super.onPostExecute(sqlResult);
    }
}
