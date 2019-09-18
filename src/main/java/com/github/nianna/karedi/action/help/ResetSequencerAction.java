package main.java.com.github.nianna.karedi.action.help;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.audio.MidiPlayer;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.RESET_SEQUENCER;

@Component
public class ResetSequencerAction extends KarediAction {

    private ResetSequencerAction(DisplayContext displayContext) {
        setDisabledCondition(displayContext.activeSongProperty().isNull());
    }

    @Override
    protected void onAction(ActionEvent event) {
        MidiPlayer.reset();
    }

    @Override
    public KarediActions handles() {
        return RESET_SEQUENCER;
    }
}