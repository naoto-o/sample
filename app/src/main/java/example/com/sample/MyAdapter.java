package example.com.sample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
public class MyAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater = null;
    ArrayList<Rec> recList;

    public MyAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setVoiceList(ArrayList<Rec> recList) {
        this.recList = recList;
    }

    @Override
    public int getCount() {
        return recList.size();
    }

    @Override
    public Object getItem(int position) {
        return recList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return recList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.reclow,parent,false);

        ((TextView)convertView.findViewById(R.id.name)).setText(recList.get(position).getName());

        ((TextView)convertView.findViewById(R.id.updatetime)).setText(recList.get(position).getUpdateTime());
        return convertView;
    }

}
