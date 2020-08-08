package pl.coddev.applu.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.help_view.*
import kotlinx.android.synthetic.main.main_layout.*
import pl.coddev.applu.R
import pl.coddev.applu.utils.Log

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = "MainActivity"
    private var context: Context? = null
    private var dm: DisplayMetrics? = null
    var share: FloatingActionButton? = null
    private var scale = 0f
    private var startDelay = 500
    private var screen = VIEW.MAIN

    enum class HELPVIEW_ACTION {
        SHOW, HIDE
    }

    private enum class VIEW {
        MAIN, HELP, NO
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)
        context = applicationContext

        play!!.setOnClickListener(this)
        infoButton!!.setOnClickListener(this)
        helpBody!!.movementMethod = LinkMovementMethod.getInstance()
        helpBodyBack!!.movementMethod = LinkMovementMethod.getInstance()
        dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        relative!!.getViewTreeObserver().addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // calculate scale, the divisor is the hight of the original picture
                scale = rocket!!.height.toFloat() / 1058
                rocket!!.pivotX = 297 * scale
                rocket!!.pivotY = 507 * scale
                lid!!.pivotX = 666 * scale
                lid!!.pivotY = 174 * scale
                app!!.pivotX = 714 * scale
                app!!.pivotY = 513 * scale
                frame!!.layoutParams.height = rocket!!.height
                //                if (!Utils.ranBefore(context))
//                    animateLogo(scale);
                //this is an important step not to keep receiving callbacks:
                //we should remove this listener
                //I use the function to remove it based on the api level!
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    relative!!.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                else relative!!.getViewTreeObserver().removeGlobalOnLayoutListener(this)
            }
        })
        share = findViewById(R.id.share)
        share!!.setOnClickListener(this)
    }

    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed ")
        if (screen == VIEW.HELP) {
            handleHelpView(HELPVIEW_ACTION.HIDE)
        } else if (screen == VIEW.MAIN) {
            finish()
        }
    }

    private fun resetImageViews() {
        rocket!!.translationX = 0f
        rocket!!.translationY = 0f
        rocket!!.rotation = 0f
        lid!!.translationX = 0f
        lid!!.translationY = 0f
        lid!!.rotation = 0f
        app!!.setImageResource(R.drawable.ic_app)
        app!!.translationX = 0f
        app!!.translationY = 0f
        app!!.rotation = 0f
        background!!.translationX = 0f
        val rocketBackUnfade = ObjectAnimator.ofFloat(rocket_background, "alpha", 1f)
        val binBackUnfade = ObjectAnimator.ofFloat(bin_background, "alpha", 1f)
        rocketBackUnfade.start()
        binBackUnfade.start()
    }

    private fun animateLogo(scale: Float) {
        val rotation = ObjectAnimator.ofFloat(rocket, "rotation", -2f, 2f)
        rotation.startDelay = startDelay.toLong()
        rotation.repeatCount = 40
        rotation.repeatMode = ObjectAnimator.REVERSE
        rotation.interpolator = AccelerateDecelerateInterpolator()
        rotation.duration = 40
        val translate = ObjectAnimator.ofFloat(rocket, "translationY", -1000f)
        translate.duration = 300
        translate.interpolator = AccelerateInterpolator()
        val rocketSet = AnimatorSet()
        rocketSet.playSequentially(rotation, translate)
        val lidRotation = ObjectAnimator.ofFloat(lid, "rotation", 1000f)
        val lidTranslationX = ObjectAnimator.ofFloat(lid, "translationX", dm!!.widthPixels.toFloat())
        val lidTranslationY = ObjectAnimator.ofFloat(lid, "translationY", 50 * scale)
        val lidSet = AnimatorSet()
        lidSet.startDelay = startDelay + 30 * 50.toLong()
        lidSet.duration = 1000
        lidSet.playTogether(lidRotation, lidTranslationX, lidTranslationY)
        val appScaleX = ObjectAnimator.ofFloat(app, "scaleX", 1f, 0.1f, 0.1f, 1f)
        val appScaleY = ObjectAnimator.ofFloat(app, "scaleY", 1f, 0.1f, 0.1f, 1f)
        val appTranslationX = ObjectAnimator.ofFloat(app, "translationX", 0f, 0f, 0f, 0f, -603 * scale)
        val appTranslationY = ObjectAnimator.ofFloat(app, "translationY", 0f, 260 * scale, 320 * scale, 450 * scale)
        val appRotation = ObjectAnimator.ofFloat(app, "rotation", 0f, 0f, 0f, 90f)
        val rocketBackFade = ObjectAnimator.ofFloat(rocket_background, "alpha", 1f, 1f, 1f, 0f)
        val binBackFade = ObjectAnimator.ofFloat(bin_background, "alpha", 1f, 1f, 1f, 0f)
        val appSet = AnimatorSet()
        appSet.startDelay = startDelay + 30 * 50.toLong()
        appSet.duration = 3000
        appSet.interpolator = AnticipateOvershootInterpolator()
        appSet.playTogether(appScaleX, appScaleY, appTranslationX, appTranslationY, appRotation, rocketBackFade, binBackFade)
        val appTranslationYUp = ObjectAnimator.ofFloat(app, "translationY", 230 * scale)
        val luTranslationX = ObjectAnimator.ofFloat(background, "translationX", 220 * scale)
        val finishSet = AnimatorSet()
        finishSet.playSequentially(appSet, luTranslationX, appTranslationYUp)
        finishSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                app!!.setImageResource(R.drawable.ic_app_white)
                share!!.isEnabled = true
                share!!.isClickable = true
            }
        })
        rocketSet.start()
        lidSet.start()
        finishSet.start()
    }

    fun handleHelpView(action: HELPVIEW_ACTION) {
        val endValue: Float
        val endValueShare: Float
        if (action == HELPVIEW_ACTION.HIDE) {
            endValue = 1f
            endValueShare = 0f
            relative!!.visibility = View.VISIBLE
        } else {
            endValue = 0f
            endValueShare = scrollView!!.height + 2 * share!!.height.toFloat()
        }
        val relativeAlpha = ObjectAnimator.ofFloat(relative, "alpha", endValue)
        val shareFall = ObjectAnimator.ofFloat(share, "translationY", endValueShare)
        val relativeSet = AnimatorSet()
        relativeSet.duration = 700
        relativeSet.interpolator = OvershootInterpolator()
        relativeSet.playTogether(relativeAlpha, shareFall)
        relativeSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                Log.d(TAG, "onAnimationEnd ")
                if (action == HELPVIEW_ACTION.SHOW) {
                    relative!!.visibility = View.GONE
                    screen = VIEW.HELP
                } else {
                    screen = VIEW.MAIN
                }
                super.onAnimationEnd(animation)
            }
        })
        relativeSet.start()
    }

    override fun onClick(v: View) {
        Log.d(TAG, "onClick ")
        when (v.id) {
            R.id.play -> {
                share!!.isEnabled = false
                share!!.isClickable = false
                resetImageViews()
                startDelay = 0
                animateLogo(scale)
            }
            R.id.share -> {
                //create the send intent
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.share_subject))
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        getString(R.string.share_message))
                startActivity(Intent.createChooser(shareIntent,
                        getString(R.string.share_title)))
            }
            R.id.infoButton -> handleHelpView(HELPVIEW_ACTION.SHOW)
        }
    }
}