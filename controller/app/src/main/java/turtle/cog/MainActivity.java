package turtle.cog;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton;
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
    TextView turtle; TextView turtleLegs;
    Thread animationBlink;
    boolean err;
    boolean dead = false; boolean moving = false;
    long velocity = 2;

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

                            turtle.setTextColor(Color.WHITE);
                            turtleLegs.setTextColor(Color.WHITE);
                            findViewById(R.id.textSearching).setVisibility(View.VISIBLE);

                            setupConnection();

                            findViewById(R.id.textSearching).setVisibility(View.INVISIBLE);

                            if (err) {
                                return;
                            }

                            findViewById(R.id.btnUp).setEnabled(true);
                            findViewById(R.id.btnLeft).setEnabled(true);
                            findViewById(R.id.btnDown).setEnabled(true);
                            findViewById(R.id.btnRight).setEnabled(true);
                            findViewById(R.id.barSpeed).setEnabled(true);

                            speedBar.setProgress(25);
                            send(":50");
                        } else {
                            send(" ");
                            closeConnection();

                            turtle.setTextColor(Color.WHITE);
                            turtleLegs.setTextColor(Color.WHITE);
                            moving = false; dead = false;
                        }
                    } else {
                        err = false;
                    }
                }
            });
        }

        turtle = (TextView) findViewById(R.id.turtle);
        turtleLegs = (TextView) findViewById(R.id.turtlelegs);

        findViewById(R.id.btnUp).setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch ( motionEvent.getAction() ) {
                    case MotionEvent.ACTION_DOWN: send("w"); moving = true; break;
                    case MotionEvent.ACTION_UP: send(" "); moving = false; break;
                }
                return false;
            }
        } );

        findViewById(R.id.btnLeft).setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch ( motionEvent.getAction() ) {
                    case MotionEvent.ACTION_DOWN: send("a"); moving = true; break;
                    case MotionEvent.ACTION_UP: send(" "); moving = false; break;
                }
                return false;
            }
        } );

        findViewById(R.id.btnDown).setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch ( motionEvent.getAction() ) {
                    case MotionEvent.ACTION_DOWN: send("s"); moving = true; break;
                    case MotionEvent.ACTION_UP: send(" "); moving = false; break;
                }
                return false;
            }
        } );

        findViewById(R.id.btnRight).setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch ( motionEvent.getAction() ) {
                    case MotionEvent.ACTION_DOWN: send("d"); moving = true; break;
                    case MotionEvent.ACTION_UP: send(" "); moving = false; break;
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
                velocity = 100/(progress+25);
                if (!fromUser) {
                    return;
                }

                try {
                    send(":" + (progress+25));
                }
                catch (NullPointerException e) {
                    // Log.e("", "");
                }
            }
        } );

        String[] walking = new String[] {"turtle_step", "turtle_stand", "turtle_step2", "turtle_stand"};
        Thread animationWalk = new Thread() {
            @Override
            public void run() {
                while(true) {
                    try {

                        if (!dead) {
                            if (moving) {

                                for (int i = 0; i < 4; i++) {
                                    final int resID = getResources().getIdentifier(walking[i], "string", getPackageName());
                                    sleep(100 * velocity);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            turtleLegs.setText(getString(resID));
                                        }
                                    });
                                    if (walking[i].equals("turtle_stand")) {
                                        if (!moving) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        animationBlink = new Thread() {
            @Override
            public void run() {
                while(true) {
                    try {

                        if (!dead) {

                            sleep(3000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    turtle.setText(getString(R.string.turtle_blink));
                                }
                            });
                            sleep(200);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    turtle.setText(getString(R.string.turtle));
                                }
                            });
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        animationWalk.start();
        animationBlink.start();
    }

    public void setupConnection() {
        sent = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    socket = new Socket("turtlecog",65432);
                    socket.setSoTimeout(5000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            turtle.setText(getString(R.string.turtle));
                        }
                    });
                    turtle.setTextColor(Color.GREEN);
                    turtleLegs.setTextColor(Color.GREEN);
                    dead = false;
                } catch (UnknownHostException e) {
                    Log.e("", "Unknown Host Error whilst connecting socket");
                } catch (IOException e) {
                    Log.e("", "IO Error whilst connecting socket");

                    err = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggle.setChecked(false);
                            turtle.setText(getString(R.string.turtle_dead));
                        }
                    });
                    turtle.setTextColor(Color.RED);
                    turtleLegs.setTextColor(Color.RED);
                    dead = true;
                    animationBlink.interrupt();
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