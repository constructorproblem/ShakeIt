package aceshub.aces.shakeit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by honey on 18/9/16.
 */
public class Test extends Activity implements SensorEventListener {
    TextView name, ins, next;
    SharedPreferences sp;
    String temp;
    final Animation inx = new AlphaAnimation(0.0f, 1.0f);
    final Animation inx3 = new AlphaAnimation(0.0f, 1.0f);
    final Animation inx2 = new AlphaAnimation(0.0f, 1.0f);
    GifImageView gf;
    Typeface tf;


    //Sensor
    private SensorManager sensorMan;
    private Sensor accelerometer;
    private Sensor gyrometer;
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private int flag = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        sp = getApplicationContext().getSharedPreferences("shakeit", MODE_PRIVATE);

        name = (TextView) findViewById(R.id.textView3);
        ins = (TextView) findViewById(R.id.textView4);
        gf = (GifImageView) findViewById(R.id.view);
        next = (TextView) findViewById(R.id.textView5);

        tf = tf.createFromAsset(getAssets(), "fonts/roboto_thin.ttf");
        name.setTypeface(tf);
        ins.setTypeface(tf);

        //Sensor code
        sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyrometer = sensorMan.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorMan.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorMan.registerListener(this, gyrometer, SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;


        temp = sp.getString("name", "");
        if (temp.contains(" "))
            temp = temp.substring(0, temp.indexOf(" "));

        name.setText("Hi " + temp);
        name.setVisibility(View.INVISIBLE);
        ins.setVisibility(View.INVISIBLE);


        inx.setDuration(1500);
        inx3.setDuration(2000);
        inx2.setDuration(1000);
        name.startAnimation(inx);

        inx.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                name.setVisibility(View.VISIBLE);
                ins.startAnimation(inx2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        inx2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ins.setVisibility(View.VISIBLE);
                gf.setImageResource(R.drawable.shake);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Test.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });


    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values.clone();

            // Shake detection
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];


            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt(x * x + y * y + z * z);
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;

            if (mAccel > 20) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(300);

                name.setVisibility(View.INVISIBLE);
                ins.setVisibility(View.INVISIBLE);
                name.setText("That was great!");
                name.startAnimation(inx3);
                name.setVisibility(View.VISIBLE);

                gf.setImageResource(R.drawable.done);


                inx3.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        next.setVisibility(View.VISIBLE);

                    }


                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                sensorMan.unregisterListener(this);

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}


