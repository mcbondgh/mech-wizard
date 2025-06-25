package com.mech.app.dataproviders.dao;

import com.mech.app.configfiles.ErrorLoggerTemplate;
import com.mech.app.configfiles.database.AppConnection;
import com.mech.app.dataproviders.cars.CarDataProvider;
import com.mech.app.dataproviders.customers.CustomersDataProvider;
import com.mech.app.dataproviders.employees.EmployeesDataProvider;
import com.mech.app.dataproviders.logs.ErrorLogsDataProvider;
import com.mech.app.dataproviders.logs.NotificationRecords;
import com.mech.app.dataproviders.transactions.CustomerAccountRecord;
import com.mech.app.dataproviders.transactions.TransactionLogs;
import com.mech.app.dataproviders.users.UsersDataProvider;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DAO extends AppConnection {
    // This class serves as a base class for data access objects (DAOs).
    // It extends AppConnection to inherit database connection functionality.
    // Additional DAO-specific methods can be added here in the future.
    protected ErrorLogsDataProvider errorLogger;

    protected void logError(ErrorLogsDataProvider errorLogger) {
        try {
            String query = "INSERT INTO error_logs(error_message, stack_trace, app_instance)\n" +
                    "VALUES(?, ?, ?);";
            prepare = getCon().prepareStatement(query);
            prepare.setString(1, errorLogger.getMessage());
            prepare.setString(2, errorLogger.getStackTrace());
            prepare.setString(3, errorLogger.getAppInstance());
            prepare.execute();
        } catch (Exception ignore) {
            new ErrorLoggerTemplate(LocalDateTime.now().toString(), ignore.getLocalizedMessage(), "logError").logErrorToFile();
        }
    }

    public void logNotification(NotificationRecords records) {
        try {
            String query = "INSERT INTO notification_logs(title, content, created_by, shop_id) VALUES(?, ?, ?, ?);";
            prepare = getCon().prepareStatement(query);
            prepare.setString(1, records.title());
            prepare.setString(2, records.content());
            prepare.setInt(3, records.userId());
            prepare.setInt(4, records.getShopId());
            prepare.execute();
        } catch (Exception exception) {
            new ErrorLoggerTemplate(LocalDateTime.now().toString(), exception.getLocalizedMessage(), "logNotification()").logErrorToFile();
            logError(exception, "logNotification()");
        }
    }

    // ERROR LOGGER METHOD IMPLEMENTATION
    public void logError(Exception exception, String methodName) {
        String msg = exception.getLocalizedMessage();
        String stack = Arrays.toString(exception.getStackTrace());
        errorLogger = new ErrorLogsDataProvider(msg, stack, methodName);
        logError(errorLogger);
    }

    /*
     * THIS PART OF THE CLASS SHALL BE USED TO QUERY THE DATABASE FOR SELECT STATEMENTS...
     */

    public List<EmployeesDataProvider.EmployeesRecord> fetchAllMechanics() {
        List<EmployeesDataProvider.EmployeesRecord> data = new ArrayList<>();
        try {
            String query = """
                    SELECT emp.record_id, user_id, emp.date_created, full_name, gender, email,
                    mobile_number, address, skill, card_type, card_number, notes,
                    emp.is_active, username, user_role, user_password, user_status\s
                    FROM employees AS emp
                    INNER JOIN users AS u\s
                    ON emp.record_id = u.reference_id
                    WHERE emp.is_deleted = FALSE;
                    """;

            resultSet = getCon().prepareStatement(query).executeQuery();
            while (resultSet.next()) {
                int recordId = resultSet.getInt("record_id");
                int userId = resultSet.getInt("user_id");
                Timestamp date = resultSet.getTimestamp("date_created");
                String fullname = resultSet.getString("full_name");
                String gender = resultSet.getString("gender");
                String email = resultSet.getString("email");
                String notes = resultSet.getString("notes");
                String number = resultSet.getString("mobile_number");
                String address = resultSet.getString("address");
                var skill = resultSet.getString("skill");
                var cardType = resultSet.getString("card_type");
                var cardNumber = resultSet.getString("card_number");
                boolean isActive = resultSet.getBoolean("is_active");
                var username = resultSet.getString("username");
                var role = resultSet.getString("user_role");
                var password = resultSet.getString("user_password");
                var status = resultSet.getBoolean("user_status");
                var usersData = new UsersDataProvider.usersRecord(userId, username, password, role);
                data.add(
                        new EmployeesDataProvider.EmployeesRecord(recordId, fullname, number, email, address, gender,
                                skill, cardType, cardNumber, date, notes, isActive, usersData)
                );
            }
            resultSet.close();
            getCon().close();
        } catch (Exception ex) {
            new ErrorLoggerTemplate(LocalDateTime.now().toString(), ex.getLocalizedMessage(), "fetchAllMechanics").logErrorToFile();
            logError(ex, "fetchAllMechanics");
            ex.printStackTrace();
        }
        return data;
    }

    public List<CustomersDataProvider> fetchAllCustomers() {
        List<CustomersDataProvider> data = new ArrayList<>();
        String query = """
                SELECT * FROM customers WHERE is_deleted = false;
                """;
        try {
            resultSet = getCon().prepareStatement(query).executeQuery();
            while (resultSet.next()) {
                data.add(
                        new CustomersDataProvider(
                                resultSet.getInt("record_id"),
                                resultSet.getInt("created_by"),
                                resultSet.getInt("shop_id"),
                                resultSet.getString("customer_name"),
                                resultSet.getString("mobile_number"),
                                resultSet.getString("other_number"),
                                resultSet.getString("email"),
                                resultSet.getString("address"),
                                resultSet.getString("gender"),
                                resultSet.getString("notes"),
                                resultSet.getBoolean("is_active"),
                                resultSet.getTimestamp("date_created")
                        )
                );
            }
        } catch (Exception ex) {
            new ErrorLoggerTemplate(LocalDateTime.now().toString(), ex.getLocalizedMessage(), "fetcAllCustomers").logErrorToFile();
            logError(ex, "fetchAllCustomers");
            ex.printStackTrace();
        }

        return data;
    }

    public List<CarDataProvider> fetchCustomerCarInformation() {
        List<CarDataProvider> data = new ArrayList<>();
        String query = "SELECT * FROM customer_vehicles;";

        try {
            resultSet = getCon().prepareStatement(query).executeQuery();
            while (resultSet.next()) {
                data.add(new CarDataProvider(
                        resultSet.getInt("record_id"),
                        resultSet.getInt("customer_id"),
                        resultSet.getString("brand"),
                        resultSet.getString("model"),
                        resultSet.getString("plate_number"),
                        resultSet.getString("car_year"))
                );
            }
        } catch (Exception ex) {
            new ErrorLoggerTemplate(LocalDateTime.now().toString(), ex.getLocalizedMessage(), "fetchCustomerCarInformation").logErrorToFile();
            logError(ex, "fetchCustomerCarInformation");
            ex.printStackTrace();
        }
        return data;
    }

    public ArrayList<CarDataProvider> fetchCustomerCarsById(int customerId) {
        ArrayList<CarDataProvider> data = new ArrayList<>();
        String query = "SELECT * FROM customer_vehicles WHERE customer_id = ?;";
        try {
            prepare = getCon().prepareStatement(query);
            prepare.setInt(1, customerId);
            resultSet = prepare.executeQuery();
            while (resultSet.next()) {
                data.add(new CarDataProvider(
                        resultSet.getInt("record_id"),
                        resultSet.getInt("customer_id"),
                        resultSet.getString("brand"),
                        resultSet.getString("model"),
                        resultSet.getString("plate_number"),
                        resultSet.getString("car_year"))
                );
            }
        } catch (Exception ex) {
            new ErrorLoggerTemplate(LocalDateTime.now().toString(), ex.getLocalizedMessage(), "fetchCustomerCarsById").logErrorToFile();
            logError(ex, "fetchCustomerCarsById");
            ex.printStackTrace();
        }
        return data;
    }

    public List<CustomerAccountRecord.nameAndAccountData> getCustomerNameAndAccountBalanceOnly(int customerID) {
        List<CustomerAccountRecord.nameAndAccountData> data = new ArrayList<>();
        String query = """
                SELECT customer_name, mobile_number,
                ca.record_id, account_balance
                FROM customer_account AS ca
                INNER JOIN customers AS c
                ON c.record_id = ca.customer_id
                WHERE ca.customer_id = ? AND c.is_deleted = FALSE;
                """;

        try {
            prepare = getCon().prepareStatement(query);
            prepare.setInt(1, customerID);
            resultSet = prepare.executeQuery();
            while (resultSet.next()) {
                data.add(new CustomerAccountRecord.nameAndAccountData(
                        resultSet.getString("customer_name"),
                        resultSet.getString("mobile_number"),
                        resultSet.getInt("record_id"),
                        resultSet.getDouble("account_balance")
                ));
            }
        } catch (Exception exception) {
            new ErrorLoggerTemplate(LocalDateTime.now().toString(), exception.getLocalizedMessage(), "fetchCustomerAccountRecord").logErrorToFile();
            logError(exception, "fetchCustomerAccountRecord");
            exception.printStackTrace();
        }
        return data;
    }

    public List<TransactionLogs> fetchCustomerTransactionLogsById( int customerId) {
        List<TransactionLogs> data = new ArrayList<>();
        String query = """
                SELECT * FROM transaction_logs WHERE customer_id = ?
                AND transaction_type = 'deposit'
                ORDER BY record_id DESC LIMIT 5;
                """;
        try {
            prepare = getCon().prepareStatement(query);
            prepare.setInt(1, customerId);
            resultSet = prepare.executeQuery();
            //record_id, customer_id, transaction_date, transaction_type, transaction_id, payment_method, notes, amount, user_id
            while (resultSet.next()) {
                data.add(new TransactionLogs(
                        resultSet.getInt("customer_id"),
                        resultSet.getString("transaction_id"),
                        resultSet.getString("transaction_type"),
                        resultSet.getString("payment_method"),
                        resultSet.getString("notes"),
                        resultSet.getInt("user_id"),
                        resultSet.getInt("record_id"),
                        resultSet.getTimestamp("transaction_date"),
                        resultSet.getDouble("amount")
                ));
            }
        } catch (SQLException ex) {
            new ErrorLoggerTemplate(LocalDateTime.now().toString(), ex.getLocalizedMessage(), "fetchCustomerTransactionLogs").logErrorToFile();
            logError(ex, "fetchCustomerTransactionLogs");
            ex.printStackTrace();
        }
        return data;
    }


}//end of class...
