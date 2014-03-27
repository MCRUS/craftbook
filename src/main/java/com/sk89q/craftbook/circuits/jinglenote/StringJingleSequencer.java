package com.sk89q.craftbook.circuits.jinglenote;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;

import com.sk89q.craftbook.bukkit.CraftBookPlugin;

/**
 * @author Me4502 with code borrowed from CraftBook Extra
 */
public class StringJingleSequencer implements JingleSequencer {

    String tune;
    int delay;
    int position;
    int taskID;
    boolean isPlaying;
    boolean playedBefore = false;

    List<Note> song;

    private Set<JingleNotePlayer> players = new HashSet<JingleNotePlayer>();

    public StringJingleSequencer(String tune, int delay) {

        this.tune = tune;
        this.delay = delay;
        song = parseTune(tune);
    }

    @Override
    public void run() throws InterruptedException {

        position = 0;
        if(song == null)
            return;

        isPlaying = true;
        playedBefore = true;

        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(CraftBookPlugin.inst(), new Runnable() {

            @Override
            public void run() {

                if (position >= song.size() || !isPlaying || players.isEmpty()) {
                    Bukkit.getScheduler().cancelTask(taskID);
                    isPlaying = false;
                    return;
                }
                for(JingleNotePlayer player : players)
                    player.play(song.get(position));
                position++;
            }

        }, delay, delay);
    }

    public ArrayList<Note> parseTune(String tune) {

        if (tune == null) return null;

        ArrayList<Note> musicKeys = new ArrayList<Note>();

        byte instrument = -1;
        for (int i = 0; i < tune.length(); i++) {
            char first = tune.charAt(i);
            if (first >= '0' && first <= '9') {
                // instrument?
                instrument = getTypeFromChar(first);
            } else if (i + 1 < tune.length()) {
                // note?
                if (instrument == -1) return null;

                int pitch = getPitchFromChar(first);
                boolean skip = false;
                if (pitch == -1) {
                    switch (first) {
                        case '-':
                        case ' ':
                            skip = true;
                            break;
                        default:
                            return null;
                    }
                }

                int octave;
                try {
                    octave = Integer.parseInt(Character.toString(tune.charAt(i + 1)));
                } catch (NumberFormatException e) {
                    octave = 2;
                }

                if (skip) {
                    musicKeys.add(new Note(Instrument.PIANO, (byte) 0, 0));
                    if (octave == 0) octave = 10;

                } else {

                    if (octave < 2) octave = 2;

                    pitch += (octave - 2) * 12;

                    if (pitch < 0) pitch = 0;
                    else if (pitch > 24) pitch = 24;

                    musicKeys.add(new Note(toMCSound(instrument), (byte) pitch, 60F));
                }

                i++;
            }
        }

        if (musicKeys.size() == 0) return null;

        return musicKeys;
    }

    public byte getTypeFromChar(char type) {

        byte instrument = -1;
        switch (type) {
            case '9':
            case '8':
            case '7':
            case '0':
                instrument = 0;
                break;
            case '1':
                instrument = 1;
                break;
            case '2':
                instrument = 2;
                break;
            case '3':
                instrument = 3;
                break;
            case '4':
                instrument = 4;
                break;
            case '5':
                instrument = 5;
                break;
            case '6':
                instrument = 6;
                break;
        }

        return instrument;
    }

    public int getPitchFromChar(char charPitch) {

        int pitch = 0;
        switch (charPitch) {
            case 'f':
                pitch++;
            case 'e':
                pitch++;
            case 'D':
                pitch++;
            case 'd':
                pitch++;
            case 'C':
                pitch++;
            case 'c':
                pitch++;
            case 'b':
                pitch++;
            case 'A':
                pitch++;
            case 'a':
                pitch++;
            case 'G':
                pitch++;
            case 'g':
                pitch++;
            case 'F':
                break;
            default:
                pitch = -1;
                break;
        }

        return pitch;
    }

    protected Instrument toMCSound(byte instrument) {

        switch (instrument) {
            case 1:
                return Instrument.BASS_GUITAR;
            case 2:
                return Instrument.SNARE_DRUM;
            case 3:
                return Instrument.STICKS;
            case 4:
                return Instrument.BASS_DRUM;
            case 5:
                return Instrument.GUITAR;
            case 6:
                return Instrument.BASS;
            default:
                return Instrument.PIANO;
        }
    }

    @Override
    public void stop() {
        isPlaying = false;
    }

    @Override
    public boolean isPlaying () {
        return isPlaying;
    }

    @Override
    public boolean hasPlayedBefore () {
        return playedBefore;
    }

    @Override
    public void stop (JingleNotePlayer player) {
        players.remove(player);
    }

    @Override
    public void play (JingleNotePlayer player) {
        players.add(player);
        if(!playedBefore)
            try {
                run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    @Override
    public Set<JingleNotePlayer> getPlayers () {
        return players;
    }

    @Override
    public boolean isPlaying (JingleNotePlayer player) {
        return players.contains(player);
    }
}