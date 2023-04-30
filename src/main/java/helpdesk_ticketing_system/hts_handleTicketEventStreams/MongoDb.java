package helpdesk_ticketing_system.hts_handleTicketEventStreams;

import com.amazonaws.services.lambda.runtime.Context;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

public class MongoDb {
    private final MongoCollection<Document> mongoIssuesCollection;

    public MongoDb() {
        String connectionUri = System.getenv("mongodb_connection_uri");
        String username = System.getenv("mongodb_username");
        String password = System.getenv("mongodb_password");
        String database = System.getenv("mongodb_database");
        String issuesCollection = System.getenv("mongodb_collection_issues");

        String connectionString = String.format(connectionUri, username, password);
        MongoClient mongoClient = MongoClients.create(
                MongoClientSettings.builder()
                        .serverApi(ServerApi.builder().version(ServerApiVersion.V1).build())
                        .applyConnectionString(new ConnectionString(connectionString))
                        .build()
        );

        mongoIssuesCollection = mongoClient.getDatabase(database).getCollection(issuesCollection);
    }
    boolean updateStatusOfIssue(Object issueId, Status newStatus, Context context)
    {
        try{
            context.getLogger().log("issue id : " + issueId + "---- new status : " + newStatus + "\n");
            UpdateResult updateResult = mongoIssuesCollection.updateOne(
                    Filters.eq("_id", issueId),
                    Updates.set("status",newStatus)
            );
            context.getLogger().log("update result : " + updateResult.wasAcknowledged());
            return updateResult.wasAcknowledged();
        }catch (Exception e){
            context.getLogger().log("Exception occurred in : " + this.getClass().getName());
            context.getLogger().log("Exception Class : " + e.getClass().getName());
            context.getLogger().log("Exception Message : " + e.getMessage());
        }
        return false;
    }

    boolean updateTicketIdFieldOfIssue(Object issueId, String ticketId, Context context)
    {
        try{
            UpdateResult updateResult = mongoIssuesCollection.updateOne(
                    Filters.eq("_id", issueId),
                    Updates.set("ticket_id",ticketId)
            );
            context.getLogger().log("update result : " + updateResult.wasAcknowledged());
            return updateResult.wasAcknowledged();
        }catch (Exception e){
            context.getLogger().log("Exception occurred in : " + this.getClass().getName());
            context.getLogger().log("Exception Class : " + e.getClass().getName());
            context.getLogger().log("Exception Message : " + e.getMessage());
        }
        return false;
    }
}
