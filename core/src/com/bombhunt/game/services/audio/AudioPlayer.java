package com.bombhunt.game.services.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.bombhunt.game.services.assets.Assets;

import static java.lang.Float.max;
import static java.lang.Float.min;

/**
 * Created by samuel on 29/03/18.
 * references:
 * https://github.com/libgdx/libgdx/wiki/Threading
 * http://www.badlogicgames.com/forum/viewtopic.php?f=11&t=1084&p=6362&hilit=audio+fade#p6362
 * http://badlogicgames.com/forum/viewtopic.php?f=11&t=11152
 * https://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/Graphics.html
 */

public class AudioPlayer {

    private final float FADE_IN_WINDOW = 2f;
    private final float FADE_OUT_WINDOW = 2f;

    private Thread thread_fade_in;
    private Thread thread_fade_out;

    private float app_volume = 0.5f;
    private float app_sound = 0.5f;

    private Music current_theme_song;

    private static AudioPlayer instance = null;

    private AudioPlayer() {
        current_theme_song = null;
    }

    public static AudioPlayer getInstance() {
        if (instance == null) {
            instance = new AudioPlayer();
        }
        return instance;
    }

    public void dispose() {
        current_theme_song.dispose();
    }

    public float getVolumeThemeSong() {
        return app_volume;
    }

    public void setVolumeThemeSong(float volume) {
        app_volume = volume;
        current_theme_song.setVolume(volume);
        interruptFadeIn();
        // IMPORTANT: not needed for thread_fade_out (will vanish anyway)
    }

    public float getVolumeSoundFX() {
        return app_sound;
    }

    public void setVolumeSoundFX(float volume) {
        app_sound = volume;
    }

    public void setNewThemeSong(String new_theme_song_path) {
        Music new_theme_song = Assets.getInstance().get(new_theme_song_path, Music.class);
        if (current_theme_song != new_theme_song) {
            new_theme_song.setLooping(true);
            if (current_theme_song != null) {
                fadeOut(current_theme_song);
                fadeIn(new_theme_song);
            } else {
                fadeIn(new_theme_song);
            }
            current_theme_song = new_theme_song;
        }
    }

    private void fadeIn(Music music) {
        interruptFadeIn();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                music.setPosition(0);
                float current_volume = 0f;
                float time_window = FADE_IN_WINDOW;
                float adjustment_factor;
                float time_elapsed = 0;
                long prev_frame_id = Gdx.graphics.getFrameId();
                long current_frame_id;
                music.setVolume(current_volume);
                music.play();
                while (current_volume < app_volume) {
                    current_frame_id = Gdx.graphics.getFrameId();
                    if (current_frame_id != prev_frame_id) {
                        prev_frame_id = current_frame_id;
                        time_elapsed += Gdx.graphics.getDeltaTime();
                        adjustment_factor = min(1f, time_elapsed/time_window);
                        current_volume = app_volume*adjustment_factor;
                        music.setVolume(current_volume);
                    }
                }
            }
        };
        thread_fade_in = new Thread(runnable);
        thread_fade_in.start();
    }

    private void fadeOut(Music music) {
        interruptFadeOut();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                float initial_volume = music.getVolume();
                float current_volume = initial_volume;
                float desired_volume = 0f;
                float time_window = FADE_OUT_WINDOW;
                float adjustment_factor;
                float time_elapsed = 0;
                long prev_frame_id = Gdx.graphics.getFrameId();
                long current_frame_id;
                while (current_volume > desired_volume) {
                    current_frame_id = Gdx.graphics.getFrameId();
                    if (current_frame_id != prev_frame_id) {
                        prev_frame_id = current_frame_id;
                        time_elapsed += Gdx.graphics.getDeltaTime();
                        adjustment_factor = max(0f, 1 - time_elapsed / time_window);
                        current_volume = initial_volume * adjustment_factor;
                        music.setVolume(current_volume);
                    }
                }
            }
        };
        thread_fade_out = new Thread(runnable);
        thread_fade_out.start();
    }

    private void interruptFadeIn() {
        interruptThread(thread_fade_in);
    }

    private void interruptFadeOut() {
        interruptThread(thread_fade_out);
    }

    private void interruptThread(Thread thread) {
        if (thread != null) {
            if (thread.isAlive()) {
                thread.interrupt();
            }
        }
    }

    public void playButtonSound() {
        Sound sound = Assets.getInstance().get("digitalButton.mp3", Sound.class);
        playSound(sound);
    }

    public void playSound(Sound sound) {
        playSoundWithFactor(sound, 1);
    }

    public void playSoundWithFactor(Sound sound, float factor) {
        sound.play(app_sound*factor);
    }

}
