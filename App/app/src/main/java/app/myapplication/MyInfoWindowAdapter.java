package app.myapplication;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View myContentsView;

    MyInfoWindowAdapter(LayoutInflater inflater) {
        myContentsView = inflater.inflate(R.layout.custominfowindow, null);
    }

    @Override
    public View getInfoContents(Marker marker) {
        TextView tvTitle = ((TextView) myContentsView.findViewById(R.id.markerinfo5));
        TextView tvSnippet = ((TextView) myContentsView.findViewById(R.id.markerinfo2));
        TextView tvInfo = ((TextView) myContentsView.findViewById(R.id.markerinfo3));
        TextView tvnimi = ((TextView) myContentsView.findViewById(R.id.markerinfo));
        TextView tvInfo3 = ((TextView) myContentsView.findViewById(R.id.markerinfo6));

        tvTitle.setText(marker.getTitle());
        tvSnippet.setText(marker.getSnippet());

        @SuppressWarnings("unchecked")
        HashMap<String, String> infoProperties = (HashMap<String, String>) marker.getTag();
        String textbox = "Ylläpitäjä: " + infoProperties.get("yllapitaja");
        String textbox2 = "Katuosoite: " + infoProperties.get("katuosoite");
        String textbox3 = "Puhelin: " + infoProperties.get("puhelinnumero");
        String textbox5 = "Lisätietoja: " + infoProperties.get("lisatieto_fi");
        String textbox6 = infoProperties.get("nimi_fi");
        tvnimi.setText(textbox6);
        tvnimi.setTextSize(20);
        tvTitle.setText(textbox);
        tvSnippet.setText(textbox2);
        tvInfo.setText(textbox3);
        tvInfo3.setText(textbox5);

        if (infoProperties.get("katuosoite").contains("null")) {
            tvSnippet.setVisibility(View.GONE);
        } else {
            tvSnippet.setVisibility(View.VISIBLE);
        }
        if (infoProperties.get("puhelinnumero").contains("null")) {
            tvInfo.setVisibility(View.GONE);
        } else {
            tvInfo.setVisibility(View.VISIBLE);
        }
        if (infoProperties.get("lisatieto_fi").contains("null")) {
            tvInfo3.setVisibility(View.GONE);
        } else {
            tvInfo3.setVisibility(View.VISIBLE);
        }
        return myContentsView;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

}