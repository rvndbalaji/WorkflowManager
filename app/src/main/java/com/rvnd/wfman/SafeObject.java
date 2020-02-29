package com.rvnd.wfman;

import android.support.annotation.Keep;

@Keep
public class SafeObject
{
    public String nickname;
    public String serverOrIP;
    public String username;
    public String domain;
    public char[] password;
    public boolean win_auth = false;
    public boolean is_prod_server = false;
    
    SafeObject()
    {
    
    }
}
