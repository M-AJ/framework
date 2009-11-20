/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * class for event handlers used by ComponentEventHandler
 * 
 * @author davengo GmbH (Germany/Berlin, www.davengo.com)
 * 
 */
public class ComponentEventHandler {

    public static final String HANDLER_LISTEN_ATTRIBUTE = "listenEvents";
    public static final String HANDLER_TRIGGER_VARIABLE = "fireEvent";

    private List<String> eventRegistrations;
    private ComponentDetail detail;
    private ApplicationConnection client;

    public ComponentEventHandler(ComponentDetail detail,
            ApplicationConnection client) {
        this.detail = detail;
        this.client = client;
        this.eventRegistrations = null;
    }

    /**
     * Fires a event which is transmitted to the server and passed on the the
     * components handleEvent method provided listeners have been registered on
     * the server side.
     * 
     * @param eventIdentifier
     *            the unique identifier for the event
     * @param parameters
     *            the parameters for the event (can be null)
     */
    public void fireEvent(String eventIdentifier, String... parameters) {
        fireEvent(eventIdentifier, false, parameters);
    }

    /**
     * Fires a component event which is transmitted to the server and passed on
     * the the components handleEvent method. The event is sent to the server
     * even though there are no explicit listeners registered on the server
     * side.
     * 
     * @param eventIdentifier
     *            the unique identifier for the event
     * @param parameters
     *            the parameters for the event (can be null)
     */
    public void fireComponentEvent(String eventIdentifier, String... parameters) {
        fireEvent(eventIdentifier, true, parameters);
    }

    private void fireEvent(String eventIdentifier, boolean forceTransmission,
            String... parameters) {

        String[] event;

        // filter events which are not listened on the server-side right here
        boolean transmit = forceTransmission
                || ((!(eventRegistrations == null)) && eventRegistrations
                        .contains(eventIdentifier));

        if (transmit) {
            if (parameters != null) {
                event = new String[parameters.length + 1];
                event[0] = eventIdentifier;
                for (int i = 0; i < parameters.length; i++) {
                    event[i + 1] = parameters[i];
                }
            } else {
                event = new String[] { eventIdentifier };
            }

            // transmit the event to the server-side
            client.updateVariable(detail.getPid(), HANDLER_TRIGGER_VARIABLE,
                    event, true);
        }
    }

    void registerEventsFromUIDL(UIDL componentUIDL) {

        // read out the request event handlers
        if (componentUIDL.hasAttribute(HANDLER_LISTEN_ATTRIBUTE)) {
            String[] requestedEvents = componentUIDL
                    .getStringArrayAttribute(HANDLER_LISTEN_ATTRIBUTE);

            // create the eventRegistrations list if necessary
            if ((requestedEvents.length > 0) && (eventRegistrations == null)) {
                eventRegistrations = new ArrayList<String>();
            }

            // parse the requested event handlers
            for (String reqEvent : requestedEvents) {

                if (!eventRegistrations.contains(reqEvent)) {
                    eventRegistrations.add(reqEvent);
                }

            }

        }

    }

}
