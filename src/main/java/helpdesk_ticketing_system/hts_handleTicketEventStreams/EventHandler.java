package helpdesk_ticketing_system.hts_handleTicketEventStreams;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.net.HttpURLConnection;
import java.util.Map;

public class EventHandler implements RequestHandler<Map<String,Object>,Object> {
    private final MongoDb mongoDb;

    public EventHandler() {
        mongoDb = new MongoDb();
    }

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
        if(inputEvent==null || inputEvent.isEmpty())
            return HttpURLConnection.HTTP_BAD_REQUEST;
        context.getLogger().log("input event : " + inputEvent+"\n");
        try
        {
            // getting what event has been raised.
            Events events = Events.valueOf(String.valueOf(inputEvent.get("event")));
            Object issueId = inputEvent.get("issue_id");
            switch(events)
            {
                // new ticket has been 'INSERTED'
                // status field of ISSUE has to be set to "TICKET_RAISED".
                case TICKET_RAISED:
                    context.getLogger().log("Event : " + Events.TICKET_RAISED + "\n");
                    if(mongoDb.updateStatusOfIssue(issueId,Status.TICKET_RAISED,context) &&
                            mongoDb.updateTicketIdFieldOfIssue(issueId,String.valueOf(inputEvent.get("ticket_id")),context))
                        return HttpURLConnection.HTTP_OK;
                    break;

                // ticket has been 'UPDATED' --> status updated
                // status field of ISSUE has to be set to the new status (but not to ON_HOLD status).
                case TICKET_STATUS_CHANGED:{
                    context.getLogger().log("Event : " + Events.TICKET_STATUS_CHANGED + "\n");
                    try{
                        Status newStatus = Status.valueOf(String.valueOf(inputEvent.get("status")).toUpperCase());
                        if(newStatus.equals(Status.ON_HOLD) || mongoDb.updateStatusOfIssue(issueId,newStatus,context))
                            return HttpURLConnection.HTTP_OK;
                        break;
                    }catch (Exception e){
                        context.getLogger().log("Exception occurred in : " + this.getClass().getName());
                        context.getLogger().log("Exception Class : " + e.getClass().getName());
                        context.getLogger().log("Exception Message : " + e.getMessage());
                    }
                    break;
                }
            }
        }
        catch (Exception e)
        {
            context.getLogger().log("Exception occurred in : " + this.getClass().getName());
            context.getLogger().log("Exception Class : " + e.getClass().getName());
            context.getLogger().log("Exception Message : " + e.getMessage());
        }
        return HttpURLConnection.HTTP_INTERNAL_ERROR;
    }
}
