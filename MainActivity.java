public class MainActivity extends Activity {


private Button startButton,stopButton;

public byte[] buffer;
public static DatagramSocket socket;
    AudioRecord recorder;

private int sampleRate = 44100;   
private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;    
private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;       
int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
    private boolean status = true;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

     startButton = (Button) findViewById (R.id.start_button);
     stopButton = (Button) findViewById (R.id.stop_button);

     startButton.setOnClickListener(startListener);
     stopButton.setOnClickListener(stopListener);

     minBufSize += 2048;
}

@Override
public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
}

private final OnClickListener stopListener = new OnClickListener() {

    @Override
    public void onClick(View arg0) {
                status = false;
                recorder.release();
                Log.d("VS","Recorder released");
    }
};

private final OnClickListener startListener = new OnClickListener() {

    @Override
    public void onClick(View arg0) {
                status = true;
                startStreaming();           
    }
};



public void startStreaming()
{
    Thread streamThread = new Thread(new Runnable(){
        @Override
        public void run()
        {
            try{

                DatagramSocket socket = new DatagramSocket();
                Log.d("VS", "Socket Created");

                byte[] buffer = new byte[minBufSize];

                Log.d("VS","Buffer created of size " + minBufSize);


                Log.d("VS", "Address retrieved");
                recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRate,channelConfig,audioFormat,minBufSize);
                Log.d("VS", "Recorder initialized");


                recorder.startRecording();


                InetAddress IPAddress = InetAddress.getByName("192.168.1.5");
                byte[] sendData = new byte[1024];
                byte[] receiveData = new byte[1024];


                while (status == true)
                {
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 50005);
                    socket.send(sendPacket);
                }

            } catch(UnknownHostException e) {
                Log.e("VS", "UnknownHostException");
            } catch (IOException e) {
                Log.e("VS", "IOException");
                e.printStackTrace();
            } 


        }

    });
    streamThread.start();
}
