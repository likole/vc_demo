package cn.likole.vc;

/**
 * Created by likole on 8/21/18.
 */

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.io.File;
import java.util.Calendar;
import java.util.List;

public class ListViewAdapter extends BaseSwipeAdapter {

    private Context mContext;
    private List<File> mDatas;
    //private TextView mDelete;
    //private SwipeLayout swipeLayout;
    private int pos;

    public ListViewAdapter(Context context, List<File> mDatas) {
        this.mContext = context;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public void fillValues(int position, View convertView) {
        File file = mDatas.get(position);

        TextView tv_name = (TextView) convertView.findViewById(R.id.tv_filename);
        tv_name.setText(file.getName());

        TextView tv_time = (TextView) convertView.findViewById(R.id.tv_time);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(file.lastModified());
        if (file.length() > 1000)
            tv_time.setText(cal.getTime().toLocaleString()+"     "+file.length() / 1000 + "K");
        else
            tv_time.setText(cal.getTime().toLocaleString()+"     "+file.length() + "B");

        final SwipeLayout sl = (SwipeLayout) convertView.findViewById(getSwipeLayoutResourceId(position));
        final TextView delete  = (TextView) convertView.findViewById(R.id.delete);
        delete.setTag(position);
        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int pos = (Integer) delete.getTag();
                File obj = mDatas.get(pos);
                obj.delete();
                Log.e("onClick", "........pos ...."+pos+" obj = "+obj);
                mDatas.remove(obj);
                notifyDataSetChanged();
                sl.close();
            }
        });
    }

    @Override
    public View generateView(int position, ViewGroup arg1) {
        // TODO Auto-generated method stub
        Log.e("generateView", "position = " + position);
        View v = LayoutInflater.from(mContext).inflate(R.layout.listview_item, null);
        pos = position;
        final SwipeLayout swipeLayout = (SwipeLayout) v.findViewById(R.id.swipe);
        return v;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        // TODO Auto-generated method stub
        return R.id.swipe;
    }

}