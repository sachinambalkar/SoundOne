package play.media.com.media;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Sachin on 09-03-2016.
 */
public class ViewHolderGroup extends RecyclerView.ViewHolder
{
    TextView tv_group_name;
    TextView tv_song_name;
    View view;
    public  ViewHolderGroup(View view){
        super(view);
        this.view=view;
        this.tv_group_name =(TextView)view.findViewById(R.id.tv_group_name);
        this.tv_song_name = (TextView)view.findViewById(R.id.tv_song_name);

    }

    public  View getView(){
        return  view;
    }
    public TextView getTv_group_name() {
        return tv_group_name;
    }
    public  TextView getTv_song_name(){
        return  tv_song_name;
    }
}
