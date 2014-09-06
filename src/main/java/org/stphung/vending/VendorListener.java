package org.stphung.vending;

public interface VendorListener {
    void offerCreated(Offer offer);
    //void itemSold(); // TODO: what's the signature for this?
    // TODO: listener for when we are looking up prices
}
