package main.java.com.github.nianna.karedi.action.tag;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.command.tag.ChangeTagValueCommand;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.dialog.EditTagDialog;
import main.java.com.github.nianna.karedi.song.tag.Tag;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static main.java.com.github.nianna.karedi.action.KarediActions.ADD_TAG;

@Component
class AddTagAction extends TagAction {

    private final CommandExecutor commandExecutor;

    AddTagAction(AppContext appContext, DisplayContext displayContext, CommandExecutor commandExecutor) {
        super(displayContext);
        this.commandExecutor = commandExecutor;
    }

    @Override
    protected void onAction(ActionEvent event) {
        EditTagDialog dialog = new EditTagDialog(I18N.get("dialog.new_tag.title"));
        Optional<Tag> result = dialog.showAndWait();
        result.ifPresent(tag -> commandExecutor.execute(new ChangeTagValueCommand(displayContext.getActiveSong(), tag.getKey(), tag.getValue())));
    }

    @Override
    public KarediActions handles() {
        return ADD_TAG;
    }
}