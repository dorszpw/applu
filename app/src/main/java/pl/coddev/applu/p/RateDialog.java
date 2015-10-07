package pl.coddev.applu.p;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import pl.coddev.applu.R;
import pl.coddev.applu.c.Constants;

/**
 * Created by Piotr Woszczek on 28/09/15.
 */
public class RateDialog extends Activity implements DialogInterface.OnClickListener{

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_rate);

        TextView txt = (TextView) findViewById(R.id.mytxt);
        txt.setText(R.string.rate_body);

        Button getFullButton = (Button) findViewById(R.id.getFullVerBtn);

        getFullButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(Constants.APPSTORE_LINK_PLAY_FREE));
                RateDialog.this.startActivity(i);
                finish();
            }

        });

        Button dismissbutton = (Button) findViewById(R.id.closeBtn);
        dismissbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RateDialog.this.finish();
            }
        });
        setFinishOnTouchOutside(false);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Toast.makeText(this, "clicked", Toast.LENGTH_SHORT);
    }
}
