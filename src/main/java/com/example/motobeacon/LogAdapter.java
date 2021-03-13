package com.example.motobeacon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ExampleViewHolder> {
    private ArrayList<LogItem> mExampleList;

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView1;
        public TextView mTextView2;
        public TextView mTextView3;

        public ExampleViewHolder(View itemView) {
            super(itemView);
            mTextView1 = itemView.findViewById(R.id.textView);
            mTextView2 = itemView.findViewById(R.id.textView2);
            mTextView3 = itemView.findViewById(R.id.textView3);
        }
    }

    public LogAdapter(ArrayList<LogItem> exampleList) {
        mExampleList = exampleList;
    }

    public void addToLog(LogItem logItem) {
        this.mExampleList.add(logItem);
        this.notifyItemInserted(mExampleList.size() - 1);
    }


    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        LogItem currentItem = mExampleList.get(position);

        holder.mTextView1.setText(currentItem.getNumber());
        holder.mTextView2.setText(currentItem.getTime());
        holder.mTextView3.setText(currentItem.getAction());
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}