package com.rvnd.wfman;

public class SQLError
{
    public String err_title;
    public String err_desc;
    public String err_text;
    
    
    SQLError()
    {
    
    }
    SQLError(String err_title,String err_desc,String err_text)
    {
        this.err_title = err_title;
        this.err_desc = err_desc;
        this.err_text = err_text;
    }
}
