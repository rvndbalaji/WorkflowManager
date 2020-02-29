
package com.rvnd.wfman;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SQLServer
{
    private String LOG = "sqls";
    public String nickname;
    private String serverOrIP;
    private String dbname;
    private String username;
    private String domain;
    private char[] password;
    private boolean win_auth = false;
    private Connection connection;
    private String connectionString;
    public boolean is_prod_server = false;
    
    SQLServer()
    {
    
    }
    
    SQLServer(String serverOrIP, String dbname, String domain, String username, char[] password, boolean win_auth, boolean is_prod_server,String nickname)
    {
        this.serverOrIP = serverOrIP;
        this.dbname = dbname;
        this.domain = domain;
        this.username = username;
        this.password = password;
        this.win_auth = win_auth;
        this.is_prod_server = is_prod_server;
        this.nickname = nickname;
        
    }
    SQLResult connect()
    {
        SQLResult sqlres = new SQLResult();
        SQLError sqlError;
        
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            
            if (dbname==null || dbname.equals(""))
            {
                dbname = "master";
            }
            connectionString = "jdbc:jtds:sqlserver://" + serverOrIP + ";databaseName=" + dbname  + ";domain=" +domain +
                    ";user=" + username + ";password=" + String.valueOf(password);
                    
            if(win_auth)
            {
                connectionString = connectionString + ";integratedSecurity=true";
            }
            
            connection = DriverManager.getConnection(connectionString);
            return new SQLResult(null,null);
            
        } catch (Exception e)
        {
            sqlError = new SQLError();
            sqlError.err_title = "Connection failed";
            sqlError.err_desc = "Please check if you have<br><br>> connected to VPN<br>> entered the correct username/password/server<br>> authenticated properly (win/sql auth)";
            sqlError.err_text = e.getMessage();
        }
        sqlres.error = sqlError;
        return sqlres;
    }
    
    public void setPassword(char[] password)
    {
        this.password = password;
    }
    
    SQLResult execute(String query,boolean resultExpected)
    {
        SQLError sqlError;
        try
        {
            Statement statement = connection.createStatement();
            ResultSet resultSet;
            if(resultExpected)
            {
                resultSet = statement.executeQuery(query);
                return new SQLResult(resultSet, null);
            }
            else
            {
                statement.executeUpdate(query);
                return new SQLResult(null, null);
            }
            
        }
        catch (Exception ex)
        {
            sqlError = new SQLError();
            sqlError.err_title = "Query failed";
            sqlError.err_desc = "Server returned the following error";
            sqlError.err_text = ex.getMessage();
        }
        
        return new SQLResult(null,sqlError);
    }
    
    public void disconnect()
    {
        try
        {
            connection.close();
        } catch (Exception e)
        {
            Log.d("sqlserver","Unable to terminate SQL Server connection\nError : " + e.getMessage());
        }
    }
    
    public Connection getConnection()
    {
        return connection;
    }
    
    public String getDomainName()
    {
        return domain;
    }
    
    public void setDomainName(String domain)
    {
        this.domain = domain;
    }
    public String getServerName()
    {
        return serverOrIP;
    }
    public void setServerName(String serverOrIP)
    {
        this.serverOrIP = serverOrIP;
    }
    
    public String getDatabaseName()
    {
        return dbname;
    }
    
    public void setDatabase(String database_name)
    {
        this.dbname = database_name;
    }
    
    public char[] getPassword()
    {
        return password;
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public String getConnectionString()
    {
        return connectionString;
    }
    
    
    
    public boolean isWin_auth()
    {
        return win_auth;
    }
}
