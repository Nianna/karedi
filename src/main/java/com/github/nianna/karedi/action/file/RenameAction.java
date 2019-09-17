package main.java.com.github.nianna.karedi.action.file;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.command.Command;
import main.java.com.github.nianna.karedi.command.CommandComposite;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.command.tag.ChangeTagValueCommand;
import main.java.com.github.nianna.karedi.context.SongContext;
import main.java.com.github.nianna.karedi.dialog.EditFilenamesDialog;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.song.tag.TagKey;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static main.java.com.github.nianna.karedi.action.KarediActions.RENAME;

@Component
 class RenameAction extends NewKarediAction {

    private final SongContext songContext;
    private final CommandExecutor commandExecutor;

    RenameAction(SongContext songContext, CommandExecutor commandExecutor) {
        this.songContext = songContext;
        this.commandExecutor = commandExecutor;
        setDisabledCondition(this.songContext.activeSongIsNullProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        Song song = songContext.getActiveSong();
        EditFilenamesDialog dialog = new EditFilenamesDialog();

        song.getTagValue(TagKey.ARTIST).ifPresent(dialog::setSongArtist);
        song.getTagValue(TagKey.TITLE).ifPresent(dialog::setSongTitle);
        song.getTagValue(TagKey.MP3).ifPresent(dialog::setAudioFilename);
        song.getTagValue(TagKey.COVER).ifPresent(dialog::setCoverFilename);

        Optional<String> optVideoFilename = song.getTagValue(TagKey.VIDEO);
        if (optVideoFilename.isPresent()) {
            dialog.setVideoFilename(optVideoFilename.get());
        } else {
            dialog.hideVideo();
        }

        Optional<String> optBackgroundFilename = song.getTagValue(TagKey.BACKGROUND);
        if (optBackgroundFilename.isPresent()) {
            dialog.setBackgroundFilename(optBackgroundFilename.get());
        } else {
            dialog.hideBackground();
        }

        Optional<EditFilenamesDialog.FilenamesEditResult> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent(result -> commandExecutor.execute(commandFromResults(result)));
    }

    private Command commandFromResults(EditFilenamesDialog.FilenamesEditResult result) {
        return new CommandComposite(I18N.get("ui.common.rename")) {

            @Override
            protected void buildSubCommands() {
                Song song = songContext.getActiveSong();
                addSubCommand(new ChangeTagValueCommand(song, TagKey.ARTIST, result.getArtist()));
                addSubCommand(new ChangeTagValueCommand(song, TagKey.TITLE, result.getTitle()));
                addSubCommand(new ChangeTagValueCommand(song, TagKey.MP3, result.getAudioFilename()));
                addSubCommand(new ChangeTagValueCommand(song, TagKey.COVER, result.getCoverFilename()));
                result.getBackgroundFilename().ifPresent(filename -> addSubCommand(new ChangeTagValueCommand(song, TagKey.BACKGROUND, filename)));
                result.getVideoFilename().ifPresent(filename -> addSubCommand(new ChangeTagValueCommand(song, TagKey.VIDEO, filename)));
            }
        };
    }

    @Override
    public KarediActions handles() {
        return RENAME;
    }
}
