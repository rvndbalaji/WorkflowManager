package com.rvnd.wfman;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AC_Servers extends AppCompatActivity
{
    List<SafeObject> FLYING_SERVERS;
    
    My my;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    
        my = new My(this);
    
        Fresco.initialize(this);
        Hawk.init(getApplicationContext()).build();
    
        //Make StatusBar Transparent
        my.setStatusBarTransparent();
        
        setContentView(R.layout.activity_servers);
        
        final SimpleDraweeView backy = findViewById(R.id.backy);
        backy.setActualImageResource(R.drawable.backy);
        
    
        FloatingActionButton fab = findViewById(R.id.add_server);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(AC_Servers.this,AC_ServerEditor.class);
                startActivity(intent);
            }
        });
    
    
        //Prepare recycler view
        final RecyclerView server_list = findViewById(R.id.server_list);
        server_list.setLayoutManager(new LinearLayoutManager(this));
        //Fetch all saved_serverlist
        loadAllServers();
        server_list.setAdapter(new Server_Adapter(this,new ArrayList<>(FLYING_SERVERS)));
    }
    
    public void loadAllServers()
    {
        
        FLYING_SERVERS = new ArrayList<>();
        //Get Servers List
        HashMap<String,SafeObject> server_list =  Hawk.get("SERVER_LIST",new HashMap<String, SafeObject>());
        if(server_list!=null && !server_list.isEmpty())
            FLYING_SERVERS.addAll(server_list.values());
    }
    
}
