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
    NewKarediAction playSelectionAudio(NoteSelection selection, AppContext appContext, SongPlayer songPlayer, BeatMillisConverter beatMillisConverter) {
        return new PlaySelectionAction(PLAY_SELECTION_AUDIO, AUDIO_ONLY, selection, appContext, songPlayer, beatMillisConverter);
    }

    @Bean
    NewKarediAction playSelectionMidi(NoteSelection selection, AppContext appContext, SongPlayer songPlayer, BeatMillisConverter beatMillisConverter) {
        return new PlaySelectionAction(PLAY_SELECTION_MIDI, MIDI_ONLY, selection, appContext, songPlayer, beatMillisConverter);
    }

    @Bean
    NewKarediAction playSelectionAudioMidi(NoteSelection selection, AppContext appContext, SongPlayer songPlayer, BeatMillisConverter beatMillisConverter) {
        return new PlaySelectionAction(PLAY_SELECTION_AUDIO_MIDI, AUDIO_MIDI, selection, appContext, songPlayer, beatMillisConverter);
    }

    @Bean
    NewKarediAction playMedleyAudio(SongContext songContext, AppContext appContext, NoteSelection selection) {
        return new PlayMedleyAction(PLAY_MEDLEY_AUDIO, AUDIO_ONLY, songContext, appContext, selection);
    }

    @Bean
    NewKarediAction playMedleyMidi(SongContext songContext, AppContext appContext, NoteSelection selection) {
        return new PlayMedleyAction(PLAY_MEDLEY_MIDI, MIDI_ONLY, songContext, appContext, selection);
    }

    @Bean
    NewKarediAction playMedleyAudioMidi(SongContext songContext, AppContext appContext, NoteSelection selection) {
        return new PlayMedleyAction(PLAY_MEDLEY_AUDIO_MIDI, AUDIO_MIDI, songContext, appContext, selection);
    }

    @Bean
    NewKarediAction playVisibleAudioAction(VisibleArea visibleArea, SongContext songContext, AppContext appContext) {
        return new PlayRangeAction(PLAY_VISIBLE_AUDIO, AUDIO_ONLY, visibleArea.lowerXBoundProperty(), visibleArea.upperXBoundProperty(), songContext, appContext);
    }

    @Bean
    NewKarediAction playVisibleMidiAction(VisibleArea visibleArea, SongContext songContext, AppContext appContext) {
        return new PlayRangeAction(PLAY_VISIBLE_MIDI, MIDI_ONLY, visibleArea.lowerXBoundProperty(), visibleArea.upperXBoundProperty(), songContext, appContext);
    }

    @Bean
    NewKarediAction playVisibleAudioMidiAction(VisibleArea visibleArea, SongContext songContext, AppContext appContext) {
        return new PlayRangeAction(PLAY_VISIBLE_AUDIO_MIDI, AUDIO_MIDI, visibleArea.lowerXBoundProperty(), visibleArea.upperXBoundProperty(), songContext, appContext);
    }

    @Bean
    NewKarediAction playAllAudioAction(VisibleArea visibleArea, SongContext songContext, AppContext appContext) {
        return new PlayRangeAction(PLAY_ALL_AUDIO, AUDIO_ONLY, appContext.minBeatProperty(), appContext.maxBeatProperty(), songContext, appContext);
    }

    @Bean
    NewKarediAction playAllMidiAction(VisibleArea visibleArea, SongContext songContext, AppContext appContext) {
        return new PlayRangeAction(PLAY_ALL_MIDI, MIDI_ONLY, appContext.minBeatProperty(), appContext.maxBeatProperty(), songContext, appContext);
    }

    @Bean
    NewKarediAction playAllAudioMidiAction(VisibleArea visibleArea, SongContext songContext, AppContext appContext) {
        return new PlayRangeAction(PLAY_ALL_AUDIO_MIDI, AUDIO_MIDI, appContext.minBeatProperty(), appContext.maxBeatProperty(), songContext, appContext);
    }

    @Bean
    NewKarediAction playToTheEndAudioAction(VisibleArea visibleArea, SongContext songContext, AppContext appContext) {
        return new PlayRangeAction(PLAY_TO_THE_END_AUDIO, AUDIO_ONLY, appContext.playToTheEndStartBeatProperty(), appContext.maxBeatProperty(), songContext, appContext);
    }

    @Bean
    NewKarediAction playToTheEndMidiAction(VisibleArea visibleArea, SongContext songContext, AppContext appContext) {
        return new PlayRangeAction(PLAY_TO_THE_END_MIDI, MIDI_ONLY, appContext.playToTheEndStartBeatProperty(), appContext.maxBeatProperty(), songContext, appContext);
    }

    @Bean
    NewKarediAction playToTheEndAudioMidiAction(VisibleArea visibleArea, SongContext songContext, AppContext appContext) {
        return new PlayRangeAction(PLAY_TO_THE_END_AUDIO_MIDI, AUDIO_MIDI, appContext.playToTheEndStartBeatProperty(), appContext.maxBeatProperty(), songContext, appContext);
    }
}
