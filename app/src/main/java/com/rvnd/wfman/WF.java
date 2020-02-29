package com.rvnd.wfman;

import android.support.annotation.Keep;

@Keep
public class WF
{
   public String wf_id;
   public String wf_name;
   public String wf_desc;
   public boolean active = false;
   public String update_user;
   public String update_dt;
    
    public WF()
    {
    
    }
    
    WF(String wf_id,String wf_name,String wf_desc,boolean active,String update_user,String update_dt)
    {
        this.wf_id = wf_id;
        this.wf_name = wf_name;
        this.wf_desc = wf_desc;
        this.active = active;
        this.update_user = update_user;
        this.update_dt = update_dt;
    }

  }