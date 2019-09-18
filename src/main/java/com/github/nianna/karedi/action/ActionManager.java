package main.java.com.github.nianna.karedi.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ActionManager {

    private Map<KarediActions, KarediAction> actionMap = new HashMap<>();

    @Autowired
    public void mapActions(List<KarediAction> karediActions) {
        //TODO warning if already in map
        karediActions.forEach(action -> actionMap.put(action.handles(), action));
    }

    public void addAction(KarediAction action) {
        //TODO warning if already in map
        actionMap.put(action.handles(), action);
    }

    public KarediAction get(KarediActions key) {
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

    public KarediAction getAction(KarediActions key) {
        return actionMap.get(key);
    }
}
