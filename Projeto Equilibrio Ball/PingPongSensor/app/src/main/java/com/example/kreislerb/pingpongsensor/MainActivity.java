package com.example.kreislerb.pingpongsensor;



import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{


    //Definiçoes do Bluetooth
    protected BluetoothAdapter btfAdapter;
    public static ConnectionThread connect;
    public static int ENABLE_BLUETOOTH = 1;
    public static int SELECT_PAIRED_DEVICE = 2;
    public static int SELECT_DISCOVERED_DEVICE = 3;
    protected static TextView statusMessage;
    protected static Button  btn_enviar, btn_start;
    protected static EditText messageBox;
    protected static LinearLayout home;
    final Context context = this;
    MenuItem itemStop, itemConfig;
    public boolean controlItemActionBar = false;

    //Constantes de Controle
    private  double set_pointX ;
    private  double set_pointY ;
    private  double kp;
    private  double ki;
    private  double kd;
    String FILENAME = "bd.txt";


    //Definicoes de PDI

    private static final String  TAG              = "OCVSample::Activity";

    private Mat mRgba;
    private Scalar CONTOUR_COLOR = new Scalar(220,100,100);
    private Scalar               CENTER_COLOR = new Scalar(100,220,100);
    private Mat  circles, grayImg, filtroGauss;

    private CameraBridgeViewBase mOpenCvCameraView;


    //Limites de posiçao para fator de conversão
    static final double x_Max = 240;
    static final double y_Max = 160;
    static final double radius_Max = 200;
    static final double radius_Min = 5;



    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        statusMessage = (TextView) findViewById(R.id.statusMessage);
        btn_enviar = (Button) findViewById(R.id.button2);
        btn_start = (Button) findViewById(R.id.button1);
        messageBox = (EditText) findViewById(R.id.editText_MessageBox);
        home = (LinearLayout) findViewById(R.id.layoutPrincpal);


        //Bluetooth adapter
        btfAdapter = BluetoothAdapter.getDefaultAdapter();
        // Verifica se o dispositivo suporta bluetooth
        if(btfAdapter==null){
            toast("Bluetooth não disponível neste dispositivo.");
            finish();
            return;}
        else{
            //Verifica se o bluetooth esta ativado

            if(btfAdapter.isEnabled()){/*Ligado*/}
            else{
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, 0);

            }

        }
        //Atribuir titulo ao action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Inicio");

        //Se estiver conectado em algum dispositivo
        try{
            if(connect.isConnected()){
                Log.i("Bluetooth", "Status: Conectado!");
                statusMessageConnected();
                setEnable(true);
            }
        }
        catch(RuntimeException erro){
            Log.i("Bluetooth", "Status: Aguardando conexão!");
            statusMessageDefault();
            setEnable(false);
        }


        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.color_blob_detection_activity_surface_view);
        mOpenCvCameraView.setMaxFrameSize(256,256);
        cameraVisibility(false, SurfaceView.INVISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        lerArquivo();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == ENABLE_BLUETOOTH) {
            if(resultCode == RESULT_OK) {
                toast("Bluetooth ativado :D");
            }
            else {
                toast("Bluetooth não ativado :(");

            }
        }
        else if(requestCode == SELECT_PAIRED_DEVICE || requestCode == SELECT_DISCOVERED_DEVICE){
            if(resultCode == RESULT_OK) {
                toast("Você selecionou " + data.getStringExtra("btDevName") + "\n"
                        + data.getStringExtra("btDevAddress"));
                connect = new ConnectionThread(data.getStringExtra("btDevAddress"));
                connect.start();

            }
            else {
                toast("Nenhum dispositivo selecionado :(");
            }
        }
    }

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String dataString = new String(data);

            if (dataString.equals("---N")) {
                Log.i("Bluetooth", "Status: Ocorreu um erro durante a conexão!");

                statusMessageErro();
                setEnable(false);
            } else if (dataString.equals("---S")) {
                Log.i("Bluetooth", "Status: Conectado!");
                statusMessageConnected();
                setEnable(true);
            }
            else {
               Log.i("Bluetooth","Recebido: "+ dataString);
            }

        }
    };

    public void sendMessage(View view) {
        try {
            if (connect.isConnected()) {
                String messageBoxString = messageBox.getText().toString();
                connect.write(messageBoxString);
            }
        }
        catch(Exception erro){
            erro.printStackTrace();
            if(messageBox.getText().toString().equals("enable")){
                setEnable(true);
            }
            else{
                toast("A mensagem não foi enviada");
                setEnable(false);
                statusMessageDefault();
                Log.i("Bluetooth", "O bluetooth não esta conectado!");
            }
        }

    }


    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        circles = new Mat();
        grayImg = new Mat(height, width,CvType.CV_8SC1);
        filtroGauss = new Mat();
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }


    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
       if(connect.isConnected()) {

            Imgproc.cvtColor(mRgba, grayImg, Imgproc.COLOR_RGB2GRAY);
            double minDist = 20;//20
           // min and max radii (set these values as you desire)
           int minRadius = 0;
           int maxRadius = 30;
           double dp = 1d;
           // param1 = gradient value used to handle edge detection
           // param2 = Accumulator threshold value for the
           // cv2.CV_HOUGH_GRADIENT method.
           // The smaller the threshold is, the more circles will be
           // detected (including false circles).
           // The larger the threshold is, the more circles will
           // potentially be returned.
           double param1 = 25;//150
           double param2 = 20;
            //Teste 1 param2 = 50
            //Teste 2 param2 = 40
            //Teste 3 param2 = 30  Apresentrou uma sensibilidade boa
            //Teste 4 Manteve-se accumulator 30 e add filtro de gauss com s = 9,9   e 2,2     Melhorou

            Size s = new Size(7,7);
            Imgproc.GaussianBlur(grayImg, filtroGauss, s,2,2);
            //Imgproc.adaptiveThreshold(filtroGauss,filtroGauss,255,Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,Imgproc.THRESH_BINARY,11,3);
            Imgproc.HoughCircles(filtroGauss, circles, Imgproc.CV_HOUGH_GRADIENT, dp, minDist, param1, param2, minRadius, maxRadius);


            int thickness = 1;
            Log.i("Circulos encontrados", " = " + circles.cols());
            if (circles.cols() > 0) {
                double[] circle = circles.get(0, 0);
                final double centerX = circle[0], centerY = circle[1], radius = circle[2];
                // Log.i("Posição Circulo", "X = "+centerX+"   Y: "+centerY+"  Raio: "+radius);
                Point center = new Point(centerX, centerY);
                // Imgproc.cvtColor(imagemFiltrada,	imagemFiltrada,	Imgproc.COLOR_GRAY2RGB);


                Imgproc.circle(filtroGauss, center, (int) radius, CONTOUR_COLOR, thickness);
                Imgproc.circle(filtroGauss, center, 2, CENTER_COLOR, thickness);



                    double[] pos = posicaoCorrigida(centerX, centerY, radius);


                        Log.i("Posição enviada: ", pos[0] + "X \t" + pos[1] + " Y \t" + pos[2] + " Raio");

                            connect.write(pos[0]+"");
                            Log.i("Dado Enviado: ",pos[0]+"");



            }

        }
        else{
           Log.i("Bluetooth: ","Sem conexão");
        }
        circles.release();
        grayImg.release();
        return filtroGauss;
    }


    public void buttomIniciarLeitura(View v) {

        //inicializarParametros();
        homeVisible(false, View.INVISIBLE);
        cameraVisibility(true, SurfaceView.VISIBLE);
        controlItemActionBar = true;
        supportInvalidateOptionsMenu();


    }
    public void pararLeitura(){
        cameraVisibility(false, SurfaceView.INVISIBLE);
        homeVisible(true, View.VISIBLE);
        controlItemActionBar = false;
        supportInvalidateOptionsMenu();

    }

    public void cameraVisibility(boolean status, int visible){
        mOpenCvCameraView.setEnabled(status);
        mOpenCvCameraView.setVisibility(visible);
    }
    public void homeVisible(boolean status, int visible){
        home.setEnabled(status);
        home.setVisibility(visible);
    }

    public void inicializarParametros(){

        String parametros = set_pointX+
                        "!"+set_pointY+
                        "@"+kp+
                        "#"+ki+
                        "$"+kd;
        Log.i("ParametrosInicializados", parametros);
        connect.write(parametros);
    }

    public void configure (){
        Intent c1 = new Intent(this, Configure.class);
        startActivity(c1);
    }



    /**
     * Calcula a nova referencia de posiçoes x,y e o raio do circulo. A função determina que a origem
     * é no centro da imagem. O raio é convertido para altura considerando a origem em seu valor minimo atingido
     * @param x Posição X atual do circulo
     * @param y Posição Y atual do circulo
     * @param radius Dimensão do raio atual do circulo
     * @return Vetor contendo as novas posições x,y e raio respectivamente,
     */
    public double[] posicaoCorrigida(double x, double y, double radius){

        double[] posicaoReal = new double[3];
        posicaoReal[0]=  x;//x - (x_Max/2);
        posicaoReal[1]=  y - (y_Max/2);
        posicaoReal[2]=  radius-radius_Min;
        posicaoReal[2]= Double.parseDouble(String.format(Locale.US, "%.2f", posicaoReal[2]));

        return posicaoReal;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        itemStop = menu.findItem(R.id.actionStop);
        itemConfig = menu.findItem(R.id.action_configure);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.action_paireds:
                Intent searchPairedDevicesIntent = new Intent(this, PairedDevices.class);
                startActivityForResult(searchPairedDevicesIntent, SELECT_PAIRED_DEVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    this.invalidateOptionsMenu();
                }
                return true;

            case R.id.action_search_devices:
                Intent searchDiscoverDevicesIntent = new Intent(this, DiscoveredDevices.class);
                startActivityForResult(searchDiscoverDevicesIntent, SELECT_DISCOVERED_DEVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    this.invalidateOptionsMenu();
                }
                return true;

            case R.id.action_visible:
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 30);
                startActivity(discoverableIntent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    this.invalidateOptionsMenu();
                }
                return true;

            case R.id.actionStop:
                pararLeitura();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    this.invalidateOptionsMenu();
                }
                return true;

            case R.id.action_configure:
                configure();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    this.invalidateOptionsMenu();
                }
                return true;
            case R.id.action_desconect:

                toast("Isto sera implementado em uma proxima compilação");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    this.invalidateOptionsMenu();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        super.onPrepareOptionsMenu(menu);

            if (controlItemActionBar) {
                itemStop.setEnabled(true);
                itemStop.setVisible(true);
                itemConfig.setEnabled(false);
                itemConfig.setVisible(false);
            } else {
                itemStop.setEnabled(false);
                itemStop.setVisible(false);
                itemConfig.setEnabled(true);
                itemConfig.setVisible(true);

            }


        return true;
    }

    private static void setEnable(boolean enable){
        btn_start.setEnabled(enable);
    }
    private static void statusMessageErro(){
        statusMessage.setBackgroundColor(Color.rgb(255,87,87));
        statusMessage.setTextColor(Color.WHITE);
        statusMessage.setText("Status: Ocorreu um erro durante a conexão!");
    }
    private static void statusMessageDefault(){
        statusMessage.setBackgroundColor(Color.WHITE);
        statusMessage.setTextColor(Color.BLACK);
        statusMessage.setText("Status: Aguardando conexão");
    }
    private static void statusMessageConnected(){
        statusMessage.setBackgroundColor(Color.rgb(115,200,85));
        statusMessage.setTextColor(Color.WHITE);
        statusMessage.setText("Status: Conectado :)");
    }

    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void lerArquivo(){

        FileInputStream in = null;
        try {
            in = openFileInput(FILENAME);
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            Log.i("FileRead", sb.toString());
            String dadosLidos[] = sb.toString().split("#");
            set_pointX = Double.parseDouble(dadosLidos[0]);
            set_pointY = Double.parseDouble(dadosLidos[1]);
            kp = Double.parseDouble(dadosLidos[2]);
            ki = Double.parseDouble(dadosLidos[3]);
            kd = Double.parseDouble(dadosLidos[4]);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            toast("Arquivo não encontrado!");
        } catch (IOException e) {
            e.printStackTrace();
            toast("Falha ao abrir!");
        }
        catch (NullPointerException e){
            e.printStackTrace();
            toast("Vetores nulos!");
        }
    }




}