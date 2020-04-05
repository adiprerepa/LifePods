package database.circles.circle_entities;

import database.base_entities.BaseDatabaseIdentifier;

/**
 * (C) Copyright Aditya Prerepa 2019. All Rights Reserved.
 * adiprerepa@gmail.com
 */

public class CircleDatabaseIdentifier<Identifier> implements BaseDatabaseIdentifier<Identifier> {

    private Identifier identifier;

    public CircleDatabaseIdentifier(Identifier id) {
        identifier = id;
    }


    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    // for db identify
    @Override
    public String getName() {
        return "circle_id";
    }

    // second identifier
    public String getAlternameName() {
        return "username";
    }
}
