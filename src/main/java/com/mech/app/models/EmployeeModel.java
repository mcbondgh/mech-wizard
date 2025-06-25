package com.mech.app.models;

import com.mech.app.configfiles.ErrorLoggerTemplate;
import com.mech.app.dataproviders.dao.DAO;
import com.mech.app.dataproviders.employees.EmployeesDataProvider;
import com.mech.app.dataproviders.users.UsersDataProvider;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class EmployeeModel extends DAO {
    // This class can be extended to include methods specific to employee data handling,
    // For example, methods to retrieve employee details, update records, etc.

    public EmployeeModel() {
        super();
        // Initialize any necessary resources or configurations here
    }

    // Example method to retrieve employee details by ID
    public String getEmployeeDetailsById(int employeeId) {
        // Logic to fetch employee details from the database using the provided ID
        return "Employee Details for ID: " + employeeId; // Placeholder return statement
    }

    public int registerEmployee(EmployeesDataProvider provider) {
        String query = """
                INSERT INTO employees(shop_id, full_name, gender, mobile_number, email,\s
                address, skill, card_type, card_number, notes, created_by, date_joined)
                VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;
        try {
            prepare = getCon().prepareStatement(query);
            prepare.setInt(1, provider.getShopId());
            prepare.setString(2, provider.getFullName());
            prepare.setString(3, provider.getGender());
            prepare.setString(4, provider.getMobileNumber());
            prepare.setString(5, provider.getEmail());
            prepare.setString(6, provider.getDigitalAddress());
            prepare.setString(7, provider.getEmployeeSkill());
            prepare.setString(8, provider.getCardType());
            prepare.setString(9, provider.getCardNumber());
            prepare.setString(10, provider.getDescription());
            prepare.setInt(11, provider.getUserId());
            prepare.setDate(12, provider.getDateJoined());
            return prepare.executeUpdate();

        }catch (SQLException e) {
            e.printStackTrace();
            logError(e, "registerEmployee()");
            new ErrorLoggerTemplate(LocalDateTime.now().toString(), e.getLocalizedMessage(), "registerEmployee()").logErrorToFile();

        }
        return 0;
    }

    public int registerUser(UsersDataProvider obj) {
        String query = """
                INSERT INTO users(shop_id, reference_id, username, user_role, user_password)
                VALUES(?, (SELECT MAX(record_id) from employees), ?, ?, ?);
                """;
        try {
            prepare = getCon().prepareStatement(query);
            prepare.setInt(1, obj.getShop_id());
            prepare.setString(2, obj.getUsername());
            prepare.setString(3, obj.getRole());
            prepare.setString(4, obj.getPassword());
            return prepare.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            logError(e, "registerUser()");
            new ErrorLoggerTemplate(LocalDateTime.now().toString(), e.getLocalizedMessage(), "registerUser()").logErrorToFile();
        }
        return 0;
    }

}//end of class
