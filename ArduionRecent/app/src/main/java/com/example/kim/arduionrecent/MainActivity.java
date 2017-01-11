package com.example.kim.arduionrecent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.id;

public class MainActivity extends AppCompatActivity
{
    private final static int DEVICES_DIALOG = 1;
    private final static int ERROR_DIALOG = 2;

    public static Context mContext;
    public static AppCompatActivity activity;

    TextView myLabel, mRecv,test;
    EditText myTextbox;

    String msg;

    static BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button sendButton = (Button)findViewById(R.id.send);

        myLabel = (TextView)findViewById(R.id.label);
        myTextbox = (EditText)findViewById(R.id.entry);
        mRecv = (TextView)findViewById(R.id.recv);

        test = (TextView)findViewById(R.id.test);



        mContext = this;
        activity=this;


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            ErrorDialog("This device is not implement Bluetooth.");
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            ErrorDialog("This device is disabled Bluetooth.");
            return;
        }else DeviceDialog();


//소켓버튼 바로 실행




        sendButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
          /*      try
                {
*/
                ConnectThread thread = new ConnectThread("169.254.175.81");

                thread.start();

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);



/*
                }
                catch (IOException ex) { }*/
            }
        });

        //여기서 서버로부터 어떤정보를 받아야된다. 1이든 2이든  받고나서 버튼 시작한다
        //여기다가 서버로부터 받은정보 추가
        //받으면 그때 performclick() 으로해줘야된다
        // ipbutton.performClick();



    }//onCreate




    class ConnectThread extends Thread {
        String hostname;

        public ConnectThread(String addr) {
            hostname = addr;
        }

        public void run() {

            try {

                int port = 10012;

                Socket sock = new Socket(hostname, port);
                ObjectOutputStream outstream = new ObjectOutputStream(sock.getOutputStream());


                // String temp=beginListenForData();  /// 서버로 아두이노로부터 받은 데이터를 보낸다
                outstream.writeObject("from client");
                outstream.flush();





                ObjectInputStream instream = new ObjectInputStream(sock.getInputStream());
                String obj = (String) instream.readObject();//서버로부터 여기로 데이터받음

                Log.d("MainActivity", "�������� ���� �޽��� : " + obj);
                sendData(obj);  //서버로부터 받은 데이터를 아두이노로 보낸다
                //일단 이거는 받아짐  반대로 아두이노로부터 데이터는 안받아짐짐



                sock.close();

            } catch(Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    static public Set<BluetoothDevice> getPairedDevices() {
        return mBluetoothAdapter.getBondedDevices();
    }

    @Override
    public void onBackPressed() {
        doClose();
        super.onBackPressed();
    }


    public void doClose() {
        workerThread.interrupt();
        new CloseTask().execute();
    }

    public void doConnect(BluetoothDevice device) {
        mmDevice = device;

        //Standard SerialPortService ID
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {

            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mBluetoothAdapter.cancelDiscovery();
            new ConnectTask().execute();
        } catch (IOException e) {
            Log.e("", e.toString(), e);
            ErrorDialog("doConnect "+e.toString());
        }
    }

    private class ConnectTask extends AsyncTask<Void, Void, Object> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Object doInBackground(Void... params) {
            try {

                mmSocket.connect();
                mmOutputStream = mmSocket.getOutputStream();
                mmInputStream = mmSocket.getInputStream();

                beginListenForData();


            } catch (Throwable t) {
                Log.e( "", "connect? "+ t.getMessage() );
                doClose();
                return t;
            }
            return null;
        }


        @Override
        protected void onPostExecute(Object result) {
            myLabel.setText("Bluetooth Opened");
            if (result instanceof Throwable)
            {
                Log.d("","ConnectTask "+result.toString() );
                ErrorDialog("ConnectTask "+result.toString());

            }
        }
    }
    private class CloseTask extends AsyncTask<Void, Void, Object> {
        @Override
        protected Object doInBackground(Void... params) {
            try {
                try{mmOutputStream.close();}catch(Throwable t){/*ignore*/}
                try{mmInputStream.close();}catch(Throwable t){/*ignore*/}
                mmSocket.close();
            } catch (Throwable t) {
                return t;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result instanceof Throwable) {
                Log.e("",result.toString(),(Throwable)result);
                ErrorDialog(result.toString());
            }
        }
    }






    public void DeviceDialog()
    {
        if (activity.isFinishing()) return;

        FragmentManager fm = MainActivity.this.getSupportFragmentManager();
        MyDialogFragment alertDialog = MyDialogFragment.newInstance(DEVICES_DIALOG, "");
        alertDialog.show(fm, "");
    }



    public void ErrorDialog(String text)
    {
        if (activity.isFinishing()) return;

        FragmentManager fm = MainActivity.this.getSupportFragmentManager();
        MyDialogFragment alertDialog = MyDialogFragment.newInstance(ERROR_DIALOG, text);
        alertDialog.show(fm, "");
    }


    String temp;
    public String beginListenForData()
    {


        final Handler handler = new Handler(Looper.getMainLooper());

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];


        workerThread = new Thread(new Runnable()
        {
            public void run()
            {

                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == '\n')
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");


                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            mRecv.setText(data);
                                            temp=data;

                                            /////   //여기,, 여기가 아두이노로부터 받은값이고 여기서 서버로 보내야한다.

                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
        return temp;
    }

    void sendData(String fromServer) throws IOException
    {
        ///////////////이부분에서 서버에서 1이든 2이든 받으면된다  //서버에서 111111111받음

        ///////////////이부분에서 서버에서 1이든 2이든 받으면된다
   /*      msg = myTextbox.getText().toString();
        if ( msg.length() == 0 ) return;

        msg += "\n";
        mmOutputStream.write(msg.getBytes());*/

        //  if ( fromServer.length() == 0 ) return;

        fromServer += "\n";
        mmOutputStream.write(fromServer.getBytes());


        myLabel.setText("Data Sent"+fromServer);
        myTextbox.setText(" ");
    }


}