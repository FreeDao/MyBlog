package cn.picksomething.myblog.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.picksomething.myblog.R;
import cn.picksomething.myblog.model.SettingItems;


public class SlideMenuAdapter extends BaseAdapter {

    private Context mContext;
    private List<SettingItems> items = null;
    private LayoutInflater inflater = null;

    public SlideMenuAdapter(Context context, List<SettingItems> items) {
        this.mContext = context;
        this.items = items;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.slidemenu_item, null);
            holder.itemName = (TextView) convertView.findViewById(R.id.itemName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.itemName.setText(items.get(position).getSettingItemsName());
        Log.d("caobin", "i = " + position + "name = " + items.get(position).getSettingItemsName());
        return convertView;
    }

    private static class ViewHolder {
        TextView itemName = null;
    }

}
