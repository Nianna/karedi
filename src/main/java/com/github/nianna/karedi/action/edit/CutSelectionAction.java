package main.java.com.github.nianna.karedi.action.edit;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.action.ActionManager;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.command.ChangePreStateCommandDecorator;
import main.java.com.github.nianna.karedi.command.Command;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.context.NoteSelection;

import static main.java.com.github.nianna.karedi.action.KarediActions.CUT;

class CutSelectionAction extends NewKarediAction {

    private final CommandExecutor commandExecutor;
    private final ActionManager actionManager;
    private final DeleteSelectionAction deleteSelectionAction;

    CutSelectionAction(NoteSelection noteSelection, CommandExecutor commandExecutor, ActionManager actionManager, DeleteSelectionAction deleteSelectionAction) {
        this.commandExecutor = commandExecutor;
        this.actionManager = actionManager;
        this.deleteSelectionAction = deleteSelectionAction;
        setDisabledCondition(noteSelection.isEmptyProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        Command cmd = new ChangePreStateCommandDecorator(
                deleteSelectionAction.getCommand(),
                c -> actionManager.execute(KarediActions.COPY)
        );
        cmd.setTitle(I18N.get("common.cut"));
        commandExecutor.execute(cmd);
    }

    @Override
    public KarediActions handles() {
        return CUT;
    }
}

