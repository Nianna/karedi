package main.java.com.github.nianna.karedi.action.play;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.BeatRange;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.util.BeatMillisConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static main.java.com.github.nianna.karedi.action.KarediActions.*;
import static main.java.com.github.nianna.karedi.audio.Player.Mode.*;

@Configuration
class PlayActionsConfiguration {

    private final IntegerBinding playToTheEndStartBeatProperty;

    PlayActionsConfiguration(SongPlayer songPlayer, DisplayContext displayContext) {
        this.playToTheEndStartBeatProperty = Bindings.createIntegerBinding(() -> {
            if (isMarkerVisible(songPlayer, displayContext)) {
                return songPlayer.getMarkerBeat();
            } else {
                return displayContext.getVisibleAreaBounds().getLowerXBound();
            }
        }, songPlayer.markerBeatProperty(), displayContext.getVisibleAreaBounds());
    }

    private boolean isMarkerVisible(SongPlayer songPlayer, DisplayContext displayContext) {
        return displayContext.getVisibleAreaBounds().inBoundsX(songPlayer.getMarkerBeat());
    }

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
    NewKarediAction playMedleyAudio(DisplayContext displayContext, NoteSelection selection, SongPlayer songPlayer) {
        return new PlayMedleyAction(PLAY_MEDLEY_AUDIO, AUDIO_ONLY, displayContext, selection, songPlayer);
    }

    @Bean
    NewKarediAction playMedleyMidi(DisplayContext displayContext, NoteSelection selection, SongPlayer songPlayer) {
        return new PlayMedleyAction(PLAY_MEDLEY_MIDI, MIDI_ONLY, displayContext, selection, songPlayer);
    }

    @Bean
    NewKarediAction playMedleyAudioMidi(DisplayContext displayContext, NoteSelection selection, SongPlayer songPlayer) {
        return new PlayMedleyAction(PLAY_MEDLEY_AUDIO_MIDI, AUDIO_MIDI, displayContext, selection, songPlayer);
    }

    @Bean
    NewKarediAction playVisibleAudioAction(DisplayContext displayContext, SongPlayer songPlayer) {
        return new PlayRangeAction(PLAY_VISIBLE_AUDIO, AUDIO_ONLY, lowerVisibleXBoundProperty(displayContext), upperVisibleXBoundProperty(displayContext), displayContext, songPlayer);
    }

    @Bean
    NewKarediAction playVisibleMidiAction(DisplayContext displayContext, SongPlayer songPlayer) {
        return new PlayRangeAction(PLAY_VISIBLE_MIDI, MIDI_ONLY, lowerVisibleXBoundProperty(displayContext), upperVisibleXBoundProperty(displayContext), displayContext, songPlayer);
    }

    @Bean
    NewKarediAction playVisibleAudioMidiAction(DisplayContext displayContext, SongPlayer songPlayer) {
        return new PlayRangeAction(PLAY_VISIBLE_AUDIO_MIDI, AUDIO_MIDI, lowerVisibleXBoundProperty(displayContext), upperVisibleXBoundProperty(displayContext), displayContext, songPlayer);
    }

    private ReadOnlyObjectProperty<Integer> lowerVisibleXBoundProperty(DisplayContext displayContext) {
        return displayContext.getVisibleAreaBounds().lowerXBoundProperty();
    }

    private ReadOnlyObjectProperty<Integer> upperVisibleXBoundProperty(DisplayContext displayContext) {
        return displayContext.getVisibleAreaBounds().upperXBoundProperty();
    }

    @Bean
    NewKarediAction playAllAudioAction(DisplayContext displayContext, SongPlayer songPlayer, BeatRange beatRange) {
        return new PlayRangeAction(PLAY_ALL_AUDIO, AUDIO_ONLY, beatRange.minBeatProperty(), beatRange.maxBeatProperty(), displayContext, songPlayer);
    }

    @Bean
    NewKarediAction playAllMidiAction(DisplayContext displayContext, SongPlayer songPlayer, BeatRange beatRange) {
        return new PlayRangeAction(PLAY_ALL_MIDI, MIDI_ONLY, beatRange.minBeatProperty(), beatRange.maxBeatProperty(), displayContext, songPlayer);
    }

    @Bean
    NewKarediAction playAllAudioMidiAction(DisplayContext displayContext, SongPlayer songPlayer, BeatRange beatRange) {
        return new PlayRangeAction(PLAY_ALL_AUDIO_MIDI, AUDIO_MIDI, beatRange.minBeatProperty(), beatRange.maxBeatProperty(), displayContext, songPlayer);
    }

    @Bean
    NewKarediAction playToTheEndAudioAction(DisplayContext displayContext, SongPlayer songPlayer, BeatRange beatRange) {
        return new PlayRangeAction(PLAY_TO_THE_END_AUDIO, AUDIO_ONLY, playToTheEndStartBeatProperty, beatRange.maxBeatProperty(), displayContext, songPlayer);
    }

    @Bean
    NewKarediAction playToTheEndMidiAction(DisplayContext displayContext, SongPlayer songPlayer, BeatRange beatRange) {
        return new PlayRangeAction(PLAY_TO_THE_END_MIDI, MIDI_ONLY, playToTheEndStartBeatProperty, beatRange.maxBeatProperty(), displayContext, songPlayer);
    }

    @Bean
    NewKarediAction playToTheEndAudioMidiAction(DisplayContext displayContext, SongPlayer songPlayer, BeatRange beatRange) {
        return new PlayRangeAction(PLAY_TO_THE_END_AUDIO_MIDI, AUDIO_MIDI, playToTheEndStartBeatProperty, beatRange.maxBeatProperty(), displayContext, songPlayer);
    }
}
