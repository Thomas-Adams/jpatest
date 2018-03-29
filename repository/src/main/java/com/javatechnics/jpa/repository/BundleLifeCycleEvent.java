package com.javatechnics.jpa.repository;

import org.osgi.framework.BundleEvent;

import java.util.HashMap;
import java.util.Map;

public enum BundleLifeCycleEvent
{
    INSTALLED(BundleEvent.INSTALLED),
    LAZY_ACTIVATION(BundleEvent.LAZY_ACTIVATION),
    RESOLVED(BundleEvent.RESOLVED),
    STARTED(BundleEvent.STARTED),
    STARTING(BundleEvent.STARTING),
    STOPPED(BundleEvent.STOPPED),
    STOPPING(BundleEvent.STOPPING),
    UNINSTALLED(BundleEvent.UNINSTALLED),
    UNRESOLVED(BundleEvent.UNRESOLVED),
    UPDATED(BundleEvent.UPDATED);

    private final int value;

    private static Map<Integer, BundleLifeCycleEvent> eventMap = new HashMap<>(BundleLifeCycleEvent.values().length);

    static
    {
        for (final BundleLifeCycleEvent event : BundleLifeCycleEvent.values())
        {
            eventMap.put(event.value, event);
        }
    }

    BundleLifeCycleEvent(final int value)
    {
        this.value = value;
    }

    public static BundleLifeCycleEvent getEnum(int value)
    {
        return eventMap.get(value);
    }
}
