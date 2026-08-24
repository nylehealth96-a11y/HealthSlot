package com.hospital.smartqueue.eta.application;

import com.hospital.smartqueue.common.domain.ConflictException;

/** Non-disclosing conflict for unavailable or inconsistent authoritative ETA inputs. */
public class EtaUnavailableException extends ConflictException {
    public EtaUnavailableException() { super("ETA prediction is currently unavailable"); }
}
