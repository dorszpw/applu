package pl.coddev.applu.p;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import pl.coddev.applu.R;
import pl.coddev.applu.c.Constants;

/**
 * Created by Piotr Woszczek on 28/09/15.
 */
public class RedirectDialog extends Activity {

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_redirect);

        TextView txt = (TextView) findViewById(R.id.mytxt);
        txt.setText(R.string.filtering_in_full_version);

        Button getFullButton = (Button) findViewById(R.id.getFullVerBtn);

        getFullButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(Constants.APPSTORE_LINK_AMAZON_PRO));
                RedirectDialog.this.startActivity(i);
                finish();
            }

        });

        Button dismissbutton = (Button) findViewById(R.id.closeBtn);
        dismissbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RedirectDialog.this.finish();
            }
        });

    }
}
