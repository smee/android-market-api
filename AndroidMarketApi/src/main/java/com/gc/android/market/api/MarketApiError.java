package com.gc.android.market.api;

/**
 * @author Amir Raminfar
 */
public class MarketApiError extends Exception {
    private int code;

    public MarketApiError(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
