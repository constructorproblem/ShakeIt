package aceshub.aces.shakeit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.provider.Settings.Secure;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import cz.msebera.android.httpclient.Header;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity  implements SensorEventListener{

    GifImageView gf;
    TextView tv, cancel;
    Typeface tf;
    SharedPreferences sp;
    String android_id;

    //Sensor
    private SensorManager sensorMan;
    private Sensor accelerometer;
    private Sensor gyrometer;
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private int flag = 0;

    ProgressDialog prgDialog;
    RequestParams params = new RequestParams();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gf = (GifImageView)findViewById(R.id.view);
        tv = (TextView) findViewById(R.id.textView);
        cancel = (TextView) findViewById(R.id.textView6);

        tf = tf.createFromAsset(getAssets(), "fonts/roboto_thin.ttf");
        tv.setTypeface(tf);

        gf.setImageResource(R.drawable.shake);

        sp = getApplicationContext().getSharedPreferences("shakeit", MODE_PRIVATE);

        //Getting device id
        android_id = Secure.getString(getApplicationContext().getContentResolver(),
                Secure.ANDROID_ID);

        //Creating parameters
        params.put("Name", composeJSON(sp.getString("name", "Not Found"), android_id));


        //Toast.makeText(MainActivity.this, android_id, Toast.LENGTH_SHORT).show();

        //Initialize Progress Dialog properties
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Sending Request..");
        prgDialog.setCancelable(true);
        prgDialog.setCanceledOnTouchOutside(false);

        //Sensor code
        sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyrometer = sensorMan.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorMan.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorMan.registerListener(this, gyrometer, SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v1.vibrate(300);
                startSensor();
                deleteRequest();


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

                sendRequest();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void startSensor() {
        sensorMan.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }
    public void stopSensor() {
        sensorMan.unregisterListener(this);

    }

    public void sendRequest() {
        //When JSON is not null
        AsyncHttpClient client = new AsyncHttpClient();
        prgDialog.show();
        client.post("http://10.100.106.68/college/addRequest.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                prgDialog.hide();

                //Updating UI
                tv.setText("Request Send");
                cancel.setVisibility(View.VISIBLE);
                gf.setImageResource(R.drawable.done);
                stopSensor();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // TODO Auto-generated method stub
                prgDialog.hide();
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Most common Error: Device might not be connected to Internet", Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    public String composeJSON(String name, String id){
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();

        try {

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", name);
            map.put("id", id);
            wordList.add(map);

            //Toast.makeText(MainActivity.this, name + id, Toast.LENGTH_SHORT).show();

        }
        catch(Exception e) {
            Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_SHORT).show();
        }

        Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);
    }

    public void  deleteRequest() {
        //When JSON is not null
        AsyncHttpClient client = new AsyncHttpClient();
        prgDialog.show();
        client.post("http://10.100.106.68/college/deleteRequest.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                prgDialog.hide();

                //Updating UI
                tv.setText("Shake to ask");
                cancel.setVisibility(View.INVISIBLE);
                gf.setImageResource(R.drawable.shake);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // TODO Auto-generated method stub
                prgDialog.hide();
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Most common Error: Device might not be connected to Internet", Toast.LENGTH_LONG).show();
                }
            }

        });
    }

}
