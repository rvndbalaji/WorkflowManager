
package com.rvnd.wfman;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


/**
 * Created by aravi on 14-Feb-16.
 */

public class Pair_Adapter extends RecyclerView.Adapter<Pair_Adapter.EventHolder>
{

    public static SparseBooleanArray selectedItems = new SparseBooleanArray();
    List<KVPair> KVPair_list;
    public KVPair flying_KVPair;
    Context context;
    My my;

    public Pair_Adapter(Context context, List<KVPair> KVPair_list)
    {
        this.KVPair_list = KVPair_list;
        this.context =context;
        my = new My(context);
    }


    
    View.OnClickListener clickListener;
    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pair_layout, parent, false);
        final EventHolder viewHolder = new EventHolder(view);

       clickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final EventHolder viewHolder = (EventHolder) v.getTag();
                int position = viewHolder.getAdapterPosition();
                
                flying_KVPair = KVPair_list.get(position);
    
                Toast.makeText(context, flying_KVPair.value, Toast.LENGTH_SHORT).show();
            }
        };

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final EventHolder holder, int position)
    {
        KVPair KVPair = KVPair_list.get(position);
        holder.Value.setText(KVPair.value);
        holder.Key.setText(KVPair.key);
        
        holder.cardlayout.setOnClickListener(clickListener);
        holder.cardlayout.setTag(holder);
        holder.cardlayout.setSelected(selectedItems.get(position,false));
    }

    @Override
    public void onViewRecycled(EventHolder holder)
    {
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount()
    {
        return KVPair_list.size();
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

 

    public class EventHolder extends RecyclerView.ViewHolder {
        public TextView Value;
        public TextView Key;
      //  public TextView Status;

        // protected CardView cardlayout;
        protected LinearLayout cardlayout;


        public EventHolder(View v)
        {
            super(v);
            Value =  v.findViewById(R.id.pair_value);
            Key =  v.findViewById(R.id.pair_key);
            //Status = v.findViewById(R.id.wf_status);
            // cardlayout = (CardView)v.findViewById(R.id.contlayout);
            cardlayout = v.findViewById(R.id.full_card);
            
        }

    }

}
