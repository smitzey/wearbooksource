package io.wearbook.wlist;


import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyWearableListViewAdapter extends WearableListView.Adapter {

    private  Context context ;
    private  LayoutInflater layoutInflater ;
    private List<String> listItems ;

    public MyWearableListViewAdapter(Context aContext, LayoutInflater aLayoutInflater, List<String> aListOfItems) {
        this.context = aContext;
        this.layoutInflater = aLayoutInflater;
        this.listItems = aListOfItems ;
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        WearableListView.ViewHolder retVal = null ;

        retVal = new WearableListView.ViewHolder( layoutInflater.inflate(R.layout.list_item, null));

        return retVal ;
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {

        TextView view = (TextView) holder.itemView.findViewById(R.id.item_text);
        view.setText(listItems.get(position).toString());
        holder.itemView.setTag(position);

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    private static  final String TAG = MyWearableListViewAdapter.class.toString() ;

}
