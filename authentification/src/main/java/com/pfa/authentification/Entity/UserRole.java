package com.pfa.authentification.Entity;

public enum UserRole {
    ADMIN,      // Administrateur : gère le réseau, les paramètres, les comptes
    OPERATOR,   // Opérateur / Dispatcher : console d'alertes, carte, incidents
    DRIVER,     // Conducteur : déclare des incidents
    VIEWER      // Passager / lecture seule : horaires, ETA, retards
}
