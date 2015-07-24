package pl.coddev.applu.p;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import pl.coddev.applu.R;
import pl.coddev.applu.c.Constants;


public class MainActivity extends Activity {

    private final String TAG = "MainActivity";
    private Context context;
    private ImageButton confirmButton;
    private ImageButton cosmosLogo;
    private EditText codeEdit;
    private static boolean codeValid;
    private AnimatorSet buttonAnimDown;
    private Spinner countrySpinner;
    private TextView searchTxt;
    private Animation fieldAnim;
    private boolean receivedDataFromServerSuccessfully = true;
    private TextView versionName;
    private String currentGroupCode;
    private RelativeLayout swipeHint;
    private boolean ranBefore;
    private LinearLayout linear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.main_layout);

        ranBefore = getIntent().getBooleanExtra(Constants.EXTRA_RAN_BEFORE, true);

        
        context = getApplicationContext();
        
        searchTxt = (TextView) findViewById(R.id.searchText);

       // ImageView img = new ImageView();
       // img.drawable


    }






}
