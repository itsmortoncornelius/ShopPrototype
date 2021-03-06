package de.handler.mobile.android.fairmondo.network;

import android.support.annotation.NonNull;

/**
 * Network Exception which is thrown whenever something goes wrong in the Network Layer.
 */
public class NetworkException extends RuntimeException {
    /**
     * Public Constructor forcing the assignment of a Throwable.
     * @param detailMessage the error message
     * @param throwable an appropriate throwable
     */
    public NetworkException(@NonNull final String detailMessage, @NonNull final Throwable throwable) {
        super(detailMessage, throwable);
    }
}
