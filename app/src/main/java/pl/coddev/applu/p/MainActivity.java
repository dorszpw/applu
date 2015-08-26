package pl.coddev.applu.p;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import pl.coddev.applu.R;
import pl.coddev.applu.c.Log;
import pl.coddev.applu.c.Utils;


public class MainActivity extends Activity implements View.OnClickListener {

    private final String TAG = "MainActivity";
    private Context context;
    RelativeLayout relative;
    ImageButton play;
    ImageButton infoButton;
    ImageView lu;
    ImageView rocket_background;
    ImageView bin_background;
    ImageView app;
    ImageView lid;
    ImageView rocket;
    ImageView frame;
    private DisplayMetrics dm;
    FloatingActionButton share;
    private float scale;
    private int startDelay = 500;
    private boolean onHelpScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.main_layout);

        context = getApplicationContext();
        relative = (RelativeLayout) findViewById(R.id.relative);
        frame = (ImageView) findViewById(R.id.frame);
        lu = (ImageView) findViewById(R.id.background);
        app = (ImageView) findViewById(R.id.app);
        lid = (ImageView) findViewById(R.id.lid);
        rocket = (ImageView) findViewById(R.id.rocket);
        rocket_background = (ImageView) findViewById(R.id.rocket_background);
        bin_background = (ImageView) findViewById(R.id.bin_background);
        play = (ImageButton) findViewById(R.id.play);
        play.setOnClickListener(this);
        infoButton = (ImageButton) findViewById(R.id.infoButton);
        infoButton.setOnClickListener(this);

        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);


        relative.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // calculate scale, the divisor is the hight of the original picture
                scale = (float) rocket.getHeight() / 1058;
                rocket.setPivotX(297 * scale);
                rocket.setPivotY(507 * scale);
                lid.setPivotX(666 * scale);
                lid.setPivotY(174 * scale);
                app.setPivotX(714 * scale);
                app.setPivotY(513 * scale);
                frame.getLayoutParams().height = rocket.getHeight();
                if (!Utils.ranBefore(context))
                    animateLogo(scale);
                //this is an important step not to keep receiving callbacks:
                //we should remove this listener
                //I use the function to remove it based on the api level!
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
                    relative.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    relative.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });


        share = (FloatingActionButton) findViewById(R.id.share);
        share.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed ");
        if (onHelpScreen) {
            animateRelative(1f);
            onHelpScreen = false;
        } else {
            super.onBackPressed();
        }
    }

    private void resetImageViews() {
        rocket.setTranslationX(0);
        rocket.setTranslationY(0);
        rocket.setRotation(0);
        lid.setTranslationX(0);
        lid.setTranslationY(0);
        lid.setRotation(0);
        app.setImageResource(R.drawable.ic_app);
        app.setTranslationX(0);
        app.setTranslationY(0);
        app.setRotation(0);
        lu.setTranslationX(0);
        ObjectAnimator rocketBackUnfade = ObjectAnimator.ofFloat(rocket_background, "alpha", 1);
        ObjectAnimator binBackUnfade = ObjectAnimator.ofFloat(bin_background, "alpha", 1);
        rocketBackUnfade.start();
        binBackUnfade.start();
    }

    private void animateLogo(float scale) {


        ObjectAnimator rotation = ObjectAnimator.ofFloat(rocket, "rotation", -2, 2);
        rotation.setStartDelay(startDelay);
        rotation.setRepeatCount(40);
        rotation.setRepeatMode(ObjectAnimator.REVERSE);
        rotation.setInterpolator(new AccelerateDecelerateInterpolator());
        rotation.setDuration(40);
        final ObjectAnimator translate = ObjectAnimator.ofFloat(rocket, "translationY", -1000);
        translate.setDuration(300);
        translate.setInterpolator(new AccelerateInterpolator());

        AnimatorSet rocketSet = new AnimatorSet();
        rocketSet.playSequentially(rotation, translate);

        ObjectAnimator lidRotation = ObjectAnimator.ofFloat(lid, "rotation", 1000);
        ObjectAnimator lidTranslationX = ObjectAnimator.ofFloat(lid, "translationX", dm.widthPixels);
        ObjectAnimator lidTranslationY = ObjectAnimator.ofFloat(lid, "translationY", 50 * scale);
        AnimatorSet lidSet = new AnimatorSet();
        lidSet.setStartDelay(startDelay + 30 * 50);
        lidSet.setDuration(1000);
        lidSet.playTogether(lidRotation, lidTranslationX, lidTranslationY);


        ObjectAnimator appScaleX = ObjectAnimator.ofFloat(app, "scaleX", 1f, 0.1f, 0.1f, 1f);
        ObjectAnimator appScaleY = ObjectAnimator.ofFloat(app, "scaleY", 1f, 0.1f, 0.1f, 1f);
        ObjectAnimator appTranslationX = ObjectAnimator.ofFloat(app, "translationX", 0, 0, 0, 0, -603 * scale);
        ObjectAnimator appTranslationY = ObjectAnimator.ofFloat(app, "translationY", 0, 260 * scale, 320 * scale, 450 * scale);
        ObjectAnimator appRotation = ObjectAnimator.ofFloat(app, "rotation", 0, 0, 0, 90);
        ObjectAnimator rocketBackFade = ObjectAnimator.ofFloat(rocket_background, "alpha", 1, 1, 1, 0);
        ObjectAnimator binBackFade = ObjectAnimator.ofFloat(bin_background, "alpha", 1, 1, 1, 0);
        AnimatorSet appSet = new AnimatorSet();
        appSet.setStartDelay(startDelay + 30 * 50);
        appSet.setDuration(3000);
        appSet.setInterpolator(new AnticipateOvershootInterpolator());
        appSet.playTogether(appScaleX, appScaleY, appTranslationX, appTranslationY, appRotation, rocketBackFade, binBackFade);


        ObjectAnimator appTranslationYUp = ObjectAnimator.ofFloat(app, "translationY", 230 * scale);
        ObjectAnimator luTranslationX = ObjectAnimator.ofFloat(lu, "translationX", 220 * scale);
        AnimatorSet finishSet = new AnimatorSet();

        finishSet.playSequentially(appSet, luTranslationX, appTranslationYUp);
        finishSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                app.setImageResource(R.drawable.ic_app_white);
                share.setEnabled(true);
                share.setClickable(true);
            }
        });

        rocketSet.start();
        lidSet.start();
        finishSet.start();
    }

    void animateRelative(final float endValue) {
        if (endValue == 1f)
            share.setVisibility(View.VISIBLE);
        ObjectAnimator relativeScale = ObjectAnimator.ofFloat(relative, "scaleX", endValue);
        ObjectAnimator relativeAlpha = ObjectAnimator.ofFloat(relative, "alpha", endValue);
        AnimatorSet relativeSet = new AnimatorSet();
        relativeSet.setDuration(500);
        relativeSet.setInterpolator(new OvershootInterpolator());
        relativeSet.playTogether(relativeAlpha, relativeScale);
        relativeSet.start();
        relativeSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (endValue == 0f)
                    share.setVisibility(View.INVISIBLE);
            }
        });
    }


    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick ");
        switch (v.getId()) {
            case R.id.play:
                share.setEnabled(false);
                share.setClickable(false);
                resetImageViews();
                startDelay = 0;
                animateLogo(scale);
                break;

            case R.id.share:
                //create the send intent
                Intent shareIntent =
                        new Intent(android.content.Intent.ACTION_SEND);

                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                        getString(R.string.share_subject));

                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                        getString(R.string.share_message));
                startActivity(Intent.createChooser(shareIntent,
                        getString(R.string.share_title)));
                break;

            case R.id.infoButton:
                onHelpScreen = true;
                animateRelative(0f);

        }
    }

}
