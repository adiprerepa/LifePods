package database.authentication.entities;

import database.base_entities.BaseDatabaseIdentifier;

/**
 * (C) Copyright Aditya Prerepa 2019. All Rights Reserved.
 * adiprerepa@gmail.com
 */

public class AuthenticationDatabaseIdentifier<Identifier> implements BaseDatabaseIdentifier {

    private Identifier id;

    public AuthenticationDatabaseIdentifier(Identifier id) {
        this.id = id;
    }

    @Override
    public Identifier getIdentifier() {
        return id;
    }

    @Override
    public String getName() {
        return "username";
    }
}
