package com.example.callback.b_chat;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class vowel extends AppCompatActivity {

    Button listen,listDevices;
    ImageButton send;
    ListView listView;
    TextView status,word_box,score;
    EditText writeMsg;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] btArray;

    SendReceive sendReceive;

    public ArrayList<String> words = new ArrayList<>();
    String random_word,vowel;
    Random rand = new Random();
    int counter = 0;
    int i=0;
    int mark=0,mark0=0;
    AlertDialog.Builder alert;
    AlertDialog alert2;

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING=2;
    static final int STATE_CONNECTED=3;
    static final int STATE_CONNECTION_FAILED=4;
    static final int STATE_MESSAGE_RECEIVED=5;

    int REQUEST_ENABLE_BLUETOOTH=1;

    private static final String APP_NAME = "BTChat";
    private static final UUID MY_UUID=UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vowel);
        findViewByIdes();

        /****************get words from vowel file***************/
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("vowel");
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = in.readLine()) != null) {
                String word = line.trim();
                if (word.length() == 11) {
                    words.add(word);
                }
            }
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "File not loaded", Toast.LENGTH_LONG);
        }


        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BLUETOOTH);
        }

        implementListeners();
    }

    private void implementListeners() {

        listDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set<BluetoothDevice> bt=bluetoothAdapter.getBondedDevices();
                String[] strings=new String[bt.size()];
                btArray=new BluetoothDevice[bt.size()];
                int index=0;

                if( bt.size()>0)
                {
                    for(BluetoothDevice device : bt)
                    {
                        btArray[index]= device;
                        strings[index]=device.getName();
                        index++;
                    }
                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,strings);
                    listView.setAdapter(arrayAdapter);
                }
            }
        });

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServerClass serverClass=new ServerClass();
                serverClass.start();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClientClass clientClass=new ClientClass(btArray[i]);
                clientClass.start();

                status.setText("Connecting");
            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = String.valueOf(writeMsg.getText());
                writeMsg.setText("");
                if(i==0 && string.equalsIgnoreCase("start"))
                {
                    timer();
                    i++;
                    String str = "start1";
                    sendReceive.write(str.getBytes());
                }
                else if (string.equalsIgnoreCase(vowel)) {
                    mark++;
                    toast("Correct answer");
                    score.setText("score: "+Integer.toString(mark));
                    startgame();
                }
                else
                {
                    sendReceive.write(string.getBytes());
                }

            }
        });
    }

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what)
            {
                case STATE_LISTENING:
                    status.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    status.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    status.setText("Connected");
                    toast("Type start to begin the game");
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("Connection Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff= (byte[]) msg.obj;
                    String tempMsg=new String(readBuff,0,msg.arg1);
                    if(tempMsg.equalsIgnoreCase("start1"))
                    {
                        i++;
                        timer();
                    }
                    else if(tempMsg.equalsIgnoreCase("alert"))
                    {
                        counter=0;mark=0;mark0=0;
                        alert2.cancel();
                        timer();
                    }
                    else if(tempMsg.length()>5 && tempMsg.substring(0,5).equalsIgnoreCase("word "))
                    {
                        mark0++;
                        toast("Opponent gave correct answer");

                        String str = tempMsg.substring(5);
                        word_box.setText(str);
                        char ch;
                        int cnt=0;
                        for(int i=0;i<str.length();i++)
                        {
                            ch = str.charAt(i);
                            if(ch=='a' || ch=='e' || ch=='i' || ch=='o' || ch=='u')
                            {
                                cnt++;
                            }
                        }
                        vowel = Integer.toString(cnt);
                    }
                    break;
            }
            return true;
        }
    });
    //to display toast message
    private void toast(String message) {
        View layout = getLayoutInflater().inflate(R.layout.toast_layout,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        // Construct the toast, set the view and display
        Toast toast = new Toast(getApplicationContext());
        toast.setView(layout);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    private void findViewByIdes() {
        listen=(Button) findViewById(R.id.listen);
        send=(ImageButton) findViewById(R.id.send);
        listView=(ListView) findViewById(R.id.listview);
        word_box =(TextView) findViewById(R.id.word);
        score =(TextView) findViewById(R.id.score);
        status=(TextView) findViewById(R.id.status);
        writeMsg=(EditText) findViewById(R.id.writemsg);
        listDevices=(Button) findViewById(R.id.listDevices);
    }
    //to generate new word
    void startgame()
    {
        random_word = ("word "+words.get(rand.nextInt(words.size()))).toLowerCase();

        String wordTemp = random_word.substring(5);
        word_box.setText(wordTemp);
        char ch;

        int cnt=0;
        for(int i=0;i<wordTemp.length();i++)
        {
            ch = wordTemp.charAt(i);
            if(ch=='a' || ch=='e' || ch=='i' || ch=='o' || ch=='u')
            {
                cnt++;
            }
        }
        vowel = Integer.toString(cnt);
        sendReceive.write(random_word.getBytes());

    }
    //to start timer
    public void timer()
    {
        final TextView counttime = findViewById(R.id.counttime);
        new CountDownTimer(40000, 1000) {
            @Override

            public void onTick(long millisUntilFinished) {
                counttime.setText(String.valueOf("Timer:" + counter));
                counter++;
            }

            @Override
            public void onFinish() {
                counttime.setText("Finished");
                alertbox();

            }
        }.start();

        random_word = "word liquidizers";
        word_box.setText("liquidizers");
        vowel="5";
        score.setText("score: "+Integer.toString(0));
    }
    //once games ends, alert box will show scores to the user
    private void alertbox()
    {
        alert=new AlertDialog.Builder(vowel.this);

        alert.setMessage("GAME OVER!\nYour Score : "+mark+"\nOpponent Score : "+mark0).setCancelable(false)
                .setPositiveButton("NEW",new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        i=0;counter=0;mark=0;mark0=0;
                        String str = "alert";
                        sendReceive.write(str.getBytes());
                        timer();
                    }
                }).setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        //create dialog box
        alert2=alert.create();
        alert2.show();
    }

    private class ServerClass extends Thread
    {
        private BluetoothServerSocket serverSocket;

        public ServerClass(){
            try {
                serverSocket=bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME,MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            BluetoothSocket socket=null;

            while (socket==null)
            {
                try {
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTING;
                    handler.sendMessage(message);

                    socket=serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }

                if(socket!=null)
                {
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTED;
                    handler.sendMessage(message);

                    sendReceive=new SendReceive(socket);
                    sendReceive.start();

                    break;
                }
            }
        }
    }

    private class ClientClass extends Thread
    {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass (BluetoothDevice device1)
        {
            device=device1;

            try {
                socket=device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            try {
                socket.connect();
                Message message=Message.obtain();
                message.what=STATE_CONNECTED;
                handler.sendMessage(message);

                sendReceive=new SendReceive(socket);
                sendReceive.start();

            } catch (IOException e) {
                e.printStackTrace();
                Message message=Message.obtain();
                message.what=STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    private class SendReceive extends Thread
    {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive (BluetoothSocket socket)
        {
            bluetoothSocket=socket;
            InputStream tempIn=null;
            OutputStream tempOut=null;

            try {
                tempIn=bluetoothSocket.getInputStream();
                tempOut=bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream=tempIn;
            outputStream=tempOut;
        }

        public void run()
        {
            byte[] buffer=new byte[1024];
            int bytes;

            while (true)
            {
                try {
                    bytes=inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes)
        {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
