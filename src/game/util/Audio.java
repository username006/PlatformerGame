package game.util;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.util.HashMap;


public class Audio {
    private static HashMap<String, Clip> clips;
    private static int gap;
    private static boolean mute = false;

    public void init() {
        clips = new HashMap<>();
        gap = 0;
    }

    public void load(String s, String n) {
        if(clips.get(n) != null) return;
        Clip clip;
        try {
            AudioInputStream ais =
                    AudioSystem.getAudioInputStream(
                            Audio.class.getResourceAsStream(s)
                    );
            AudioFormat baseFormat = ais.getFormat();
            AudioFormat decodeFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false
            );
            AudioInputStream dais = AudioSystem.getAudioInputStream(decodeFormat, ais);
            clip = AudioSystem.getClip();
            clip.open(dais);
            clips.put(n, clip);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void play(String s) {
        play(s, gap);
    }

    public void play(String s, int i) {
        if(mute) return;
        Clip c = clips.get(s);
        if(c == null) return;
        if(c.isRunning()) c.stop();
        c.setFramePosition(i);
        while(!c.isRunning()) c.start();
    }

    public void stop(String s) {
        if(clips.get(s) == null) return;
        if(clips.get(s).isRunning()) clips.get(s).stop();
    }

    public void resume(String s) {
        if(mute) return;
        if(clips.get(s).isRunning()) return;
        clips.get(s).start();
    }

    public void loop(String s) {
        loop(s, gap, gap, clips.get(s).getFrameLength() - 1);
    }

    public void loop(String s, int frame) {
        loop(s, frame, gap, clips.get(s).getFrameLength() - 1);
    }

    public void loop(String s, int start, int end) {
        loop(s, gap, start, end);
    }

    public  void loop(String s, int frame, int start, int end) {
        stop(s);
        if(mute) return;
        clips.get(s).setLoopPoints(start, end);
        clips.get(s).setFramePosition(frame);
        clips.get(s).loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void setPosition(String s, int frame) {
        clips.get(s).setFramePosition(frame);
    }

    public int getFrames(String s) { return clips.get(s).getFrameLength(); }
    public int getPosition(String s) { return clips.get(s).getFramePosition(); }

    public void close(String s) {
        stop(s);
        clips.get(s).close();
    }
}
