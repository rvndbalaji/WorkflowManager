
package com.rvnd.wfman;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;


/**
 * Created by aravi on 14-Feb-16.
 */

public class WF_Adapter extends RecyclerView.Adapter<WF_Adapter.WFHolder>
{

    public static SparseBooleanArray selectedItems = new SparseBooleanArray();
    List<WF> wflist;
    public static WF flying_wf;
    Context context;
    My my;

    public WF_Adapter(Context context, List<WF> wflist)
    {
        this.wflist = wflist;
        this.context =context;
        my = new My(context);
    }


    
    View.OnClickListener clickListener;
    @Override
    public WFHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wf_layout, parent, false);
        final WFHolder viewHolder = new WFHolder(view);

       clickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final WFHolder viewHolder = (WFHolder) v.getTag();
                int position = viewHolder.getAdapterPosition();
                LinearLayout ll = (LinearLayout) view;
                ll.setBackground(context.getResources().getDrawable(R.drawable.gray_round,context.getTheme()));
                flying_wf = wflist.get(position);
                Intent intent = new Intent(context, AC_Viewer.class);
                context.startActivity(intent);
                
            }
        };

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final WFHolder holder, int position)
    {
        WF wf = wflist.get(position);
        holder.Name.setText(wf.wf_name);
     //   holder.Status.setText(wf.wf_status);
        
        holder.cardlayout.setOnClickListener(clickListener);
        holder.cardlayout.setTag(holder);
        holder.cardlayout.setSelected(selectedItems.get(position,false));
    }

    @Override
    public void onViewRecycled(WFHolder holder)
    {
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount()
    {
        return wflist.size();
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }



    public class WFHolder extends RecyclerView.ViewHolder {
        public TextView Name;
      //  public TextView Status;

        // protected CardView cardlayout;
        protected LinearLayout cardlayout;


        public WFHolder(View v)
        {
            super(v);
            Name =  v.findViewById(R.id.wf_name);
            //Status = v.findViewById(R.id.wf_status);
            // cardlayout = (CardView)v.findViewById(R.id.contlayout);
            cardlayout = (LinearLayout)v.findViewById(R.id.full_card);
            
        }

    }

}
