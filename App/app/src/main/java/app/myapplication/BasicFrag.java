package app.myapplication;

import android.content.Context;

import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import java.util.HashMap;

public class BasicFrag extends Fragment {
    FragmentStatusListener fragmentStatusListener;

    interface FragmentStatusListener
    {
        void onStatusPass(HashMap<String, Boolean> status);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentStatusListener = (FragmentStatusListener) context;
        passStatus(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        passStatus(false);
    }

    public void passStatus(Boolean fragmentStatus) {
        HashMap<String, Boolean> status = new HashMap<>();
        status.put(this.getTag(), fragmentStatus);
        fragmentStatusListener.onStatusPass(status);

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        FragmentTransaction tr = getFragmentManager().beginTransaction();
        tr.detach(this).attach(this).commit();
    }

    public void closeFrag()
    {
        getActivity().onBackPressed();
        Log.d("Fragment:", "closed");
    }
}