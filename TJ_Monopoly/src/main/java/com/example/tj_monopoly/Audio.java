package com.example.tj_monopoly;

import javax.sound.sampled.*;
import java.net.MalformedURLException;
import java.net.URL;

public class Audio {

    Clip clip;
    URL[] soundURL = new URL[30];

    public Audio(){

        try {
//              Files MUST be WAV and 16 BIT

            soundURL[0] = new URL("file:TJ_Monopoly/src/main/resources/audio/menuSelect.wav");
            soundURL[1] = new URL("file:TJ_Monopoly/src/main/resources/audio/transitionWoosh.wav");
            soundURL[2] = new URL("file:TJ_Monopoly/src/main/resources/audio/legoBuild.wav");
            soundURL[3] = new URL("file:TJ_Monopoly/src/main/resources/audio/starWarsTheme.wav");
            soundURL[4] = new URL("file:TJ_Monopoly/src/main/resources/audio/tatooineTheme.wav");
            soundURL[5] = new URL("file:TJ_Monopoly/src/main/resources/audio/openCard.wav");
            soundURL[6] = new URL("file:TJ_Monopoly/src/main/resources/audio/studPickup.wav");
            soundURL[7] = new URL("file:TJ_Monopoly/src/main/resources/audio/lightsaberActivate.wav");
            soundURL[8] = new URL("file:TJ_Monopoly/src/main/resources/audio/lightsaberRetract.wav");
            soundURL[9] = new URL("file:TJ_Monopoly/src/main/resources/audio/starWarsSiren.wav");

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }


    public void setFile(int i) {
        try{

            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);

        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public void play(){
        clip.start();
    }

    public void stop(){
        clip.stop();
    }

    public void loop(){
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
}
