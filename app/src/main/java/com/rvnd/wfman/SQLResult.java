package com.rvnd.wfman;

import java.sql.ResultSet;

public class SQLResult
{
    public ResultSet resultSet;
    public SQLError error;
    
    SQLResult(ResultSet resultSet,SQLError error)
    {
        this.resultSet = resultSet;
        this.error = error;
    }
    
    SQLResult()
    {
    
    }
}