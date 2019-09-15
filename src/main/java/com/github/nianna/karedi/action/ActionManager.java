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
        karediActions.forEach(action -> actionMap.put(action.handles(), action));
    }

    public KarediAction get(KarediActions key) {
        return actionMap.get(key);
    }
}
