package tw.edu.ntu.csie.kurokuma.sync;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.HashMap;
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

    SoundPool soundPool;
    HashMap<Integer, Integer> soundPoolMap;
    int soundID = 1;

    //For player identification
    String uuid = UUID.randomUUID().toString().replaceAll("-", "");
    static int player;

    // room id
    public static String magic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_multiple);

        Utils.full_screen_mode(getWindow().getDecorView());

        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap<Integer, Integer>();
        soundPoolMap.put(soundID, soundPool.load(this, R.raw.shoot, 1));
        soundPoolMap.put(2, soundPool.load(this, R.raw.hurt, 1));
        soundPoolMap.put(3, soundPool.load(this, R.raw.die, 1));

        View mContentView = findViewById(R.id.fullscreen_content);
        if( mContentView != null )
            mContentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playSound(1);
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
        ConnectandWaitforConfirm();

        myVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);

        Button num_btn = (Button) findViewById(R.id.num_btn);
        if( num_btn != null )   {
            num_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    type_number();
                }
            });
        }

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
                            if( magic.length() > 0 )    {
                                mSocket.off("connectOK"+magic, onRealConnect);
                                mSocket.on("connectOK"+magic, onRealConnect);
                            }

                            mSocket.once("connectOK", onConnectOK);
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
            ConnectandWaitforConfirm();
        }
    }

    protected void onPause() {
        super.onPause();
        sManager.unregisterListener(this);
        if( timer != null ) {
            timer.cancel();
        }
        if( mSocket != null )   {
            mSocket.disconnect();
            mSocket.off("connectOK", onConnectOK);
            mSocket.off(uuid, onConnectOK);
            if( magic.length() > 0 )    {
                mSocket.off("connectOK"+magic, onRealConnect);
            }
        }
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
            if( magic.length() > 0 )    {
                mSocket.off("connectOK"+magic, onRealConnect);
            }
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
                mSocket.emit("message1"+magic, message);
                break;
            case 2:
                mSocket.emit("message2"+magic, message);
                break;
        }
    }

    // unique listener
    private Emitter.Listener onRealConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String message = (String)args[0];

                    if(message.equals("player1")) {
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
                        playSound(2);
                    } else if (message.equals("die")) {
                        myVibrator.vibrate(1000);
                        playSound(3);
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

    private Emitter.Listener onConnectOK = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            MultipleActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String message = (String)args[0];

                    if (message.equals("OK")) {
                        setRealConnectListener();
                        mSocket.emit("requestPlayer"+magic, uuid);
                    } else if( message.equals("Failed") )   {
                        Toast.makeText(MultipleActivity.this, "connect failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };

    public void playSound(int num){
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float leftVolume = curVolume/maxVolume;
        float rightVolume = curVolume/maxVolume;
        int priority = 1;
        int no_loop = 0;
        float normal_playback_rate = 1f;
        soundPool.play(num, leftVolume, rightVolume, priority, no_loop, normal_playback_rate);
    }

    public class MyTimerTask extends TimerTask
    {
        public void run()
        {
            switch (player) {
                case 1:
                    mSocket.emit("X1"+magic, ZYXvalue[2]);
                    mSocket.emit("Y1"+magic, ZYXvalue[1]);
                    break;
                case 2:
                    mSocket.emit("X2"+magic, ZYXvalue[2]);
                    mSocket.emit("Y2"+magic, ZYXvalue[1]);
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

    public void type_number()   {
        AlertDialog.Builder builder = new AlertDialog.Builder(MultipleActivity.this);
        builder.setTitle("enter your magic hash");

        // Set up the input
        final EditText input = new EditText(MultipleActivity.this);
        // Specify the type of input expected;
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                magic = input.getText().toString();
                mSocket.emit("magic", magic);
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

    public void ConnectandWaitforConfirm()    {
        mSocket.once("connectOK", onConnectOK);
        mSocket.connect();
    }

    public void setRealConnectListener()   {
        mSocket.on("connectOK"+magic, onRealConnect);
        mSocket.on(uuid, onRealConnect);
    }
}
