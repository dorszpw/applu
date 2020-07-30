package pl.coddev.applu.p;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

/**
 * Created by pw on 16/03/15.
 */
public class ConfigureActivity extends Activity implements AdapterView.OnItemSelectedListener{
    private static final String TAG = "ConfigureActivity";
    private int appWidgetId;
    private ImageButton confirmButton;
    Context context;
    private Spinner countrySpinner;
    String selectedCountry = "";
    Integer clickCount;
    List<String> countryCodes;
    EditText codeEdit;
    private TextView countrySpinnerTxt;
    ArrayAdapter<String> adapter;
    private boolean groupCodeExists;
    private boolean sourceCountryExists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedCountry = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
