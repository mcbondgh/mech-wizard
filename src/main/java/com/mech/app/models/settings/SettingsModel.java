package com.mech.app.models.settings;

import com.mech.app.dataproviders.dao.DAO;

public class SettingsModel extends DAO {

    public int createNewServiceType(int shopId, String serviceTypeName, String serviceTypeDescription, double cost, int userId) {
        String sql = "INSERT INTO service_types(shop_id, service_type, service_desc, service_cost, user_id) VALUES (?, ?, ?, ?, ?)";
        try {
            prepare = getCon().prepareStatement(sql);
            prepare.setInt(1, shopId);
            prepare.setString(2, serviceTypeName);
            prepare.setString(3, serviceTypeDescription);
            prepare.setDouble(4, cost);
            prepare.setInt(5, userId);
            return prepare.executeUpdate(); // Return the number of rows affected
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Return -1 to indicate failure
        }
    }

}//end of class...
