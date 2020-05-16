package com.kos.AACassistant;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.VH>  {

    ArrayList<String> dataList;
    Boolean isCategoryList;
    String isSearch;
    String speechText,categorySelected,activityFrom;

    // Constructor
    public RecyclerAdapter(ArrayList<String> passedList,Boolean typeOfList,String st,String cs,
                           String af,String is) {
        this.dataList = passedList;
        this.isCategoryList = typeOfList;
        this.speechText = st;
        this.categorySelected = cs;
        this.activityFrom = af;
        this.isSearch = is;
    }
    // Filter Function
    public void filterList(ArrayList<String> filteredList)
    {
        dataList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.row_recyclerview, viewGroup, false);
        VH viewHolder = new VH(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VH vh, int position) {
        vh.txtRecordRow.setText(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }



    //RESPONSIBLE FOR MANAGING THE ROWS
    class VH extends RecyclerView.ViewHolder {

        TextView txtRecordRow;

        public VH(@NonNull View itemView) {
            super(itemView);

            txtRecordRow = itemView.findViewById(R.id.txtRecordRow);
            txtRecordRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isCategoryList == true)
                    {
                        Intent intent = new Intent(v.getContext(),SortingPhrases.class);
                        intent.putExtra("CategoryName",txtRecordRow.getText().toString());
                        intent.putExtra("From",activityFrom);
                        intent.putExtra("PC",categorySelected);
                        intent.putExtra("SpeechText",speechText);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        v.getContext().startActivity(intent);
                    }
                    if(!isSearch.equals("No"))
                    {
                        if(isSearch.equals("Categories"))
                        {
                            Intent intent = new Intent(v.getContext(),StartActivity.class);
                            intent.putExtra("PC",txtRecordRow.getText().toString());
                            intent.putExtra("SpeechText","");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            v.getContext().startActivity(intent);
                        }
                        else
                        {
                            Intent intent = new Intent(v.getContext(),FullScreenView.class);
                            intent.putExtra("PC",categorySelected);
                            intent.putExtra("SpeechText",txtRecordRow.getText().toString());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            v.getContext().startActivity(intent);
                        }
                    }
                }
            });
        }
    }
}
