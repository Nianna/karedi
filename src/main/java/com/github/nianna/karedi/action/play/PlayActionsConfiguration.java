package main.java.com.github.nianna.karedi.action.play;

import javafx.beans.property.ReadOnlyObjectProperty;
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
    NewKarediAction playVisibleAudioAction(SongContext songContext, SongPlayer songPlayer) {
        return new PlayRangeAction(PLAY_VISIBLE_AUDIO, AUDIO_ONLY, lowerVisibleXBoundProperty(songContext), upperVisibleXBoundProperty(songContext), songContext, songPlayer);
    }

    @Bean
    NewKarediAction playVisibleMidiAction(SongContext songContext, SongPlayer songPlayer) {
        return new PlayRangeAction(PLAY_VISIBLE_MIDI, MIDI_ONLY, lowerVisibleXBoundProperty(songContext), upperVisibleXBoundProperty(songContext), songContext, songPlayer);
    }

    @Bean
    NewKarediAction playVisibleAudioMidiAction(SongContext songContext, SongPlayer songPlayer) {
        return new PlayRangeAction(PLAY_VISIBLE_AUDIO_MIDI, AUDIO_MIDI, lowerVisibleXBoundProperty(songContext), upperVisibleXBoundProperty(songContext), songContext, songPlayer);
    }

    private ReadOnlyObjectProperty<Integer> lowerVisibleXBoundProperty(SongContext songContext) {
        return songContext.getVisibleAreaBounds().lowerXBoundProperty();
    }

    private ReadOnlyObjectProperty<Integer> upperVisibleXBoundProperty(SongContext songContext) {
        return songContext.getVisibleAreaBounds().upperXBoundProperty();
    }

    @Bean
    NewKarediAction playAllAudioAction(SongContext songContext, SongPlayer songPlayer, BeatRange beatRange) {
        return new PlayRangeAction(PLAY_ALL_AUDIO, AUDIO_ONLY, beatRange.minBeatProperty(), beatRange.maxBeatProperty(), songContext, songPlayer);
    }

    @Bean
    NewKarediAction playAllMidiAction(SongContext songContext, SongPlayer songPlayer, BeatRange beatRange) {
        return new PlayRangeAction(PLAY_ALL_MIDI, MIDI_ONLY, beatRange.minBeatProperty(), beatRange.maxBeatProperty(), songContext, songPlayer);
    }

    @Bean
    NewKarediAction playAllAudioMidiAction(SongContext songContext, SongPlayer songPlayer, BeatRange beatRange) {
        return new PlayRangeAction(PLAY_ALL_AUDIO_MIDI, AUDIO_MIDI, beatRange.minBeatProperty(), beatRange.maxBeatProperty(), songContext, songPlayer);
    }

    @Bean
    NewKarediAction playToTheEndAudioAction(SongContext songContext, SongPlayer songPlayer, AppContext appContext, BeatRange beatRange) {
        return new PlayRangeAction(PLAY_TO_THE_END_AUDIO, AUDIO_ONLY, appContext.playToTheEndStartBeatProperty(), beatRange.maxBeatProperty(), songContext, songPlayer);
    }

    @Bean
    NewKarediAction playToTheEndMidiAction(SongContext songContext, SongPlayer songPlayer, AppContext appContext, BeatRange beatRange) {
        return new PlayRangeAction(PLAY_TO_THE_END_MIDI, MIDI_ONLY, appContext.playToTheEndStartBeatProperty(), beatRange.maxBeatProperty(), songContext, songPlayer);
    }

    @Bean
    NewKarediAction playToTheEndAudioMidiAction(SongContext songContext, SongPlayer songPlayer, AppContext appContext, BeatRange beatRange) {
        return new PlayRangeAction(PLAY_TO_THE_END_AUDIO_MIDI, AUDIO_MIDI, appContext.playToTheEndStartBeatProperty(), beatRange.maxBeatProperty(), songContext, songPlayer);
    }
}
