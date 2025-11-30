package com.pfa.service_admin.Entity;

public enum SignalState {
    FRESH,      // position reçue récemment
    STALE,      // position un peu vieille
    NO_SIGNAL   // plus rien depuis longtemps
}