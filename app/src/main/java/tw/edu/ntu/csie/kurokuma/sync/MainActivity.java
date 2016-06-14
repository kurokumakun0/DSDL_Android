package tw.edu.ntu.csie.kurokuma.sync;

import android.app.Service;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private Socket mSocket;

    private TextView tv;
    private Button URL_button;
    String URL = null;
    private SensorManager sManager;
    Sensor accelerometer;
    Sensor magnetometer;
    float[] mGravity;
    float[] mGeomagnetic;
    String[] ZYXvalue = new String[3];
    Timer timer;
    Vibrator myVibrator;
    Boolean menu_state = true;
    ImageView gameover;
    ViewGroup container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Utils.full_screen_mode(getWindow().getDecorView());

        View mContentView = findViewById(R.id.fullscreen_content);
        if( mContentView != null )
            mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSend(view);
            }
        });
        View UltraHitButton = findViewById(R.id.ultra_hit);
        if( UltraHitButton != null )    {
            UltraHitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ultraAttack();
                }
            });
        }

        container = (ViewGroup) findViewById(R.id.container);
        gameover = (ImageView) findViewById(R.id.gameover);

        URL = getPreferences(MODE_PRIVATE).getString("connection", "http://10.5.6.140:3000/");

        try {
            mSocket = IO.socket(URL);
        }catch (URISyntaxException e)   {
            e.printStackTrace();
        }
        mSocket.on("connectOK", onConnectOK);
        mSocket.connect();

        myVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);

        tv = (TextView) findViewById(R.id.sensorValue);
        URL_button = (Button) findViewById(R.id.URL_btn);

        if( URL_button != null )    {
            URL_button.setText(URL);
            URL_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("enter your target URL");

                    // Set up the input
                    final EditText input = new EditText(MainActivity.this);
                    input.setText(URL);
                    // Specify the type of input expected;
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            URL = input.getText().toString();
                            URL_button.setText(URL);
                            getPreferences(MODE_PRIVATE).edit().putString("connection", URL).apply();
                            try {
                                mSocket = IO.socket(URL);
                            }catch (URISyntaxException e)   {
                                e.printStackTrace();
                            }

                            mSocket.disconnect();
                            mSocket.off("connectOK", onConnectOK);

                            mSocket.on("connectOK", onConnectOK);
                            mSocket.connect();

                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            });
        }

        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        sManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        
        if( mSocket != null ) {
            mSocket.on("connectOK", onConnectOK);
            mSocket.connect();
        }
    }

    protected void onPause() {
        super.onPause();
        sManager.unregisterListener(this);
        if( timer != null )
            timer.cancel();
        mSocket.disconnect();
        mSocket.off("connectOK", onConnectOK);
    }

    @Override
    protected void onStop()
    {
        sManager.unregisterListener(this);
        super.onStop();
        //timer.cancel();

        if( mSocket != null )   {
            if( mSocket.connected() )   {
                mSocket.disconnect();
            }
            mSocket.off("connectOK", onConnectOK);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1)
    {
        //Do nothing.
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                ZYXvalue[0] = Float.toString((float) Math.toDegrees(orientation[0]));
                ZYXvalue[1] = Float.toString((float) Math.toDegrees(orientation[1]));
                ZYXvalue[2] = Float.toString((float) Math.toDegrees(orientation[2]));
            }
        }

        tv.setText("Orientation X (Roll) :" + ZYXvalue[2] + "\n" +
                "Orientation Y (Pitch) :" + ZYXvalue[1] + "\n" +
                "Orientation Z (Yaw) :" + ZYXvalue[0]);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //timer.cancel();
    }

    public void attemptSend(View v) {
        String message = "fire";
        if(menu_state){
            message = "start";
            menu_state = false;
        }
        mSocket.emit("message", message);
    }

    public void ultraAttack()   {
        String message = "ultra";
        if(menu_state){
            message = "start";
            menu_state = false;
        }
        mSocket.emit("ultra", message);
    }

    private Emitter.Listener onConnectOK = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String message = (String)args[0];

                    if (message.equals("OK")) {
                        timer = new Timer(true);
                        timer.schedule(new MyTimerTask(), 80, 80);
                    } else if (message.equals("hit")) {
                        myVibrator.vibrate(300);
                    } else if (message.equals("die")) {
                        myVibrator.vibrate(1000);
                        menu_state = true;

                        gameover.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                gameover.setOnClickListener(null);
                                gameover.setVisibility(View.GONE);
                                URL_button.setVisibility(View.VISIBLE);
                            }
                        });
                        gameover.setVisibility(View.VISIBLE);
                        URL_button.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    };

    public class MyTimerTask extends TimerTask
    {
        public void run()
        {
            mSocket.emit("X", ZYXvalue[2]);
            mSocket.emit("Y", ZYXvalue[1]);
            //mSocket.emit("Z", ZYXvalue[0]);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Utils.full_screen_mode(getWindow().getDecorView());
    }
}
