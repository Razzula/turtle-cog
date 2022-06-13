package turtle.cog;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton;
import android.widget.Button;
import android.widget.SeekBar;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    static Thread sent;
    static Socket socket;
    PrintWriter socketOut;
    Switch toggle;
    TextView turtle;
    boolean err;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SeekBar speedBar = (SeekBar) findViewById(R.id.barSpeed);
        speedBar.setEnabled(false);

        toggle = (Switch) findViewById(R.id.enable);
        err = false;

        if (toggle != null) {
            toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!err) {
                        if (isChecked) {
                            setupConnection();

                            if (err) {
                                return;
                            }

                            findViewById(R.id.btnUp).setEnabled(true);
                            findViewById(R.id.btnLeft).setEnabled(true);
                            findViewById(R.id.btnDown).setEnabled(true);
                            findViewById(R.id.btnRight).setEnabled(true);
                            findViewById(R.id.barSpeed).setEnabled(true);

                            speedBar.setProgress(50);
                            send(":50");
                        } else {
                            closeConnection();

                            turtle.setTextColor(Color.WHITE);
                        }
                    } else {
                        err = false;
                    }
                }
            });
        }

        turtle = (TextView) findViewById(R.id.turtle);

        findViewById(R.id.btnUp).setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch ( motionEvent.getAction() ) {
                    case MotionEvent.ACTION_DOWN: send("w"); break;
                    case MotionEvent.ACTION_UP: send(" "); break;
                }
                return false;
            }
        } );

        findViewById(R.id.btnLeft).setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch ( motionEvent.getAction() ) {
                    case MotionEvent.ACTION_DOWN: send("a"); break;
                    case MotionEvent.ACTION_UP: send(" "); break;
                }
                return false;
            }
        } );

        findViewById(R.id.btnDown).setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch ( motionEvent.getAction() ) {
                    case MotionEvent.ACTION_DOWN: send("s"); break;
                    case MotionEvent.ACTION_UP: send(" "); break;
                }
                return false;
            }
        } );

        findViewById(R.id.btnRight).setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch ( motionEvent.getAction() ) {
                    case MotionEvent.ACTION_DOWN: send("d"); break;
                    case MotionEvent.ACTION_UP: send(" "); break;
                }
                return false;
            }
        } );

        SeekBar speed = (SeekBar) findViewById(R.id.barSpeed);
        speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }

                try {
                    send(":" + progress);
                }
                catch (NullPointerException e) {
                    // Log.e("", "");
                }
            }
        } );
    }

    public void setupConnection() {
        sent = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    socket = new Socket("turtlecog",65432);
                    socket.setSoTimeout(5000);
                    turtle.setTextColor(Color.GREEN);
                } catch (UnknownHostException e) {
                    Log.e("", "Unknown Host Error whilst connecting socket");
                } catch (IOException e) {
                    Log.e("", "IO Error whilst connecting socket");

                    err = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggle.setChecked(false);
                        }
                    });
                    turtle.setTextColor(Color.RED);
                    return;
                }

                try {
                    socketOut = new PrintWriter(socket.getOutputStream(), true);
                }
                catch (NullPointerException e) { // socket does not exist
                    Log.w("", "Cannot connect");
                }
                catch (IOException e) {
                    Log.e("", "IO Error");
                }
            }
        });

        sent.start();
        try {
            sent.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            socket.close();
            socketOut.close();
        }
        catch (IOException e) {
            Log.e("", "IO Error whilst closing socket");
        }
        catch (NullPointerException e) {
            Log.w("", "Socket already closed");
        }

        findViewById(R.id.btnUp).setEnabled(false);
        findViewById(R.id.btnLeft).setEnabled(false);
        findViewById(R.id.btnDown).setEnabled(false);
        findViewById(R.id.btnRight).setEnabled(false);
        findViewById(R.id.barSpeed).setEnabled(false);
    }

    private void send(String message) {
        Thread send = new Thread(new Runnable() {
            @Override
            public void run() {
                socketOut.print(message);
                socketOut.flush();
            }
        });
        send.start();
    }
}