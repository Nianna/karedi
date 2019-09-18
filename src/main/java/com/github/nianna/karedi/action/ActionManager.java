package main.java.com.github.nianna.karedi.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ActionManager {

    private Map<KarediActions, NewKarediAction> actionMap = new HashMap<>();

    @Autowired
    public void mapActions(List<NewKarediAction> karediActions) {
        //TODO warning if already in map
        karediActions.forEach(action -> actionMap.put(action.handles(), action));
    }

    public void addAction(NewKarediAction action) {
        //TODO warning if already in map
        actionMap.put(action.handles(), action);
    }

    public NewKarediAction get(KarediActions key) {
        return actionMap.get(key);
    }

    public void execute(KarediActions action) {
        if (canExecute(action)) {
            getAction(action).handle(null);
        }
    }

    public boolean canExecute(KarediActions action) {
        return getAction(action) != null && !getAction(action).isDisabled();
    }

    public NewKarediAction getAction(KarediActions key) {
        return actionMap.get(key);
    }
}
