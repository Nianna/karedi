package main.java.com.github.nianna.karedi.action.play;

import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.*;
import main.java.com.github.nianna.karedi.util.BeatMillisConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static main.java.com.github.nianna.karedi.action.KarediActions.*;
import static main.java.com.github.nianna.karedi.audio.Player.Mode.*;

@Configuration
class PlayActionsConfiguration {

    @Bean
    NewKarediAction playSelectionAudio(NoteSelection selection, SongPlayer songPlayer, BeatMillisConverter beatMillisConverter) {
        return new PlaySelectionAction(PLAY_SELECTION_AUDIO, AUDIO_ONLY, selection, songPlayer, beatMillisConverter);
    }

    @Bean
    NewKarediAction playSelectionMidi(NoteSelection selection, SongPlayer songPlayer, BeatMillisConverter beatMillisConverter) {
        return new PlaySelectionAction(PLAY_SELECTION_MIDI, MIDI_ONLY, selection, songPlayer, beatMillisConverter);
    }

    @Bean
    NewKarediAction playSelectionAudioMidi(NoteSelection selection, SongPlayer songPlayer, BeatMillisConverter beatMillisConverter) {
        return new PlaySelectionAction(PLAY_SELECTION_AUDIO_MIDI, AUDIO_MIDI, selection, songPlayer, beatMillisConverter);
    }

    @Bean
    NewKarediAction playMedleyAudio(SongContext songContext, NoteSelection selection, SongPlayer songPlayer) {
        return new PlayMedleyAction(PLAY_MEDLEY_AUDIO, AUDIO_ONLY, songContext, selection, songPlayer);
    }

    @Bean
    NewKarediAction playMedleyMidi(SongContext songContext, NoteSelection selection, SongPlayer songPlayer) {
        return new PlayMedleyAction(PLAY_MEDLEY_MIDI, MIDI_ONLY, songContext, selection, songPlayer);
    }

    @Bean
    NewKarediAction playMedleyAudioMidi(SongContext songContext, NoteSelection selection, SongPlayer songPlayer) {
        return new PlayMedleyAction(PLAY_MEDLEY_AUDIO_MIDI, AUDIO_MIDI, songContext, selection, songPlayer);
    }

    @Bean
    NewKarediAction playVisibleAudioAction(VisibleArea visibleArea, SongContext songContext, SongPlayer songPlayer) {
        return new PlayRangeAction(PLAY_VISIBLE_AUDIO, AUDIO_ONLY, visibleArea.lowerXBoundProperty(), visibleArea.upperXBoundProperty(), songContext, songPlayer);
    }

    @Bean
    NewKarediAction playVisibleMidiAction(VisibleArea visibleArea, SongContext songContext, SongPlayer songPlayer) {
        return new PlayRangeAction(PLAY_VISIBLE_MIDI, MIDI_ONLY, visibleArea.lowerXBoundProperty(), visibleArea.upperXBoundProperty(), songContext, songPlayer);
    }

    @Bean
    NewKarediAction playVisibleAudioMidiAction(VisibleArea visibleArea, SongContext songContext, SongPlayer songPlayer) {
        return new PlayRangeAction(PLAY_VISIBLE_AUDIO_MIDI, AUDIO_MIDI, visibleArea.lowerXBoundProperty(), visibleArea.upperXBoundProperty(), songContext, songPlayer);
    }

    @Bean
    NewKarediAction playAllAudioAction(VisibleArea visibleArea, SongContext songContext, SongPlayer songPlayer, AppContext appContext) {
        return new PlayRangeAction(PLAY_ALL_AUDIO, AUDIO_ONLY, appContext.minBeatProperty(), appContext.maxBeatProperty(), songContext, songPlayer);
    }

    @Bean
    NewKarediAction playAllMidiAction(VisibleArea visibleArea, SongContext songContext, SongPlayer songPlayer, AppContext appContext) {
        return new PlayRangeAction(PLAY_ALL_MIDI, MIDI_ONLY, appContext.minBeatProperty(), appContext.maxBeatProperty(), songContext, songPlayer);
    }

    @Bean
    NewKarediAction playAllAudioMidiAction(VisibleArea visibleArea, SongContext songContext, SongPlayer songPlayer, AppContext appContext) {
        return new PlayRangeAction(PLAY_ALL_AUDIO_MIDI, AUDIO_MIDI, appContext.minBeatProperty(), appContext.maxBeatProperty(), songContext, songPlayer);
    }

    @Bean
    NewKarediAction playToTheEndAudioAction(VisibleArea visibleArea, SongContext songContext, SongPlayer songPlayer, AppContext appContext) {
        return new PlayRangeAction(PLAY_TO_THE_END_AUDIO, AUDIO_ONLY, appContext.playToTheEndStartBeatProperty(), appContext.maxBeatProperty(), songContext, songPlayer);
    }

    @Bean
    NewKarediAction playToTheEndMidiAction(VisibleArea visibleArea, SongContext songContext, SongPlayer songPlayer, AppContext appContext) {
        return new PlayRangeAction(PLAY_TO_THE_END_MIDI, MIDI_ONLY, appContext.playToTheEndStartBeatProperty(), appContext.maxBeatProperty(), songContext, songPlayer);
    }

    @Bean
    NewKarediAction playToTheEndAudioMidiAction(VisibleArea visibleArea, SongContext songContext, SongPlayer songPlayer, AppContext appContext) {
        return new PlayRangeAction(PLAY_TO_THE_END_AUDIO_MIDI, AUDIO_MIDI, appContext.playToTheEndStartBeatProperty(), appContext.maxBeatProperty(), songContext, songPlayer);
    }
}
