package com.example.basicproject;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;



public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ToDoListViewHolder>  {

    private final ArrayList<String> mToDoList;
    private LayoutInflater mInflater;
    private Context context;


    public ToDoListAdapter(Context context, ArrayList<String> wordList) {
        mInflater = LayoutInflater.from(context);
        this.mToDoList = wordList;
        this.context = context;
    }


    @NonNull
    @Override
    public ToDoListAdapter.ToDoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.todolistitem, parent, false);
        return new ToDoListViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoListAdapter.ToDoListViewHolder  holder, int position) {
        String mCurrent = mToDoList.get(position);
        holder.wordItemView.setText(mCurrent);
    }

    @Override
    public int getItemCount() {
        return mToDoList.size();
    }


    class ToDoListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final TextView wordItemView;
        final ToDoListAdapter mAdapter;

        public ToDoListViewHolder(View itemView, ToDoListAdapter adapter) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.ToDoItem);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final int mPosition = getLayoutPosition();

            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle(R.string.EditItemPopupTitle);
            builder.setMessage(R.string.EditItemPopupMessage);

            final EditText input = new EditText(context);
            input.setText(mToDoList.get(mPosition));
            builder.setView(input);

            builder.setPositiveButton(R.string.PositiveButtonsText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mToDoList.set(mPosition, input.getText().toString());
                    mAdapter.notifyDataSetChanged();
                }
            });

            builder.setNegativeButton(R.string.NegativeButtonsText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog ad = builder.create();
            ad.show();

        }
    }
}
