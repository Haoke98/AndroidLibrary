package com.sadam.sadamlibarary.TTS;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

public class Speak {
    private TextToSpeech tts;
    private Context context;

    public Speak(final Context context) {
        this.context = context;
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                /*如果加载引擎成功*/
                if (status == TextToSpeech.SUCCESS) {
                    /*设置使用中文朗读*/
                    int result = tts.setLanguage(Locale.CHINA);
                    /*如果不支持所设置的语言*/
                    if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE && result != TextToSpeech.LANG_AVAILABLE) {
                        Toast.makeText(context, "TTS暂时不支持这种语言朗读！", Toast.LENGTH_LONG).show();
                    } else {
                        tts.speak("朗读功能正常", TextToSpeech.QUEUE_FLUSH, null, "111");
                        Toast.makeText(context, "朗读功能正常", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void speak(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "111");
    }

    public void shutDown() {
        tts.shutdown();
    }

}
