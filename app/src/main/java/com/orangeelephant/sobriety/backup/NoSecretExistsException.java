package com.orangeelephant.sobriety.backup;

public class NoSecretExistsException extends Exception {
    public NoSecretExistsException(String m) {
        super(m);
    }
}
