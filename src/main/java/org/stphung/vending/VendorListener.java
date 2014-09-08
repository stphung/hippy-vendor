package org.stphung.vending;

public interface VendorListener {
    void offerCreated(String id, Offer offer);
    // TODO: nice to have, events for when we are looking up prices
}
