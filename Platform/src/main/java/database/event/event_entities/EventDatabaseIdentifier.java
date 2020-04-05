package database.event.event_entities;

import database.base_entities.BaseDatabaseIdentifier;

/**
 * (C) Copyright Aditya Prerepa 2019. All Rights Reserved.
 * adiprerepa@gmail.com
 */

public class EventDatabaseIdentifier<T> implements BaseDatabaseIdentifier<T> {

    private T identifier;

    public EventDatabaseIdentifier(T identifier) {
        this.identifier = identifier;
    }

    @Override
    public T getIdentifier() {
        return identifier;
    }

    @Override
    public String getName() {
        return "user_id";
    }
}
