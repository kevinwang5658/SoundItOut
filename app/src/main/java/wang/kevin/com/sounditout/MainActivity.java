package wang.kevin.com.sounditout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements RecognitionListener{
    private static final String TAG = "MainActivity";
    private int requestAudioRecord;
    boolean errorFlag = false;
    boolean toggleMicrophone = false;
    SpeechRecognizer speechRecognizer;
    TextToSpeech textToSpeech;
    Intent recognitionIntent;
    ImageButton speechButton;
    ImageButton speakerButton;
    ImageButton infoButton;
    TextView outputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
        setup();
        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleMicrophone == false) {
                    startSpeechRecognition();
                } else {
                    endSpeechRecognition();
                }
            }
        });
        speakerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(outputText.getText().equals("")) && errorFlag != true){
                    textToSpeech.speak(outputText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment infoFragment = new InfoPopUp();
                infoFragment.show(getSupportFragmentManager(), "Info");
            }
        });
    }

    void requestPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, requestAudioRecord);
            }
        }
    }

    void setup(){
        speechButton = (ImageButton) findViewById(R.id.speech_button);
        speakerButton = (ImageButton) findViewById(R.id.speaker_button);
        infoButton = (ImageButton) findViewById(R.id.info_button);
        outputText = (TextView) findViewById(R.id.output_text);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(this);
        recognitionIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognitionIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognitionIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        recognitionIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "wang.kevin.com.sounditout");
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR){
                    textToSpeech.setLanguage(Locale.CANADA);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setup();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (speechRecognizer != null) {
            endSpeechRecognition();
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.d(TAG, "beginning");
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {
        Log.d(TAG, "end");
        endSpeechRecognition();
    }

    @Override
    public void onError(int i) {
        errorFlag = true;
        endSpeechRecognition();
        Log.d(TAG, getErrorText(i));
    }

    @Override
    public void onResults(Bundle results) {
        Log.d(TAG, "result");
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
    }

    @Override
    public void onPartialResults(Bundle results) {
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        Log.d(TAG, "partial result");
        String matchString = matches.get(0);
        if (matchString.length() > 0) {
            String stringArray[] = matchString.split(" ", 2);
            String firstWord = stringArray[0];
            firstWord = firstWord.substring(0, 1).toUpperCase() + firstWord.substring(1);
            outputText.setText(firstWord);
            endSpeechRecognition();
            errorFlag = false;
        }
    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                outputText.setText("Sorry You Didn't Allow me to Use The Microphone");
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                outputText.setText("Sorry There's a Network Problem");
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                outputText.setText("Sorry There's a Network Problem");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                outputText.setText("Sorry Couldn't Understand You");
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                outputText.setText("Sorry You Didn't Say Anything");
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    void startSpeechRecognition(){
        toggleMicrophone = true;
        speechRecognizer.startListening(recognitionIntent);
        speechButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.microphone_on, null));
    }

    void endSpeechRecognition(){
        toggleMicrophone = false;
        speechRecognizer.cancel();
        speechButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.microphone_off, null));
    }
}
