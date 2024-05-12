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
import pl.coddev.applu.R
import pl.coddev.applu.databinding.MainLayoutBinding
import pl.coddev.applu.utils.Log

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = "MainActivity"
    private var context: Context? = null
    private var dm: DisplayMetrics? = null
    var share: FloatingActionButton? = null
    private var scale = 0f
    private var startDelay = 500
    private var screen = VIEW.MAIN

    private lateinit var binding: MainLayoutBinding

    enum class HELPVIEW_ACTION {
        SHOW, HIDE
    }

    private enum class VIEW {
        MAIN, HELP, NO
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = applicationContext

        binding.play.setOnClickListener(this)
        binding.infoButton.setOnClickListener(this)
        binding.helpBody.movementMethod = LinkMovementMethod.getInstance()
//        binding.helpBodyBack.movementMethod = LinkMovementMethod.getInstance()
        dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        binding.relative.getViewTreeObserver().addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // calculate scale, the divisor is the hight of the original picture
                scale = binding.rocket!!.height.toFloat() / 1058
                binding.rocket!!.pivotX = 297 * scale
                binding.rocket!!.pivotY = 507 * scale
                binding.lid!!.pivotX = 666 * scale
                binding.lid!!.pivotY = 174 * scale
                binding.app!!.pivotX = 714 * scale
                binding.app!!.pivotY = 513 * scale
                binding.frame!!.layoutParams.height = binding.rocket!!.height
                //                if (!Utils.ranBefore(context))
//                    animateLogo(scale);
                //this is an important step not to keep receiving callbacks:
                //we should remove this listener
                //I use the function to remove it based on the api level!
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    binding.relative!!.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                else binding.relative!!.getViewTreeObserver().removeGlobalOnLayoutListener(this)
            }
        })
        share = findViewById(R.id.share)
        share!!.setOnClickListener(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.d(TAG, "onBackPressed ")
        if (screen == VIEW.HELP) {
            handleHelpView(HELPVIEW_ACTION.HIDE)
        } else if (screen == VIEW.MAIN) {
            finish()
        }
    }

    private fun resetImageViews() {
        binding.rocket!!.translationX = 0f
        binding.rocket!!.translationY = 0f
        binding.rocket!!.rotation = 0f
        binding.lid!!.translationX = 0f
        binding.lid!!.translationY = 0f
        binding.lid!!.rotation = 0f
        binding.app!!.setImageResource(R.drawable.ic_app)
        binding.app!!.translationX = 0f
        binding.app!!.translationY = 0f
        binding.app!!.rotation = 0f
        binding.background!!.translationX = 0f
        val rocketBackUnfade = ObjectAnimator.ofFloat(binding.rocketBackground, "alpha", 1f)
        val binBackUnfade = ObjectAnimator.ofFloat(binding.binBackground, "alpha", 1f)
        rocketBackUnfade.start()
        binBackUnfade.start()
    }

    private fun animateLogo(scale: Float) {
        val rotation = ObjectAnimator.ofFloat(binding.rocket, "rotation", -2f, 2f)
        rotation.startDelay = startDelay.toLong()
        rotation.repeatCount = 40
        rotation.repeatMode = ObjectAnimator.REVERSE
        rotation.interpolator = AccelerateDecelerateInterpolator()
        rotation.duration = 40
        val translate = ObjectAnimator.ofFloat(binding.rocket, "translationY", -1000f)
        translate.duration = 300
        translate.interpolator = AccelerateInterpolator()
        val rocketSet = AnimatorSet()
        rocketSet.playSequentially(rotation, translate)
        val lidRotation = ObjectAnimator.ofFloat(binding.lid, "rotation", 1000f)
        val lidTranslationX = ObjectAnimator.ofFloat(binding.lid, "translationX", dm!!.widthPixels.toFloat())
        val lidTranslationY = ObjectAnimator.ofFloat(binding.lid, "translationY", 50 * scale)
        val lidSet = AnimatorSet()
        lidSet.startDelay = startDelay + 30 * 50.toLong()
        lidSet.duration = 1000
        lidSet.playTogether(lidRotation, lidTranslationX, lidTranslationY)
        val appScaleX = ObjectAnimator.ofFloat(binding.app, "scaleX", 1f, 0.1f, 0.1f, 1f)
        val appScaleY = ObjectAnimator.ofFloat(binding.app, "scaleY", 1f, 0.1f, 0.1f, 1f)
        val appTranslationX = ObjectAnimator.ofFloat(binding.app, "translationX", 0f, 0f, 0f, 0f, -603 * scale)
        val appTranslationY = ObjectAnimator.ofFloat(binding.app, "translationY", 0f, 260 * scale, 320 * scale, 450 * scale)
        val appRotation = ObjectAnimator.ofFloat(binding.app, "rotation", 0f, 0f, 0f, 90f)
        val rocketBackFade = ObjectAnimator.ofFloat(binding.rocketBackground, "alpha", 1f, 1f, 1f, 0f)
        val binBackFade = ObjectAnimator.ofFloat(binding.binBackground, "alpha", 1f, 1f, 1f, 0f)
        val appSet = AnimatorSet()
        appSet.startDelay = startDelay + 30 * 50.toLong()
        appSet.duration = 3000
        appSet.interpolator = AnticipateOvershootInterpolator()
        appSet.playTogether(appScaleX, appScaleY, appTranslationX, appTranslationY, appRotation, rocketBackFade, binBackFade)
        val appTranslationYUp = ObjectAnimator.ofFloat(binding.app, "translationY", 230 * scale)
        val luTranslationX = ObjectAnimator.ofFloat(binding.background, "translationX", 220 * scale)
        val finishSet = AnimatorSet()
        finishSet.playSequentially(appSet, luTranslationX, appTranslationYUp)
        finishSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                binding.app.setImageResource(R.drawable.ic_app_white)
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
            binding.relative.visibility = View.VISIBLE
        } else {
            endValue = 0f
            endValueShare = binding.scrollView.height + 2 * share!!.height.toFloat()
        }
        val relativeAlpha = ObjectAnimator.ofFloat(binding.relative, "alpha", endValue)
        val shareFall = ObjectAnimator.ofFloat(share, "translationY", endValueShare)
        val relativeSet = AnimatorSet()
        relativeSet.duration = 700
        relativeSet.interpolator = OvershootInterpolator()
        relativeSet.playTogether(relativeAlpha, shareFall)
        relativeSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                Log.d(TAG, "onAnimationEnd ")
                if (action == HELPVIEW_ACTION.SHOW) {
                    binding.relative.visibility = View.GONE
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