package com.github.bskierys.pine.sample.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.bskierys.pine.sample.R;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    private static final int MAX_RANDOM_NUMBER = 6917;
    private static final long EXCEPTION_SPAM_PERIOD = 5000L;
    private static final String CUSTOM_FONT_PATH = "fonts/JosefinSans-Light.ttf";

    private static Typeface customFont;

    @BindView(R.id.txt_title) TextView titleView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if(customFont == null){
            customFont = Typeface.createFromAsset(getAssets(), CUSTOM_FONT_PATH);
        }
        titleView.setTypeface(customFont);

        Random rnd = new Random();
        Timber.i("Activity created. Here is a random number to celebrate it: %d", rnd.nextInt() % MAX_RANDOM_NUMBER);

        // log every x seconds. That's cruel
        Observable.interval(EXCEPTION_SPAM_PERIOD, TimeUnit.MILLISECONDS)
                  .subscribe(number -> {
                      // you can see every log with package, class, and line number when it have happened
                      if (number % 20 == 0) {
                          try {
                              throw new NullPointerException("Nobody expects NullPointerException!");
                          } catch (NullPointerException ex) {
                              Timber.e(ex, "Example of logging exception");
                          }
                      } else {
                          Timber.d("Debug message");
                      }
                  });
    }
}
