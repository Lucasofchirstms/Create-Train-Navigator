package de.mrjulsen.crn.event.events;

import de.mrjulsen.crn.event.CRNEventsManager.AbstractCRNEvent;

public class DefaultTrainDataRefreshEvent extends AbstractCRNEvent<Runnable> {
    public void run() {
        listeners.values().forEach(Runnable::run);
        tickPost();
    }
}
