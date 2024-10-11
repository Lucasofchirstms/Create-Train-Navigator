package de.mrjulsen.crn.event.events;

import de.mrjulsen.crn.event.CRNEventsManager.AbstractCRNEvent;

public final class GlobalTrainDisplayDataRefreshEventPost extends AbstractCRNEvent<Runnable> {
    public void run() {
        listeners.values().forEach(Runnable::run);
        tickPost();
    }
}