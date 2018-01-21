package app.myapplication;



import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CheckBox;

import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import static android.view.Gravity.CENTER;
import static android.view.Gravity.LEFT;
import static android.view.Gravity.RIGHT;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class SettingsInfo extends BasicFrag {
    SharedPreferences sPrefs;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.settings, container, false);
        sPrefs = this.getActivity().getPreferences(Context.MODE_PRIVATE);
        addCheckBoxes(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentStatusListener = (FragmentStatusListener) context;
    }

    public void addCheckBoxes(View v) {
        LinearLayout featuresTable = (LinearLayout) v.getRootView().findViewById(R.id.layout);
        List<String> nimilista = MapsActivity.nimilista;
        java.util.Collections.sort(nimilista);

        for (String nimi : nimilista) {
            CheckBox check = new CheckBox(v.getContext());
            check.setText(nimi);
            check.setTextColor(Color.WHITE);
            int states[][] = {{android.R.attr.state_checked}, {}};
            int colors[] = {Color.RED, Color.RED};
            ColorStateList stateList = new ColorStateList(states, colors);
            check.setButtonTintList(stateList);
            check.setChecked(sPrefs.getBoolean(check.getText().toString(), false));
            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filterMarkers(v);
                }
            });
            Float scale = 1.4f;
            check.setScaleX(scale);
            check.setScaleY(scale);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(Math.round(275*(scale-1)+30), 20, 0, 20);
            params.width = MATCH_PARENT;
            check.setLayoutParams(params);

            featuresTable.addView(check);
        }
    }

    public void filterMarkers(View v) {
        CheckBox check = (CheckBox) v;
        MapsActivity.hideMarkers(check);
        SharedPreferences.Editor editor = sPrefs.edit();
        editor.putBoolean(check.getText().toString(), check.isChecked());
        editor.apply();
    }

}