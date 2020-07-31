package pl.coddev.applu.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener

/**
 * Created by pw on 16/03/15.
 */
class ConfigureActivity : Activity(), OnItemSelectedListener {
    private val appWidgetId = 0
    private val confirmButton: ImageButton? = null
    var context: Context? = null
    private val countrySpinner: Spinner? = null
    var selectedCountry = ""
    var clickCount: Int? = null
    var countryCodes: List<String>? = null
    var codeEdit: EditText? = null
    private val countrySpinnerTxt: TextView? = null
    var adapter: ArrayAdapter<String>? = null
    private val groupCodeExists = false
    private val sourceCountryExists = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        selectedCountry = parent.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    companion object {
        private const val TAG = "ConfigureActivity"
    }
}