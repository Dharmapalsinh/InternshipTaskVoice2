package com.dnc.kt.internshiptaskvoice2

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.dnc.kt.internshiptaskvoice2.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.util.*


//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//    }
//}

class MainActivity : AppCompatActivity(), RecognitionListener,TextToSpeech.OnInitListener {

//    lateinit var s1:SpeechRecognisation

    private val permission = 100
    private lateinit var returnedText: TextView
    private lateinit var toggleButton: ToggleButton
    private lateinit var progressBar: ProgressBar
    private lateinit var speech: SpeechRecognizer
    private lateinit var recognizerIntent: Intent
    private var logTag = "VoiceRecognitionActivity"
    private lateinit var  audioManager:AudioManager

    //
    private var _binding: ActivityMainBinding? =null
    private val binding get() =_binding!!
    var n1=4
    var n2=0
    var sum:Int=0
    private var tts:TextToSpeech? =null

    private fun speak(speaktext:String) {
//        Log.d("temp", n1.toString() + n2.toString() + result )
//        val text=n1.toString()+"into"+n2.toString()
        val text=speaktext
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH,null,"")
    }

    override fun onInit(status: Int) {
        if (status==TextToSpeech.SUCCESS){
            val result=tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language not supported!")
            } else {
//                binding.button.isEnabled = true
            }

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        s1=SpeechRecognisation(this,SpeechRecognizer.createSpeechRecognizer(this),
//            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
//        )

        //
        title = "KotlinApp"
        returnedText = findViewById(R.id.textView)
//        progressBar = findViewById(R.id.progressBar)
//        toggleButton = findViewById(R.id.toggleButton)
//        progressBar.visibility = View.VISIBLE
        speech = SpeechRecognizer.createSpeechRecognizer(this)
        Log.i(logTag, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this))
        speech.setRecognitionListener(this)


        tts=TextToSpeech(this,this)

        //
        GlobalScope.launch {
            for (i in 1..10) {
                n1 = (Math.random() * 9 + 1).toInt()
                n2 = (Math.random() * 9 + 1).toInt()
                sum=n1*n2
                withContext(Dispatchers.Main) {
                    delay(2000)
                    binding.txt.setText(n1.toString() + "*" + n2.toString())
                    delay(1000)
                }
                Log.d("forloop", "$i")
                delay(2000)
                speak(n1.toString() + "into" + n2.toString())

                delay(3000)

//                s1.speech.setRecognitionListener(this@MainActivity)
//                s1.a(this@MainActivity)
                //recognise

                recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "US-en")
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
//        recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 3)

                audioManager= getSystemService(Context.AUDIO_SERVICE) as AudioManager
                audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,0,0)

                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    permission)

            }
        }



//        toggleButton.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                progressBar.visibility = View.VISIBLE
//                progressBar.isIndeterminate = true
//                ActivityCompat.requestPermissions(this@MainActivity,
//                    arrayOf(Manifest.permission.RECORD_AUDIO),
//                    permission)
//            } else {
//                progressBar.isIndeterminate = false
//                progressBar.visibility = View.VISIBLE
//                speech.stopListening()
//            }
//        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

            permission -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager
                    .PERMISSION_GRANTED) {
                    speech.startListening(recognizerIntent)
            } else {
                Toast.makeText(this@MainActivity, "Permission Denied!",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onStop() {
        super.onStop()
        // Shutdown TTS when
        // activity is destroyed
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        //
        speech.destroy()
//        Log.i(logTag, "destroy")
    }
    override fun onReadyForSpeech(params: Bundle?) {

    }
    override fun onRmsChanged(rmsdB: Float) {
//        progressBar.progress = rmsdB.toInt()
    }
    override fun onBufferReceived(buffer: ByteArray?) {

    }
    override fun onPartialResults(partialResults: Bundle?) {

    }
    override fun onEvent(eventType: Int, params: Bundle?) {

    }
    override fun onBeginningOfSpeech() {
//        Log.i(logTag, "onBeginningOfSpeech")
//        progressBar.isIndeterminate = false
//        progressBar.max = 10
    }
    override fun onEndOfSpeech() {
//        progressBar.isIndeterminate = true
//        toggleButton.isChecked = false
    }
    override fun onError(error: Int) {
        val errorMessage: String = getErrorText(error)
        Log.d(logTag, "FAILED $errorMessage")
        returnedText.text = errorMessage
//        toggleButton.isChecked = false
    }
    private fun getErrorText(error: Int): String {
        var message = ""
        message = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER -> "error from server"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Didn't understand, please try again."
        }
        return message
    }
    override fun onResults(results: Bundle?) {
        Log.i(logTag, "onResults")
        val matches = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        var text = ""
        if (matches != null) {
            for (result in matches) text = """
          $result
          """.trimIndent()
        }
        returnedText.text = text
//        var sum=(n1*n2)
//        var temp=returnedText.text
        Log.d("sum",sum.toString())
        if (text==sum.toString()){
            speak("Your answer is Correct")
            Toast.makeText(this,"Your Answer Is Correct :) ",Toast.LENGTH_SHORT).show()
        }
        else{
            speak("Your answer is wrong")
            Toast.makeText(this,"Your Answer Is Wrong :( ",Toast.LENGTH_SHORT).show()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        // Shutdown TTS when
        // activity is destroyed
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        audioManager= getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,100,0)
    }
}