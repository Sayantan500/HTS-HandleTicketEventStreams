package helpdesk_ticketing_system.hts_handleTicketEventStreams;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Map;

public class EventHandler implements RequestHandler<Map<String,Object>,Object> {

    /**
     * if event => "TICKET_RAISED"
     * then,
     *      - update status of issue with issue_id received from event to "TICKET_RAISED"
     *      - send a notification email to the user with user_id (i.e. submitted_by field value) received from event
     * <p>
     * if event => "STATUS_CHANGED" & if the new status is not "ON-HOLD"
     * then,
     *      - update status of issue with issue_id received from event to "{new-status-value}"
     * */
    @Override
    public Object handleRequest(Map<String, Object> inputEvent, Context context) {
        return null;
    }
}
