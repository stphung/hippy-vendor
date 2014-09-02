package org.stphung.vending;

public interface VendorListener {
    void offerCreated(Offer offer);
    // TODO: listener for when we are looking up prices
}
