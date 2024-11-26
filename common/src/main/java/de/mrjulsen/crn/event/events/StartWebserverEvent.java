package de.mrjulsen.crn.event.events;

import java.util.Collection;
import java.util.List;

import de.mrjulsen.crn.event.CRNEventsManager.AbstractCRNEvent;
import de.mrjulsen.crn.web.DLRestServer.DLRestManager;

public class StartWebserverEvent extends AbstractCRNEvent<StartWebserverEvent.IStartWebserverEvent> {
    public List<DLRestManager> run() {
        List<DLRestManager> res = listeners.values().stream().flatMap(x -> x.run().stream()).toList();
        return res;
    }

    @FunctionalInterface
    public static interface IStartWebserverEvent {
        Collection<DLRestManager> run();
    }
}
