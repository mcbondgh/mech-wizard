package com.mech.app.models;

import com.mech.app.configfiles.ErrorLoggerTemplate;
import com.mech.app.dataproviders.dao.DAO;
import com.mech.app.dataproviders.servicesrequest.ServicesDataProvider;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

public class ServiceRequestModel extends DAO {

    public void createJobCard(int serviceId) {
        String query = """
                INSERT INTO job_cards(service_id, job_status, job_progress)
                VALUES( ? , 'new', 0);
                """;
        try {
            prepare = getCon().prepareStatement(query);
            prepare.setInt(1, serviceId);
            prepare.executeUpdate();
        }catch (Exception ex) {
            ex.printStackTrace();
            new ErrorLoggerTemplate(LocalDateTime.now().toString(), ex.getLocalizedMessage(), "createJobCard()").logErrorToFile();
            logError(ex, "createJobCard");
        }
    }

    public int updateJobCardStatus(int jobId, int serviceId, String progress, String status, String note) {
        String query1 = "UPDATE job_cards SET job_status = '"+status+"', job_progress = '"+progress+"', last_updated = DEFAULT WHERE job_id = '"+jobId+"';";
        String query2 = "INSERT INTO job_notes(job_id, notes) VALUES('"+jobId+"','"+note+"');";
        String query3 = "UPDATE service_requests SET service_status = 'completed', last_updated = CURRENT_TIMESTAMP WHERE id = '"+serviceId+"';";

        try {
            int response = getCon().prepareStatement(query1).executeUpdate();
            response+= getCon().prepareStatement(query2).executeUpdate();
            if (Objects.equals("completed", status)){
                response += getCon().prepareStatement(query3).executeUpdate();
            }
            return response;
        }catch (Exception ex) {
            ex.printStackTrace();
            new ErrorLoggerTemplate(LocalDateTime.now().toString(), ex.getLocalizedMessage(), "updateJobCardStatus()").logErrorToFile();
            logError(ex, "updateJobCardStatus");
        }

        return 0;
    }

    public int assignMechanicModel(int mechanicId, int serviceId) {
        String query = """
                UPDATE service_requests SET assigned_mechanic = ?, service_status = 'assigned',
                last_updated = CURRENT_TIMESTAMP WHERE id = ?;
                """;
        try {
            prepare = getCon().prepareStatement(query);
            prepare.setInt(1, mechanicId);
            prepare.setInt(2, serviceId);
            return prepare.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            new ErrorLoggerTemplate(LocalDateTime.now().toString(), ex.getLocalizedMessage(), "assignMechanicModel()").logErrorToFile();
            logError(ex, "assignMechanicModel");
        }
        return -1;
    }

    public int updateServiceRequest(Map<String, Object> dataObj) {
        String query = """
                 UPDATE service_requests
                 SET service_type_id = ?, service_desc = ?,
                     preferred_date = ?, urgency_level = ?,
                     pickup_or_dropoff = ?, last_updated = CURRENT_TIMESTAMP
                 WHERE id = ?;
                """;
        var serviceId = Integer.parseInt(dataObj.getOrDefault("serviceId", 0).toString());
        var typeId = dataObj.get("typeId").toString();
        var preferredDate = dataObj.getOrDefault("dateValue", LocalDate.now()).toString();
        var desc = dataObj.get("desc").toString();
        var level = dataObj.get("level").toString();
        var pickupValue = dataObj.get("pickupValue").toString();

        try {
            prepare = getCon().prepareStatement(query);
            prepare.setInt(1, Integer.parseInt(typeId));
            prepare.setString(2, desc);
            prepare.setDate(3, Date.valueOf(preferredDate));
            prepare.setString(4, level);
            prepare.setBoolean(5, Boolean.parseBoolean(pickupValue));
            prepare.setInt(6, serviceId);
            return prepare.executeUpdate();

        } catch (Exception ex) {
            ex.printStackTrace();
            new ErrorLoggerTemplate(LocalDateTime.now().toString(), ex.getLocalizedMessage(), "updateServiceRequest()").logErrorToFile();
            logError(ex, "updateServiceRequest");
        }
        return 0;
    }

    public int cancelServiceRequest(int serviceId, String note) {
        String query = """
                UPDATE service_requests SET service_status = 'cancelled',
                last_updated = CURRENT_TIMESTAMP, termination_note = ? WHERE id = ?;
                """;
        try {
            prepare  = getCon().prepareStatement(query);
            prepare.setString(1, note);
            prepare.setInt(2, serviceId);
            return prepare.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            new ErrorLoggerTemplate(LocalDateTime.now().toString(), ex.getLocalizedMessage(), "cancelServiceRequest()").logErrorToFile();
            logError(ex, "cancelServiceRequest");
        }
        return 0;
    }

    public int deleteServiceRequest(int serviceId) {
        String query = "UPDATE service_requests SET is_deleted = TRUE, last_updated = CURRENT_TIMESTAMP WHERE id = '"+serviceId+"'";
        try {
            return getCon().prepareStatement(query).executeUpdate();
        }catch (Exception ex) {
            ex.printStackTrace();
            new ErrorLoggerTemplate(LocalDateTime.now().toString(), ex.getLocalizedMessage(), "deleteServiceRequest()").logErrorToFile();
            logError(ex, "deleteServiceRequest");
        }
        return -1;
    }

    public int addPurchasedPart(int jobId, String name, int qty, double amount) {
        String query = """
                INSERT INTO purchased_job_item(job_id, item_name, qty, price)
                VALUES(?, ?, ?, ?);
                """;
        try {
            prepare = getCon().prepareStatement(query);
            prepare.setInt(1, jobId);
            prepare.setString(2, name);
            prepare.setInt(3, qty);
            prepare.setDouble(4, amount);
            return prepare.executeUpdate();
        }catch (Exception ex) {
            ex.printStackTrace();
            new ErrorLoggerTemplate(LocalDateTime.now().toString(), ex.getLocalizedMessage(), "addPurchasedPart()").logErrorToFile();
            logError(ex, "addPurchasedPart");
        }

        return 0;
    }
}
