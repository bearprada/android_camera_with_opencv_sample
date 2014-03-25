package lab.prada.android.test;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

class MyListAdapter extends BaseAdapter {
    private MainActivity mActivity;
    private final int mItemWidth;
    private final int mItemHeight;

    public MyListAdapter(MainActivity activity) {
        mActivity = activity;
        mItemWidth = Utils.getScreenWidth(activity) / 3;
        mItemHeight = Utils.getScreenHeight(activity) / 6;
    }

    @Override
    public int getCount() {
        return 18;
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        SubSurfaceView view = new SubSurfaceView(mActivity);
        view.setLayoutParams(new GridView.LayoutParams(mItemWidth, mItemHeight));
        view.setId(position);
        mActivity.addListener(view);
        return view;
    }
}