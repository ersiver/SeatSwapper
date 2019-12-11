package com.breiter.seatswapper.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.breiter.seatswapper.R;

public class PasswordRulesAdapter extends BaseExpandableListAdapter {

    Context context;

    String[] title = {"(strong password rules)"};

    String[][] rules = {{"Your password must contain:\n" +
            "at least 8 characters\n" +
            "at least one uppercase letter\n" +
            "at least one lowercase letter\n" +
            "at least one number digit \n" +
            "at least one special character"} , {}};

    public PasswordRulesAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return title.length;
    }

    @Override
    public int getChildrenCount(int i) {
        return rules[i].length;
    }

    @Override
    public Object getGroup(int i) {
        return title[i];
    }

    @Override
    public Object getChild(int i, int i1) {
        return rules[i][i1];
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        String passwordRule = (String) getGroup(i);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_password_rule, null);
        }

        TextView passwordRule2 = view.findViewById(R.id.passRulesTitle);

        passwordRule2.setTypeface(null, Typeface.ITALIC);

        passwordRule2.setText(passwordRule);

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

        String rules = (String) getChild(i, i1);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_password_rules_title, null);
        }

        TextView rulesDescription = view.findViewById(R.id.rulesDescription);

        rulesDescription.setTypeface(null, Typeface.ITALIC);

        rulesDescription.setText(rules);

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
