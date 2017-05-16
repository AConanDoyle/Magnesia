package climbberlin.de.mapapps.climbup.Fragments;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;

import climbberlin.de.mapapps.climbup.R;

public class CustomList extends ArrayAdapter<String> {

    private ArrayList<String> titles, type, inout, krouten, brouten, adress, material, opening, webadress, price
            = new ArrayList<String>();
    private ArrayList<Double> lat, longC;
    private ArrayList<Integer> imageid;
    private Activity context;

    public CustomList(Activity context, ArrayList<String> heads, ArrayList<Integer> imageid, ArrayList<String> type, ArrayList<String> inout,
                      ArrayList<String> krouten, ArrayList<String> brouten, ArrayList<String> material, ArrayList<String> opening, ArrayList<String> price,
                      ArrayList<String> adress, ArrayList<Double> lat, ArrayList<Double> longC, ArrayList<String> webadress) {
        super(context, R.layout.list_contant_item, heads);
        this.context = context;
        this.titles = heads;
        this.imageid = imageid;
        this.type = type;
        this.inout = inout;
        this.krouten = krouten;
        this.brouten = brouten;
        this.material = material;
        this.opening = opening;
        this.price = price;
        this.adress = adress;
        this.lat = lat;
        this.longC = longC;
        this.webadress = webadress;
    }

    // puts the data into the text- and image views
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_contant_item, null, true);
        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewTitels);
        ImageView imageView = (ImageView) listViewItem.findViewById(R.id.imageView);
        TextView textViewType = (TextView) listViewItem.findViewById(R.id.textViewType);
        TextView textViewInOut = (TextView) listViewItem.findViewById(R.id.textViewInOUT);
        TextView textViewKRouten = (TextView) listViewItem.findViewById(R.id.textViewKRouten);
        TextView textViewBRouten = (TextView) listViewItem.findViewById(R.id.textViewBRouten);
        TextView textViewMaterial = (TextView) listViewItem.findViewById(R.id.textViewMaterial);
        TextView textViewOpening = (TextView) listViewItem.findViewById(R.id.textViewopening);
        TextView textViewPrice = (TextView) listViewItem.findViewById(R.id.textViewPrice);
        TextView textViewAdress = (TextView) listViewItem.findViewById(R.id.textViewAdress);
        TextView textViewLat = (TextView) listViewItem.findViewById(R.id.textViewLat);
        TextView textViewLong = (TextView) listViewItem.findViewById(R.id.textViewLong);
        TextView textViewWebadress = (TextView) listViewItem.findViewById(R.id.textViewWebadress);

        NumberFormat nm = NumberFormat.getNumberInstance();

        // Sets the image for a spot or a default image
        if (imageid.get(position) != 0) {
            Picasso.with(context).load(imageid.get(position)).into(imageView);
            // imageView.setImageResource(imageid[position]);
        } else {
            Picasso.with(context).load(R.mipmap.ic_launcher).into(imageView);
                if (type.get(position).equals("Bouldern")){
                Picasso.with(context).load(R.drawable.ic_button_black).into(imageView);
            } else {
                Picasso.with(context).load(R.drawable.ic_button_white).into(imageView);
            }
        }

        // set the values in the text
        textViewName.setText(titles.get(position));
        textViewType.setText(type.get(position));
        textViewInOut.setText(inout.get(position));
        textViewKRouten.setText(krouten.get(position));
        textViewBRouten.setText(brouten.get(position));
        textViewMaterial.setText(material.get(position));
        textViewOpening.setText(opening.get(position));
        textViewPrice.setText(price.get(position));
        textViewAdress.setText(adress.get(position));
        textViewLat.setText(nm.format(lat.get(position)));
        textViewLong.setText(nm.format(longC.get(position)));
        textViewWebadress.setText(webadress.get(position));

        return listViewItem;
    }

    // returns the listview Item by position
    public View getViewByPosition(int pos, ListView listView) {

        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);

        }
    }

}
