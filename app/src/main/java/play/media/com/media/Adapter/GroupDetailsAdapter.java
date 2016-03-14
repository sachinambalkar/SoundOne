package play.media.com.media.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import play.media.com.media.Activities.ClientActivity;
import play.media.com.media.Animation.AnimationsUtils;
import play.media.com.media.R;
import play.media.com.media.ViewHolderGroup;
import play.media.com.media.model.GroupModel;

/**
 * Created by Sachin on 09-03-2016.
 */
public class GroupDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    Context context;
    List<GroupModel> list;
    LayoutInflater layoutInflater;
    ViewGroup viewGroup;

    public  GroupDetailsAdapter(Context context,List<GroupModel> list)
    {

        this.list=list;
        this.context=context;
        layoutInflater=LayoutInflater.from(context);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=layoutInflater.inflate(R.layout.group_model,parent,false);
        ViewHolderGroup viewHolderGroup=new ViewHolderGroup(view);
        return viewHolderGroup;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if(holder!=null){
            ((ViewHolderGroup)holder).getTv_song_name(). setText(list.get(position).getSong_name());
            ((ViewHolderGroup)holder).getTv_group_name().setText(list.get(position).getGroup_name());
        }

        ((ViewHolderGroup)holder).getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent=new Intent(context,ClientActivity.class);
                    intent.putExtra("DATA",list.get(position));
                    context.startActivity(intent);
        }});

        AnimationsUtils.animate(holder, true);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @UiThread
    public void dataSetChanged() {
        notifyDataSetChanged();
    }

}
