package pl.coddev.applu.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import pl.coddev.applu.R
import pl.coddev.applu.utils.Constants

/**
 * Created by Piotr Woszczek on 28/09/15.
 */
class RateDialog : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_rate)
        val txt = findViewById<View>(R.id.mytxt) as TextView
        txt.setText(R.string.rate_body)
        val getFullButton = findViewById<View>(R.id.getFullVerBtn) as Button
        getFullButton.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(Constants.APPSTORE_LINK)
            this@RateDialog.startActivity(i)
            finish()
        }
        val dismissbutton = findViewById<View>(R.id.closeBtn) as Button
        dismissbutton.setOnClickListener { finish() }
        setFinishOnTouchOutside(false)
    }

    companion object {
        private const val TAG = "RateDialog"
    }
}