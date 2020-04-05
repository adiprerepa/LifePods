package database.base_entities;

/**
 * (C) Copyright Aditya Prerepa 2019. All Rights Reserved.
 * adiprerepa@gmail.com
 */

public interface BaseDatabaseIdentifier<T> {

    T getIdentifier();

    String getName();
}
