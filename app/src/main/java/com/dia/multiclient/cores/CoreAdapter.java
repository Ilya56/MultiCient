package com.dia.multiclient.cores;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Ilya on 13.10.2016.
 */
public abstract class CoreAdapter<T> extends BaseAdapter {
    protected List<T> list;
    protected LayoutInflater layoutInflater;
    protected Activity activity;

    public CoreAdapter (List<T> list, Activity activity) {
        this.list = list;
        this.activity = activity;
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return getCount() > i ? list.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public boolean add(T t) {
        boolean temp = false;
        try {
            temp = list.add(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
        return temp;
    }

    public T remove(int pos) {
        T t = list.remove(pos);
        changeData();
        return t;
    }

    public boolean remove(T t) {
        boolean temp = list.remove(t);
        changeData();
        return temp;
    }

    public void clear() {
        try {
            list.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        changeData();
    }

    public void set(List<T> tList) {
        this.list = tList;
        changeData();
    }

    public List<T> getAll() {
        return list;
    }

    private void changeData() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public boolean replace(T tOld, T tNew) {
        int i = list.indexOf(tOld);
        if (i < 0)
            return false;
        boolean r = list.remove(tOld);
        list.add(i, tNew);
        changeData();
        return r;
    }
}
