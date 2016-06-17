package tw.edu.ntu.csie.kurokuma.sync;

import android.app.Service;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


public class MultipleActivity extends AppCompatActivity implements SensorEventListener{
    public static Socket mSocket;

    private TextView tv;
    private TextView tvPlayer;
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
    Boolean menu_state = false;
    ImageView gameover;

    //For player identification
    String uuid = UUID.randomUUID().toString().replaceAll("-", "");
    static int player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_multiple);

        Utils.full_screen_mode(getWindow().getDecorView());

        View mContentView = findViewById(R.id.fullscreen_content);
        if( mContentView != null )
            mContentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptSend(view);
                }
            });

        gameover = (ImageView) findViewById(R.id.gameover);

        URL = getPreferences(MODE_PRIVATE).getString("connection", "http://192.168.2.100:3000/");

        try {
            mSocket = IO.socket(URL);
        }catch (URISyntaxException e)   {
            e.printStackTrace();
        }
        mSocket.on("connectOK", onConnectOK);
        mSocket.on(uuid, onConnectOK);
        mSocket.connect();

        myVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);

        tv = (TextView) findViewById(R.id.sensorValue);
        tvPlayer= (TextView) findViewById(R.id.player);
        URL_button = (Button) findViewById(R.id.URL_btn);

        if( URL_button != null )    {
            URL_button.setText(URL);
            URL_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MultipleActivity.this);
                    builder.setTitle("enter your target URL");

                    // Set up the input
                    final EditText input = new EditText(MultipleActivity.this);
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
                            mSocket.off(uuid, onConnectOK);

                            mSocket.on("connectOK", onConnectOK);
                            mSocket.on(uuid, onConnectOK);
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
            mSocket.on(uuid, onConnectOK);
            mSocket.connect();
        }
    }

    protected void onPause() {
        super.onPause();
        sManager.unregisterListener(this);
        if( timer != null ) {
            timer.cancel();
        }
        mSocket.disconnect();
        mSocket.off("connectOK", onConnectOK);
        mSocket.off(uuid, onConnectOK);
    }

    @Override
    protected void onStop()
    {
        sManager.unregisterListener(this);
        super.onStop();

        if( mSocket != null )   {
            if( mSocket.connected() )   {
                mSocket.disconnect();
            }
            mSocket.off("connectOK", onConnectOK);
            mSocket.off(uuid, onConnectOK);
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

        //tv.setText("Orientation X (Roll) :" + ZYXvalue[2] + "\n" +
        //        "Orientation Y (Pitch) :" + ZYXvalue[1] + "\n" +
        //        "Orientation Z (Yaw) :" + ZYXvalue[0]);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void attemptSend(View v) {
        String message = "fire";
        if(menu_state){
            message = "start";
            menu_state = false;
        }
        //mSocket.emit("message", message);
        switch (player) {
            case 1:
                mSocket.emit("message1", message);
                break;
            case 2:
                mSocket.emit("message2", message);
                break;
        }
    }

    private Emitter.Listener onConnectOK = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            MultipleActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String message = (String)args[0];

                    if (message.equals("OK")) {
                        mSocket.emit("requestPlayer", uuid);
                    } else if(message.equals("player1")) {
                        player = 1;
                        tvPlayer.setText("Player1");
                    } else if(message.equals("player2")) {
                        player = 2;
                        tvPlayer.setText("Player2");
                    } else if(message.equals("ready")) {
                        Toast.makeText(MultipleActivity.this, "players are ready", Toast.LENGTH_SHORT).show();
                        timer = new Timer(true);
                        timer.schedule(new MyTimerTask(), 80, 80);
                    } else if(message.equals("checkConnect")) {
                        mSocket.emit("stillConnect", uuid);
                    } else if(message.equals("full")) {
                        player = 0;
                        tvPlayer.setText("full");
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
            switch (player) {
                case 1:
                    mSocket.emit("X1", ZYXvalue[2]);
                    mSocket.emit("Y1", ZYXvalue[1]);
                    break;
                case 2:
                    mSocket.emit("X2", ZYXvalue[2]);
                    mSocket.emit("Y2", ZYXvalue[1]);
                    break;
            }
            //mSocket.emit("X", ZYXvalue[2]);
            //mSocket.emit("Y", ZYXvalue[1]);
            //mSocket.emit("Z", ZYXvalue[0]);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Utils.full_screen_mode(getWindow().getDecorView());
    }
}
