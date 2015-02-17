package gadgetbazaar.bulbana.sistem;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import gadgetbazaar.bulbana.R;

public class secim extends ListActivity {

    protected String mSelected;
    protected String[] mTurler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secim);

        Resources resources =getResources();
        mTurler=resources.getStringArray(R.array.turler);


        ArrayAdapter<String> adapter =new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mTurler);
        setListAdapter(adapter);



    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        mSelected= (String) l.getItemAtPosition(position);

        Intent iSelected = new Intent(secim.this, haritaGorunumu.class);
        iSelected.putExtra("selected",  mSelected);
        iSelected.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        iSelected.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(iSelected);
    }
}
