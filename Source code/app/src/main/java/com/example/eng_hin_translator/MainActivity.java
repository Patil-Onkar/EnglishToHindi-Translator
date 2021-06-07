package com.example.eng_hin_translator;

import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

import android.view.Menu;
import android.view.MenuItem;



public class MainActivity extends AppCompatActivity {

    private Interpreter tfLite;

    private Button translateButton;
    private EditText userInput;
    private TextView outputSentence;
    private static final int maxhin=5002;
    private static final int maxeng=5002;
    private ArrayList<String> englishTokenList = new ArrayList<>();
    private ArrayList<String> hindiTokenList = new ArrayList<>();

    @SuppressWarnings("deprecation")


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        translateButton =findViewById(R.id.button);
        userInput = findViewById(R.id.editText);
        outputSentence = findViewById(R.id.textView2);

        try {
            tfLite = new Interpreter(loadModelFile(this.getAssets(),"model.tflite"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadJson();
//        FloatingActionButton fab = findViewById(R.id.fab);
        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] words = userInput.getText().toString().split("\\s+");

                int wordsLength = words.length;

                int[][] inputArray = new int[1][13];

                if(wordsLength > 11)
                {
                    inputArray[0][0]=5000;
                    for(int i = 1; i < 13; i++)
                        inputArray[0][i] = getTokenNumber(englishTokenList,words[i-1]);
                }
                else
                {
                    inputArray[0][0]=5000;
                    for(int i = 1; i < 13; i++) {
                        if(i > wordsLength) {
                            if(i == wordsLength+1) {
                                inputArray[0][i] = 5001;
                            }
                            else
                                inputArray[0][i] = 0;
                        }
                        else
                            inputArray[0][i] = getTokenNumber(englishTokenList, words[i-1]);
                    }
                }

                String res = runModel(inputArray,hindiTokenList);
                outputSentence.setText(res);


            }
        });
    }

    private void loadJson(){

        ProgressDialog pd = new ProgressDialog(MainActivity.this);

        pd.setMessage("Loading Data..");

        pd.show();

        JSONObject hindiJsonObject = null;
        JSONObject englishJsonObject =  null;
        try {
            hindiJsonObject = new JSONObject(loadJSONFromAsset("hn_itow.json"));
            englishJsonObject = new JSONObject(loadJSONFromAsset("en_itow.json"));
            for(int i = 1; i< maxhin; i++)
                hindiTokenList.add((String)hindiJsonObject.get(String.valueOf(i)));

            for(int i = 1; i< maxeng; i++)
                englishTokenList.add((String)englishJsonObject.get(String.valueOf(i)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        pd.dismiss();

    }

    private int getTokenNumber(ArrayList<String> list,String key){

        if(list.contains(key)) {
            return list.indexOf(key) + 1;
        }
        else {
            return 0;
        }
    }

    private String getWordFromToken(ArrayList<String> list,int key){

        if((key == 0) || (key == 5001))
            return "";
        else
            return list.get(key-1);

    }

    private static MappedByteBuffer loadModelFile(AssetManager assets, String modelFilename)throws IOException {
        AssetFileDescriptor fileDescriptor = assets.openFd(modelFilename);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public String loadJSONFromAsset(String name) {
        String json = null;
        try {
            InputStream is = MainActivity.this.getAssets().open(name);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private String runModel(int[][] inputVal,ArrayList<String> list){

        long [] outputVal = new long [12];

        Log.d("ADebugTag", "Value: " + Arrays.deepToString(inputVal));

        tfLite.run(inputVal,outputVal);

        Log.d("ADebugTag2", "Value: " + Arrays.toString(outputVal));

        StringBuilder stringBuilder = new StringBuilder();

        long [] aint = outputVal;

        for (long a:aint) {
            int tmp = (int) a;
            stringBuilder.append(getWordFromToken(list,tmp));
            stringBuilder.append(" ");
        }

        return stringBuilder.toString();
    }

    private static int argMax(float[] floatArray) {

        float max = floatArray[0];
        int index = 0;

        for (int i = 0; i < floatArray.length; i++)
        {
            if (max < floatArray[i])
            {
                max = floatArray[i];
                index = i;
            }
        }
        return index;
    }
}
