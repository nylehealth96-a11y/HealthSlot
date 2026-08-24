package com.hospital.smartqueue.eta.application.port;

import java.util.UUID;

/** Notification boundary for the queue module; ETA never owns queue state. */
public interface QueueEtaConsumerPort {
    void predictionPublished(UUID predictionVersionId);
}
