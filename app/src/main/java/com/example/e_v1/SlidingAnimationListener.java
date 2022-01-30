package com.example.e_v1;

import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class SlidingAnimationListener implements Animation.AnimationListener{

    private Boolean isPageState = false;
    private Boolean homPageState = false;
    private Boolean menuPageState = false;

    private RelativeLayout layout_R;
    private RelativeLayout layout_L;
    private ImageButton button;

    public SlidingAnimationListener(RelativeLayout layout_R, RelativeLayout layout_L, ImageButton button){
        this.layout_R = layout_R;
        this.layout_L = layout_L;
        this.button = button;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
//        if(isPageState){
//            layout_L.setVisibility(View.INVISIBLE);
//            isPageState = false;
//        }else{
//            isPageState = true;
//        }

//        if(homPageState){
//            layout_L.setVisibility(View.INVISIBLE);
//            homPageState = false;
//        }else{
//            homPageState = true;
//        }

//        if(menuPageState){
//            layout_R.setVisibility(View.INVISIBLE);
//            menuPageState = false;
//        }else{
//            menuPageState = true;
//        }


    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public Boolean getIsPageState(){
        return isPageState;
    }

    public Boolean gethomePageState(){
        return homPageState;
    }

    public Boolean getMenuPageState() { return menuPageState;}
}
