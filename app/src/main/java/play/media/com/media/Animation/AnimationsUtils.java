package play.media.com.media.Animation;

import android.support.v7.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

/**
 * Created by Sachin on 03-02-2016.
 */
public class AnimationsUtils
{
    public static void animate(RecyclerView.ViewHolder holder,boolean goesDown){


        YoYo.with(Techniques.Wobble)
                .duration(1000)
                .playOn(holder.itemView);

/*
        AnimatorSet animatorSet=new AnimatorSet();
   ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(holder.itemView,"translationY",goesDown==true?300:-300,0);
   ObjectAnimator animatorTranslateX = ObjectAnimator.ofFloat(holder.itemView,"translationX",-50,50,-40,40,-30,30,-20,20,-10,10,0);
   animatorSet.playTogether(animatorTranslateX,animatorTranslateY);
        animatorSet.setDuration(1000);
//        animatorSet.setInterpolator(new AnticipateInterpolator());
        animatorSet.start();
*/


    }
}
