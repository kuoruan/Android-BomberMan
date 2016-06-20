package com.kuoruan.bomberman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kuoruan.bomberman.R;
import com.kuoruan.bomberman.entity.ConnectionItem;

import java.util.List;

public class ConnectionAdapter extends BaseAdapter {
    private List<ConnectionItem> mData;
    private Context mContext;

    public ConnectionAdapter(Context context, List<ConnectionItem> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public int getCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    @Override
    public ConnectionItem getItem(int position) {
        if (mData != null) {
            return mData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflate = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflate.inflate(R.layout.connection_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.ip = (TextView) convertView.findViewById(R.id.ip);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ConnectionItem item = mData.get(position);
        holder.name.setText(mContext.getString(R.string.game_player) + item.name);
        holder.ip.setText("IP地址" + item.ip);
        return convertView;
    }

    public void changeData(List<ConnectionItem> data) {
        mData = data;
        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView name;
        TextView ip;
    }
}
