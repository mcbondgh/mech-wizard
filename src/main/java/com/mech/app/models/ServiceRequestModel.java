package com.mech.app.models;

import com.mech.app.configfiles.ErrorLoggerTemplate;
import com.mech.app.dataproviders.dao.DAO;
import com.mech.app.dataproviders.servicesrequest.ServicesDataProvider;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public class ServiceRequestModel extends DAO {

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
        var typeId = dataObj.get("serviceId").toString();
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
}
