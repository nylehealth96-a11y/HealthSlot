package com.hospital.smartqueue.notification.application.port;

/** Provider-neutral delivery boundary; vendor-specific adapters are deliberately excluded. */
public interface NotificationDeliveryProvider {
    DeliveryResult deliver(DeliveryRequest request);
    record DeliveryRequest(String opaqueDestination, String renderedContent) { }
    record DeliveryResult(boolean delivered, String safeReasonCode) { }
}
