package com.mech.app.models;

import com.mech.app.configfiles.ErrorLoggerTemplate;
import com.mech.app.dataproviders.cars.CarDataProvider;
import com.mech.app.dataproviders.customers.CustomersDataProvider;
import com.mech.app.dataproviders.dao.DAO;
import com.mech.app.dataproviders.users.UsersDataProvider;

import java.time.LocalDateTime;

public class CustomerModel extends DAO {

    public int registerCustomer(CustomersDataProvider obj) {
        String query = """
                INSERT INTO
                customers(shop_id, customer_name, gender, mobile_number, other_number, email, address, notes, is_active, created_by)
                VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;
        try {
            prepare = getCon().prepareStatement(query);
            prepare.setInt(1, obj.getShopId());
            prepare.setString(2, obj.getName());
            prepare.setString(3, obj.getGender());
            prepare.setString(4, obj.getMobileNumber());
            prepare.setString(5, obj.getOtherNumber());
            prepare.setString(6, obj.getEmail());
            prepare .setString(7, obj.getDigitalAddress());
            prepare.setString(8, obj.getComments());
            prepare.setBoolean(9, obj.isStatus());
            prepare.setInt(10, obj.getUserId());
            return prepare.executeUpdate();
        }catch (Exception ex) {
            ex.printStackTrace();
            new ErrorLoggerTemplate(LocalDateTime.now().toString(), ex.getLocalizedMessage(), "registerCustomer()").logErrorToFile();
            logError(ex, "registerCustomer");
        }
        return 0;
    }

    public int registerCustomerAsUser(UsersDataProvider obj) {
        String query = """
                INSERT INTO users(shop_id, reference_id, username, user_role, user_password)
                VALUES(?, (SELECT MAX(record_id) from customers), ?, ?, ?)
                """;
        try {
            prepare = getCon().prepareStatement(query);
            prepare.setInt(1, obj.getShop_id());
            prepare.setString(2, obj.getUsername());
            prepare.setString(3, obj.getRole());
            prepare.setString(4, obj.getPassword());
            return prepare.executeUpdate();

        }catch (Exception ex) {
            ex.printStackTrace();
            new ErrorLoggerTemplate(LocalDateTime.now().toString(), ex.getLocalizedMessage(), "registerCustomerAsUser()").logErrorToFile();
            logError(ex, "registerCustomerAsUser");
        }
        return 0;
    }

    public int registerCustomerCar(CarDataProvider obj) {
        String query = """
                INSERT INTO customer_vehicles(customer_id, brand, model, car_year, plate_number)
                VALUES((SELECT MAX(record_id) FROM customers), ?, ?, ?, ?);
                """;
        try {
            prepare = getCon().prepareStatement(query);
            prepare.setString(1, obj.getBrand());
            prepare.setString(2, obj.getModel());
            prepare.setString(3, String.valueOf(obj.getCarYear()));
            prepare.setString(4, obj.getPlateNumber());
            return prepare.executeUpdate();
        }catch (Exception ex) {
            ex.printStackTrace();
            new ErrorLoggerTemplate(LocalDateTime.now().toString(), ex.getLocalizedMessage(), "registerCustomerCar()").logErrorToFile();
            logError(ex, "registerCustomerCar");
        }
        return 0;
    }

}//END OF CLASS...
