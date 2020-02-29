package com.rvnd.wfman;

import android.util.Log;

public class QueryBuilder
{
    private String db_name;
    private String schema_name = "dbo";
    private String tbl_name;
    private String primary_col;
    private String primary_condition;
    private String req_cols = "*";
    private int limit = 0;
    private String order_by = "1";
    private boolean order_desc = false;
    
    QueryBuilder()
    {
    
    }
    
    public QueryBuilder setDatabase(String database_name)
    {
        this.db_name = database_name;
        return this;
    }
    public QueryBuilder setSchema(String schema_name)
    {
        this.schema_name = schema_name;
        return this;
    }
    
    public QueryBuilder setTable(String table_name)
    {
        this.tbl_name = table_name;
        return this;
    }
    
    public QueryBuilder setReqCols(String req_cols)
    {
        this.req_cols = req_cols;
        return this;
    }
    
    public QueryBuilder setPrimarySearchColumn(String prim_srch_col)
    {
        this.primary_col = prim_srch_col;
        return this;
    }
    public QueryBuilder setPrimarySearchCodition(String prim_srch_cond)
    {
        if(primary_col!=null)
            this.primary_condition = prim_srch_cond;
        else
        {
            Log.d("rvndb","Condition missing from where clause");
        }
        return this;
    }
    
    public QueryBuilder setOrder_by(String order_by)
    {
        this.order_by = order_by;
        return this;
    }
    
    public QueryBuilder setDesc(boolean desc)
    {
        this.order_desc = desc;
        return this;
    }
    public boolean isDescOrder()
    {
        return order_desc;
    }
    
    public QueryBuilder setLimit(int limit)
    {
        this.limit = limit;
        return this;
    }
    public String build()
    {
        
        
        StringBuilder query  = new StringBuilder("select " +  req_cols + " from ");
        if(limit>0)
        {
            query = new StringBuilder("select top " + limit  + " " + req_cols + " from ");
        }
        query.append(" ").append(db_name).append(".").append(schema_name).append(".").append(tbl_name).append(" ");
        query.append(" where ").append(primary_col).append(" like '").append(primary_condition).append("' ");
        query.append(" order by ").append(order_by).append(" ");
        if(order_desc)
            query.append(" desc ");
        else
            query.append(" asc ");
    
            //Log.e("rvndb",query.toString());
        return query.toString();
    }
    
}
