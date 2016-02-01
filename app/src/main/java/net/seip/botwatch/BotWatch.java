package net.seip.botwatch;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;

/**
 *
 */
enum LightColor {
    OFF,
    RED,
    GREEN,
    BLUE
}

/**
 *
 */
enum Speed {
    DEADSLOW,
    SLOW,
    NORMAL,
    FAST
}

/**
 *
 */
public class BotWatch extends AppCompatActivity {


    /**
     * Standard speeds
     */
    private static final int speedDeadslow = 20;
    private static final int speedSlow = 40;
    private static final int speedNormal = 70;
    private static final int speedFast = 100;

    private static final int speedTrigger = 100;
    private static final int turnTrigger = 360;

    /**
     * Number of sound files installed
     */
    private static final int LASTSOUND = 6;
    private static final UUID SERIAL_PORT_SERVICE_CLASS_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final String OUI_LEGO = "00:16:53";

    /**
     *
     */
    private static final String DEFAULT_NXT_ADDRESS = "00:16:53:19:FC:82";

    private String programName1 = "";
    private String programName2 = "";

    private String robotTypeName = "";

    SharedPreferences preferences;

    String nxtAddress = DEFAULT_NXT_ADDRESS;

    LightColor currentLight = LightColor.OFF;

    Speed currentSpeed = Speed.NORMAL;

    int currentSound = 0;

    BluetoothAdapter localAdapter;
    BluetoothSocket socket_nxt;

    boolean success = false;

    boolean connected = false;

    /**
     * UI objects
     */
    ImageButton btPower;
    ImageButton btSettings;

    ImageButton btLight;
    ImageButton btSpeed;
    ImageButton btBeedo;
    ImageButton btTrigger;

    ImageButton btForward;
    ImageButton btBack;
    ImageButton btLeft;
    ImageButton btRight;

    ImageButton btStop;

    ImageButton btProgram1;
    ImageButton btProgram2;


    //Enables Bluetooth if not enabled
    public void enableBT() {
        localAdapter = BluetoothAdapter.getDefaultAdapter();
        //If Bluetooth not enable then do it
        if (!localAdapter.isEnabled()) {
            localAdapter.enable();
            while (!(localAdapter.isEnabled())) {

            }
        }

    }

    /**
     *
     */
    public void disconnectFromNXT() {
        try {
            socket_nxt.close();

        } catch (IOException e) {
            Log.d("Bluetooth", getString(R.string.couldnotconnect));
        }
        success = false;
        connected = false;

        setDisplayStatus();
    }


    /**
     *
     * @return
     */
    public int getStraightSpeed() {
        int speed = 100;
        double multiplier = 1.0;

        switch (currentSpeed) {
            case DEADSLOW:
                speed = (int) Math.round(speedDeadslow * multiplier);
                break;
            case SLOW:
                speed = (int) Math.round(speedSlow * multiplier);
                break;
            case NORMAL:
                speed = (int) Math.round(speedNormal * multiplier);
                break;
            case FAST:
                speed = (int) Math.round(speedFast * multiplier);
                break;
        }

        return speed;
    }

    public int getTurnSpeed() {
        int speed = 100;
        double multiplier = 1.0;

        switch (currentSpeed) {
            case DEADSLOW:
                speed = (int) Math.round(speedDeadslow * multiplier);
                break;
            case SLOW:
                speed = (int) Math.round(speedSlow * multiplier);
                break;
            case NORMAL:
                speed = (int) Math.round(speedNormal * multiplier);
                break;
            case FAST:
                speed = (int) Math.round(speedFast * multiplier);
                break;
        }

        return speed;
    }

    /**
     *
     */
    public void setDisplayStatus() {

        if (connected) {

            btForward.setEnabled(true);
            btBack.setEnabled(true);
            btLeft.setEnabled(true);
            btRight.setEnabled(true);
            btStop.setEnabled(true);

            btTrigger.setEnabled(true);

            btPower.setEnabled(true);
            btPower.setImageResource(R.drawable.ic_bic_power_green);

            btBeedo.setEnabled(true);

            btSpeed.setEnabled(true);

            switch (currentSpeed) {
                case DEADSLOW:
                    btSpeed.setImageResource(R.drawable.ic_bic_button_red_snail);
                    break;
                case SLOW:
                    btSpeed.setImageResource(R.drawable.ic_bic_button_red_turtle);
                    break;
                case NORMAL:
                    btSpeed.setImageResource(R.drawable.ic_bic_button_red_cat);
                    break;
                case FAST:
                    btSpeed.setImageResource(R.drawable.ic_bic_button_red_hase);
                    break;
            }

            if (programName1.isEmpty()) {
                btProgram1.setVisibility(View.INVISIBLE);
                btProgram1.setEnabled(false);
            } else {
                btProgram1.setVisibility(View.VISIBLE);
                btProgram1.setEnabled(true);
            }


            if (programName2.isEmpty()) {
                btProgram2.setVisibility(View.INVISIBLE);
                btProgram1.setEnabled(false);
            } else {
                btProgram2.setVisibility(View.VISIBLE);
                btProgram1.setEnabled(true);
            }

            btProgram2.setEnabled(true);

            btLight.setEnabled(true);

            switch(currentLight) {
                case OFF:
                    btLight.setImageResource(R.drawable.ic_bic_bulb_off);
                    break;
                case RED:
                    btLight.setImageResource(R.drawable.ic_bic_bulb_red);
                    break;
                case GREEN:
                    btLight.setImageResource(R.drawable.ic_bic_bulb_green);
                    break;
                case BLUE:
                    btLight.setImageResource(R.drawable.ic_bic_bulb_blue);
                    break;
                default:
                    btLight.setImageResource(R.drawable.ic_bic_bulb_off);
                    break;
            }

        } else {

            btForward.setEnabled(false);
            btBack.setEnabled(false);
            btLeft.setEnabled(false);
            btRight.setEnabled(false);
            btStop.setEnabled(false);

            btLight.setEnabled(false);
            btLight.setImageResource(R.drawable.ic_bic_bulb_gray);

            btBeedo.setEnabled(false);
            btTrigger.setEnabled(false);

            btSpeed.setEnabled(false);

            switch (currentSpeed) {
                case DEADSLOW:
                    btSpeed.setImageResource(R.drawable.ic_bic_button_gray_snail);
                    break;
                case SLOW:
                    btSpeed.setImageResource(R.drawable.ic_bic_button_gray_turtle);
                    break;
                case NORMAL:
                    btSpeed.setImageResource(R.drawable.ic_bic_button_gray_cat);
                    break;
                case FAST:
                    btSpeed.setImageResource(R.drawable.ic_bic_button_gray_hase);
                    break;
            }

            if (programName1.isEmpty()) {
                btProgram1.setVisibility(View.INVISIBLE);
                btProgram1.setEnabled(false);
            } else {
                btProgram1.setVisibility(View.VISIBLE);
                btProgram1.setEnabled(false);
            }

            if (programName2.isEmpty()) {
                btProgram2.setVisibility(View.INVISIBLE);
                btProgram2.setEnabled(false);
            } else {
                btProgram2.setVisibility(View.VISIBLE);
                btProgram2.setEnabled(false);
            }

            if (nxtAddress.isEmpty()) {
                btPower.setEnabled(false);
            } else {
                btPower.setEnabled(true);
                btPower.setImageResource(R.drawable.ic_bic_power_red);
            }

        }

    }

    public void connectToNXT() {

        new ConnectTask().execute(nxtAddress);
    }

    private class ConnectTask extends AsyncTask<String, Integer, Boolean> {

        ProgressDialog progDlg = null;

        @Override
        protected Boolean doInBackground(String... address) {

            try {

                enableBT();

                BluetoothDevice nxt = localAdapter.getRemoteDevice(address[0]);

                socket_nxt = nxt.createRfcommSocketToServiceRecord(SERIAL_PORT_SERVICE_CLASS_UUID);
                socket_nxt.connect();

                connected = true;

            } catch (IOException e) {
                Log.d("Bluetooth", getString(R.string.couldnotconnect));
                connected = false;

            }

            return connected;
        }

        @Override
        protected void onPreExecute() {
            progDlg = ProgressDialog.show(BotWatch.this, getString(R.string.connecting), getString(R.string.searchingforrobot), true, false);

        }

//        protected void onProgressUpdate(Boolean result) {
            // setProgressPercent(progress[0]);
//        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (progDlg.isShowing()) {
                progDlg.dismiss();
            }
            if (!connected) {
                AlertDialog alertDialog = new AlertDialog.Builder(BotWatch.this).create();
                alertDialog.setTitle(getString(R.string.error));
                alertDialog.setMessage(getString(R.string.couldnotconnect));

                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                /*
                alertDialog.setIcon(R.drawable.icon);
                */
                alertDialog.show();
            }
           setDisplayStatus();
        }
    }



    public void writeMessage(byte msg) throws InterruptedException{
        BluetoothSocket connSock;

        connSock=socket_nxt;

        if(connSock!=null){
            try {

                OutputStreamWriter out=new OutputStreamWriter(connSock.getOutputStream());
                out.write(msg);
                out.flush();

                Thread.sleep(1000);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{
            //Error
        }
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 3];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j * 3 + 2] = ' ';
        }
        return new String(hexChars);
    }

    /**
     *
     * @param message
     */
    public void sendMessage(byte[] message) {

        try {
            OutputStream nxtOutputStream = socket_nxt.getOutputStream();
            // send message length
            int messageLength = message.length;

            Log.i("Botwatch", "sendMessage (" + messageLength + ") " + bytesToHex(message));

            nxtOutputStream.write(messageLength);
            nxtOutputStream.write(messageLength >> 8);
            nxtOutputStream.write(message, 0, message.length);
        } catch (IOException e) {

            connected = false;
            setDisplayStatus();

            e.printStackTrace();
        }
    }

    /**
     *
     * @return
     */
    public int readMessage(){
        BluetoothSocket connSock;

        int n;

        connSock=socket_nxt;

        if(connSock!=null){
            try {

                InputStreamReader in=new InputStreamReader(connSock.getInputStream());
                n=in.read();

                return n;

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return -1;
            }
        }else{
            //Error
            return -1;
        }

    }


    /**
     *
     */
    void openLightswitch() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View pview = inflater.inflate(R.layout.layout_lightswitch, null, false);
        final PopupWindow pw = new PopupWindow(pview,findViewById(R.id.main).getWidth() - 20,120, true);

        ImageButton img = (ImageButton) pview.findViewById(R.id.buttonLightBlue);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentLight = LightColor.BLUE;
                pw.dismiss();
                setDisplayStatus();
                sendMessage(LCPMessage.getSetInputModeMessage((byte) 2, LCPMessage.NXT__COLORBLUE, LCPMessage.NXT__RAWMODE));
            }
        });

        img = (ImageButton) pview.findViewById(R.id.buttonLightRed);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentLight = LightColor.RED;
                pw.dismiss();
                setDisplayStatus();
                sendMessage(LCPMessage.getSetInputModeMessage((byte) 2, LCPMessage.NXT__COLORRED, LCPMessage.NXT__RAWMODE));

            }
        });

        img = (ImageButton) pview.findViewById(R.id.buttonLightGreen);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentLight = LightColor.GREEN;
                pw.dismiss();
                setDisplayStatus();
                sendMessage(LCPMessage.getSetInputModeMessage((byte) 2, LCPMessage.NXT__COLORGREEN, LCPMessage.NXT__RAWMODE));

            }
        });

        img = (ImageButton) pview.findViewById(R.id.buttonLightOff);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentLight = LightColor.OFF;
                pw.dismiss();
                setDisplayStatus();
                sendMessage(LCPMessage.getSetInputModeMessage((byte) 2, LCPMessage.NXT__COLORNONE, LCPMessage.NXT__RAWMODE));

            }
        });

        pw.showAtLocation(findViewById(R.id.main), Gravity.NO_GRAVITY , 10, findViewById(R.id.buttonLight).getBottom());
    }

    /**
     *
     */
    void openSpeedswitch() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View pview = inflater.inflate(R.layout.layout_speedswitch, null, false);
        final PopupWindow pw = new PopupWindow(pview,findViewById(R.id.main).getWidth() - 20,120, true);

        ImageButton img = (ImageButton) pview.findViewById(R.id.buttonSpeedDeadslow);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentSpeed = Speed.DEADSLOW;
                pw.dismiss();
                setDisplayStatus();
            }
        });

        img = (ImageButton) pview.findViewById(R.id.buttonSpeedSlow);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentSpeed = Speed.SLOW;
                pw.dismiss();
                setDisplayStatus();
            }
        });

        img = (ImageButton) pview.findViewById(R.id.buttonSpeedNormal);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentSpeed = Speed.NORMAL;
                pw.dismiss();
                setDisplayStatus();
            }
        });

        img = (ImageButton) pview.findViewById(R.id.buttonSpeedFast);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentSpeed = Speed.FAST;
                pw.dismiss();
                setDisplayStatus();
            }
        });

        pw.showAtLocation(findViewById(R.id.main), Gravity.NO_GRAVITY , 10, findViewById(R.id.buttonSpeed).getBottom());
    }


    /**
     *
     */
    public void botAllOff() {
        sendMessage(LCPMessage.getMotorMessage(0, 0));
        sendMessage(LCPMessage.getMotorMessage(1, 0));
        sendMessage(LCPMessage.getMotorMessage(2, 0));
        sendMessage(LCPMessage.getSetInputModeMessage((byte) 2, LCPMessage.NXT__COLORNONE, LCPMessage.NXT__RAWMODE));
        currentLight = LightColor.OFF;
    }

    /**
     *
     */
    protected void botForward() {
        sendMessage(LCPMessage.getMotorMessageSync(1, getStraightSpeed()));
        sendMessage(LCPMessage.getMotorMessageSync(2, getStraightSpeed()));
    }

    /**
     *
     */
    protected void botBackward() {
        sendMessage(LCPMessage.getMotorMessageSync(1, -getStraightSpeed()));
        sendMessage(LCPMessage.getMotorMessageSync(2, -getStraightSpeed()));
    }

    /**
     *
     */
    protected void botLeft() {
        sendMessage(LCPMessage.getMotorMessageSync(1, -getTurnSpeed()));
        sendMessage(LCPMessage.getMotorMessageSync(2, getTurnSpeed()));
    }

    /**
     *
     */
    protected void botRight() {
        sendMessage(LCPMessage.getMotorMessageSync(1, getTurnSpeed()));
        sendMessage(LCPMessage.getMotorMessageSync(2, -getTurnSpeed()));
    }

    /**
     *
     */
    protected void botStop() {
        sendMessage(LCPMessage.getMotorMessage(1, 0));
        sendMessage(LCPMessage.getMotorMessage(2, 0));
    }

    /**
     *
     */
    protected void botSpeak() {
        sendMessage(LCPMessage.getPlaySoundfileMessage(String.format("R2D2-%d.rso", currentSound)));
        if (currentSound == LASTSOUND) {
            currentSound = 0;
        } else {
            currentSound++;
        }
    }

    /**
     *
     */
    protected void botFire() {
        sendMessage(LCPMessage.getMotorMessage(0, speedTrigger, turnTrigger));
    }

    /**
     *  Run program on NXT
     */
    protected void botRun(String programName) {
        sendMessage(LCPMessage.getStartProgramMessage(programName));
    }

    protected void loadPreferences() {
        nxtAddress = preferences.getString("pref_mac", DEFAULT_NXT_ADDRESS);

        programName1 = preferences.getString("pref_program_1","");
        programName2 = preferences.getString("pref_program_2","");

        robotTypeName = preferences.getString("pref_type","ShooterBot");
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /**
         * Load preferences
         */
        preferences = PreferenceManager.getDefaultSharedPreferences(this);


        loadPreferences();

        /**
         * Initialize
         */
        setContentView(R.layout.activity_bot_watch);

        /**
         * Connect button objects
         */
        btForward = (ImageButton) findViewById(R.id.buttonForward);
        btForward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    botForward();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    botStop();
                }
                return false;
            }
        });

        btBack = (ImageButton) findViewById(R.id.buttonBack);
        btBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    botBackward();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    botStop();
                }
                return false;
            }
        });

        btLeft = (ImageButton) findViewById(R.id.buttonLeft);
        btLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    botLeft();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    botStop();
                }
                return false;
            }
        });

        btRight = (ImageButton) findViewById(R.id.buttonRight);
        btRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    botRight();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    botStop();
                }
                return false;
            }
        });

        btStop = (ImageButton) findViewById(R.id.buttonStop);
        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                botAllOff();
            }
        });

        btBeedo = (ImageButton) findViewById(R.id.buttonBeedo);
        btBeedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                botSpeak();
            }
        });


        btPower = (ImageButton) findViewById(R.id.buttonPower);
        btPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connected) {
                    botAllOff();
                    disconnectFromNXT();
                } else {
                    connectToNXT();
                }
            }
        });

        btSettings = (ImageButton) findViewById(R.id.buttonSettings);
        btSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSetPref = new Intent(getApplicationContext(), PrefActivity.class);

                startActivityForResult(intentSetPref, 0);

            }
        });


        btTrigger = (ImageButton) findViewById(R.id.buttonTrigger);
        btTrigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                botFire();
            }
        });

        btLight = (ImageButton) findViewById(R.id.buttonLight);
        btLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLightswitch();
            }
        });

        btSpeed = (ImageButton) findViewById(R.id.buttonSpeed);
        btSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSpeedswitch();
            }
        });

        btProgram1 = (ImageButton) findViewById(R.id.buttonProgram1);
        btProgram1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                botRun(programName1);
            }
        });

        btProgram2 = (ImageButton) findViewById(R.id.buttonProgram2);
        btProgram2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                botRun(programName2);
            }
        });


        setDisplayStatus();

    }

    /**
     * As settings is the only sub-activity there are no branches
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        loadPreferences();
        setDisplayStatus();

    }



}
