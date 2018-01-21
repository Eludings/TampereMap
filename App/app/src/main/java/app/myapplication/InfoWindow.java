package app.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by it15 on 17.7.2017.
 */

public class InfoWindow extends BasicFrag {
    SharedPreferences sPrefs;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.infowindow, container, false);
        sPrefs = this.getActivity().getPreferences(Context.MODE_PRIVATE);
        return view;

    }

    public void createHorizontalalLayout(View view) {
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



}