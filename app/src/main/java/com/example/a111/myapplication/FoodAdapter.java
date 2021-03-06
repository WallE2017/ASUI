package com.example.a111.myapplication;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 111 on 2017/8/25.
 */

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {
    private Context mContext;
    private List<Food> mfoodList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView foodImage;
        TextView foodName;
        public ViewHolder(View view) {
            super(view);
            cardView=(CardView)view;
            foodImage=(ImageView)view.findViewById(R.id.food_image);
            foodName=(TextView)view.findViewById(R.id.food_name);
        }
    }

    public FoodAdapter(List<Food> foodList) {
        this.mfoodList = foodList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext==null){
            mContext=parent.getContext();
        }
        View view= LayoutInflater.from(mContext).inflate(R.layout.food_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Food fruit=mfoodList.get(position);
        holder.foodName.setText(fruit.getName());
        //holder.foodImage.setImageResource(fruit.getImageId());
        holder.foodImage.setBackgroundResource(fruit.getImageId());
    }

    @Override
    public int getItemCount() {
        return mfoodList.size();
    }
}
