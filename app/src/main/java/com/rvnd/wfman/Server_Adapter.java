
package com.rvnd.wfman;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;


/**
 * Created by aravi on 14-Feb-16.
 */

public class Server_Adapter extends RecyclerView.Adapter<Server_Adapter.ServerHolder>
{

    public static SparseBooleanArray selectedItems = new SparseBooleanArray();
    List<SafeObject> serverList;
    
    Context context;
    My my;

    public Server_Adapter(Context context, List<SafeObject> serverList)
    {
        this.serverList = serverList;
        this.context =context;
        my = new My(context);
    }


    
    View.OnClickListener clickListener;
    View.OnLongClickListener longClickListener;
    @Override
    public ServerHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.server_layout, parent, false);
        final ServerHolder viewHolder = new ServerHolder(view);

       clickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final ServerHolder viewHolder = (ServerHolder) v.getTag();
                int position = viewHolder.getAdapterPosition();
                SafeObject server = serverList.get(position);
                Intent intent = new Intent(context, AC_Monitor.class);
                intent.putExtra("SAFE_OBJECT",new Gson().toJson(server));
                context.startActivity(intent);
            }
        };

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ServerHolder holder, int position)
    {
        SafeObject sqls = serverList.get(position);
        
        holder.Nickname.setText(sqls.nickname);
        holder.Server_Name.setText(sqls.serverOrIP);
        if(sqls.is_prod_server)
        {
            holder.Server_Type.setText("PROD");
            holder.Server_Type.setTextColor(Color.parseColor("#F44336"));
        }
        else
        {
            holder.Server_Type.setText("TEST");
            holder.Server_Type.setTextColor(Color.parseColor("#2196F3"));
        }
        if(sqls.nickname.equals(sqls.serverOrIP))
        {
            holder.Server_Name.setVisibility(View.INVISIBLE);
        }
        
        holder.cardlayout.setOnClickListener(clickListener);
        holder.cardlayout.setTag(holder);
        holder.cardlayout.setSelected(selectedItems.get(position,false));
    }

    @Override
    public void onViewRecycled(ServerHolder holder)
    {
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount()
    {
        return serverList.size();
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }



    public class ServerHolder extends RecyclerView.ViewHolder {
        public TextView Nickname;
        public TextView Server_Type;
        public TextView Server_Name;
    
        //  public TextView Status;

        // protected CardView cardlayout;
        protected LinearLayout cardlayout;


        public ServerHolder(View v)
        {
            super(v);
            Nickname =  v.findViewById(R.id.nickname);
            Server_Name =  v.findViewById(R.id.server_name);
            Server_Type =  v.findViewById(R.id.type);
            //Status = v.findViewById(R.id.wf_status);
            // cardlayout = (CardView)v.findViewById(R.id.contlayout);
            cardlayout = v.findViewById(R.id.full_card);
            
        }

    }

}
