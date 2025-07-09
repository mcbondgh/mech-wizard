package com.mech.app.dataproviders.dao;

import com.mech.app.configfiles.ErrorLoggerTemplate;
import com.mech.app.configfiles.database.AppConnection;
import com.mech.app.dataproviders.cars.CarDataProvider;
import com.mech.app.dataproviders.customers.CustomersDataProvider;
import com.mech.app.dataproviders.employees.EmployeesDataProvider;
import com.mech.app.dataproviders.jobcards.JobCardDataProvider;
import com.mech.app.dataproviders.logs.ErrorLogsDataProvider;
import com.mech.app.dataproviders.logs.NotificationRecords;
import com.mech.app.dataproviders.servicesrequest.ServicesDataProvider;
import com.mech.app.dataproviders.servicesrequest.ServiceTypesRecord;
import com.mech.app.dataproviders.transactions.CustomerAccountRecord;
import com.mech.app.dataproviders.transactions.TransactionLogs;
import com.mech.app.dataproviders.transactions.TransactionsDataProvider;
import com.mech.app.dataproviders.users.UsersDataProvider;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.cglib.core.Local;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

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

    public List<TransactionLogs> fetchCustomerTransactionLogsById(int customerId) {
        List<TransactionLogs> data = new ArrayList<>();
        String query = """
                SELECT * FROM transaction_logs WHERE customer_id = ?
                AND (transaction_type = 'deposit' OR transaction_type = 'service payment')
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

    public List<ServiceTypesRecord> getServiceTypeByShopId(int shopId) {
        List<ServiceTypesRecord> data = new ArrayList<>();
        try {
            String query = """
                    SELECT record_id, service_type, service_desc, service_cost
                    FROM service_types
                    WHERE shop_id = ? AND is_deleted = FALSE;
                    """;
            prepare = getCon().prepareStatement(query);
            prepare.setInt(1, shopId);
            resultSet = prepare.executeQuery();

            while (resultSet.next()) {
                data.add(new ServiceTypesRecord(
                        resultSet.getInt("record_id"),
                        resultSet.getString("service_type"),
                        resultSet.getString("service_desc"),
                        resultSet.getDouble("service_cost")
                ));
            }
            prepare.close();
            resultSet.close();
        } catch (Exception ex) {
            new ErrorLoggerTemplate(LocalDateTime.now().toString(), ex.getLocalizedMessage(), "getShopServiceTypeByShopId").logErrorToFile();
            logError(ex, "getShopServiceTypeByShopId");
            ex.printStackTrace();
        }
        return data;
    }

    public ArrayList<ServicesDataProvider.BookedServicesRecord> fetchAllServiceRequestsNotDeleted(int shopId) {
        var data = new ArrayList<ServicesDataProvider.BookedServicesRecord>();
        String query = """
                SELECT\s
                	id, customer_name, brand, plate_number,
                    service_type, sr.service_cost, sr.service_desc, urgency_level, preferred_date,
                    pickup_or_dropoff, service_status, termination_note, full_name AS assigned_mechanic, logged_date
                FROM service_requests AS sr
                INNER JOIN customers AS c\s
                ON sr.customer_id = c.record_id
                INNER JOIN customer_vehicles AS cv\s
                	ON sr.vehicle_id = cv.record_id
                INNER JOIN service_types AS st
                	ON sr.service_type_id = st.record_id
                LEFT JOIN employees AS e\s
                	ON assigned_mechanic = e.record_id
                WHERE sr.is_deleted = FALSE AND c.shop_id = ? AND service_status != 'completed';
                """;
        try {
            prepare = getCon().prepareStatement(query);
            prepare.setInt(1, shopId);
            resultSet = prepare.executeQuery();
            while (resultSet.next()) {
                data.add(new ServicesDataProvider.BookedServicesRecord(
                        resultSet.getInt("id"),
                        resultSet.getString("customer_name"), resultSet.getString("brand"),
                        resultSet.getString("plate_number"), resultSet.getString("service_type"),
                        resultSet.getDouble("service_cost"), resultSet.getString("service_desc"),
                        resultSet.getString("urgency_level"), resultSet.getDate("preferred_date").toString(),
                        resultSet.getBoolean("pickup_or_dropoff"), resultSet.getString("service_status"),
                        resultSet.getString("assigned_mechanic"), resultSet.getTimestamp("logged_date"),
                        resultSet.getString("termination_note")
                ));
            }
            resultSet.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            new ErrorLoggerTemplate(LocalDateTime.now().toString(), ex.getLocalizedMessage(), "fetchAllServiceRequestsNotDeleted").logErrorToFile();
            logError(ex, "fetchAllServiceRequestsNotDeleted");
        }
        return data;
    }

    public List<JobCardDataProvider.JobCardRecords> fetchAllActiveJobCards() {
        List<JobCardDataProvider.JobCardRecords> data = new ArrayList<>();
        String query = """
                SELECT job_id, sr.id AS service_id, c.customer_name, brand, plate_number, full_name AS mechanic, service_type, sr.service_desc, logged_date,
                job_status, job_progress, sr.service_cost FROM service_requests AS sr
                INNER JOIN customers AS c\s
                	ON sr.customer_id = c.record_id
                INNER JOIN job_cards AS j
                	ON sr.id = j.service_id
                INNER JOIN customer_vehicles AS cv
                	ON sr.vehicle_id = cv.record_id
                INNER JOIN service_types AS st
                	ON sr.service_type_id = st.record_id
                INNER JOIN employees AS e
                	ON sr.assigned_mechanic = e.record_id
                WHERE service_status = 'assigned';
                """;

        try {
            resultSet = getCon().prepareStatement(query).executeQuery();
            while (resultSet.next()) {
                data.add(new JobCardDataProvider.JobCardRecords(
                        resultSet.getInt("job_id"), resultSet.getInt("service_id"),
                        resultSet.getString("customer_name"),
                        resultSet.getString("brand"),
                        resultSet.getString("mechanic"),
                        resultSet.getString("plate_number"),
                        resultSet.getString("service_type"),
                        resultSet.getString("service_desc"),
                        resultSet.getTimestamp("logged_date"),
                        resultSet.getString("job_status"),
                        resultSet.getString("job_progress")
                ));
            }
            resultSet.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }

    public List<JobCardDataProvider.JobDescptionRecord> fetchJobDescription(int jobId) {
        List<JobCardDataProvider.JobDescptionRecord> data = new ArrayList<>();
        String query = """
                SELECT jn.record_id, jc.job_id, sr.id as service_id, sr.service_desc, jn.notes, jn.entry_date, logged_date  FROM job_cards AS jc
                INNER JOIN job_notes AS jn
                	ON jc.job_id = jn.job_id
                INNER JOIN service_requests AS sr
                    ON jc.service_id = sr.id
                WHERE jc.job_id = ?;
                """;
        try {
            prepare = getCon().prepareStatement(query);
            prepare.setInt(1, jobId);
            resultSet = prepare.executeQuery();
            while (resultSet.next()) {
                data.add(new JobCardDataProvider.JobDescptionRecord(
                        resultSet.getInt("record_id"),
                        resultSet.getInt("job_id"),
                        resultSet.getInt("service_id"),
                        resultSet.getString("service_desc"),
                        resultSet.getString("notes"),
                        resultSet.getTimestamp("entry_date"),
                        resultSet.getTimestamp("logged_date")
                ));
            }
            prepare.close();
            resultSet.close();
        } catch (Exception ex) {

        }
        return data;
    }

    public List<JobCardDataProvider.JobCardPurchasesItems> fetchJobPurchasedItemsData(int jobId) {
        String query = "SELECT * FROM auto_mechanics.purchased_job_item WHERE job_id = '" + jobId + "';";
        List<JobCardDataProvider.JobCardPurchasesItems> data = new ArrayList<>();
        try {
            resultSet = getCon().prepareStatement(query).executeQuery();

            while (resultSet.next()) {
                data.add(new JobCardDataProvider.JobCardPurchasesItems(
                        resultSet.getInt("record_id"),
                        resultSet.getInt("job_id"),
                        resultSet.getString("item_name"),
                        resultSet.getInt("qty"),
                        resultSet.getDouble("price"),
                        resultSet.getTimestamp("entry_date")
                ));
            }
            resultSet.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }

    public List<CustomersDataProvider.FeedbackRecords> fetchAllFeedbacksByJobId(int jobId) {
        List<CustomersDataProvider.FeedbackRecords> data = new ArrayList<>();

        return data;
    }

    public List<ServicesDataProvider.CompletedServicesRecord> fetchCompletedJobs() {
        List<ServicesDataProvider.CompletedServicesRecord> data = new ArrayList<>();
        String query = """
                SELECT  jc.job_id, job_status, service_type, customer_name,\s
                full_name AS mechanic,\s
                DATE(logged_date) AS service_date,
                brand, plate_number, sr.service_cost,
                (SELECT COUNT(*) FROM purchased_job_item AS pji WHERE pji.job_id = jc.job_id) items_count,
                (SELECT SUM(price) FROM purchased_job_item AS pji WHERE pji.job_id = jc.job_id) AS total_items_cost,
                stars, comments, DATE(cf.entry_date) as feedback_date, jc.last_updated AS date_completed
                FROM job_cards AS jc
                INNER JOIN service_requests AS sr
                	ON jc.service_id = sr.id
                INNER JOIN customers AS c\s
                	ON c.record_id = sr.customer_id
                INNER JOIN service_types AS st
                	ON sr.service_type_id = st.record_id
                INNER JOIN employees AS e\s
                	ON sr.assigned_mechanic = e.record_id
                INNER JOIN customer_vehicles AS cv\s
                	ON sr.vehicle_id = cv.record_id
                LEFT JOIN customer_feedbacks AS cf
                	ON jc.job_id = cf.job_id
                WHERE job_status = 'completed'
                """;
        try {
//job_id, job_status, service_type, mechanic, service_date, brand, plate_number, service_cost, items_count, total_items_cost
            //stars, comments, DATE(cf.entry_date) as feedback_date
            resultSet = getCon().prepareStatement(query).executeQuery();
            while (resultSet.next()) {
                data.add(new ServicesDataProvider.CompletedServicesRecord(
                        resultSet.getInt("job_id"),
                        resultSet.getString("job_status"),
                        resultSet.getString("service_type"),
                        resultSet.getString("customer_name"),
                        resultSet.getTimestamp("service_date"),
                        resultSet.getTimestamp("date_completed"),
                        resultSet.getString("mechanic"),
                        resultSet.getString("brand"),
                        resultSet.getString("plate_number"),
                        resultSet.getDouble("service_cost"),
                        resultSet.getInt("items_count"),
                        resultSet.getDouble("total_items_cost"),
                        resultSet.getString("stars"),
                        resultSet.getString("comments"),
                        resultSet.getTimestamp("feedback_date")
                ));
            }
            resultSet.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }

    public Map<String, String> fetchTransactionsMetaData() {
        var data = new HashMap<String, String>();
        String query = """
                SELECT\s
                    awaiting_payment,
                    payment_count,
                    service_cost,
                    purchased_items_cost,
                    (service_cost + purchased_items_cost) AS total_amount
                FROM (
                    SELECT\s
                        (SELECT COUNT(id) FROM service_requests WHERE service_status = 'completed') AS awaiting_payment,
                        (SELECT COUNT(id) FROM service_requests WHERE service_status = 'paid') AS payment_count,
                        (
                            SELECT SUM(service_cost) FROM service_requests AS sr
                            INNER JOIN job_cards AS jc ON sr.id = jc.service_id
                            WHERE service_status = 'completed'
                        ) AS service_cost,
                        (
                            SELECT SUM(price) FROM purchased_job_item AS pji
                                INNER JOIN job_cards AS jc USING(job_id)
                                INNER JOIN service_requests AS sr\s
                        			ON jc.service_id = sr.id
                                WHERE service_status = 'completed'
                        ) AS purchased_items_cost
                ) AS derived;
                """;

        try {
            resultSet = getCon().prepareStatement(query).executeQuery();
            if (resultSet.next()) {
                data.put("awaiting_payment", resultSet.getString("awaiting_payment"));
                data.put("payment_count", resultSet.getString("payment_count"));
                data.put("service_cost", resultSet.getString("service_cost"));
                data.put("purchased_items_cost", resultSet.getString("purchased_items_cost"));
                data.put("total_amount", resultSet.getString("total_amount"));
            }
            resultSet.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return data;
    }

    public List<TransactionsDataProvider.TransactionRecord> fetchPaidAndUnpaidTransactions() {
        List<TransactionsDataProvider.TransactionRecord> data = new ArrayList<>();
        try {
            String query =  """
                    SELECT\s
                        jc.job_id,\s
                        sr.customer_id,
                        jc.service_id,
                        c.customer_name,
                        ca.account_balance,
                        sr.logged_date,\s
                        st.service_type,\s
                        sr.service_cost,
                        COALESCE(pji_sum.items_cost, 0) AS items_cost,
                        sr.service_status
                    FROM service_requests AS sr
                    INNER JOIN customers AS c ON sr.customer_id = c.record_id
                    INNER JOIN customer_account AS ca ON c.record_id = ca.customer_id
                    INNER JOIN job_cards AS jc ON sr.id = jc.service_id
                    INNER JOIN service_types AS st ON sr.service_type_id = st.record_id
                    LEFT JOIN (
                        SELECT job_id, SUM(price) AS items_cost
                        FROM purchased_job_item
                        GROUP BY job_id
                    ) AS pji_sum ON jc.job_id = pji_sum.job_id
                    WHERE sr.service_status IN ('completed', 'paid');
                    """;
            resultSet = getCon().prepareStatement(query).executeQuery();
            while(resultSet.next()) {
                data.add(new TransactionsDataProvider.TransactionRecord(
                        resultSet.getInt("job_id"),
                        resultSet.getInt("service_id"),
                        resultSet.getInt("customer_id"),
                        resultSet.getString("customer_name"),
                        resultSet.getString("service_type"),
                        resultSet.getDate("logged_date"),
                        resultSet.getDouble("account_balance"),
                        resultSet.getDouble("service_cost"),
                        resultSet.getDouble("items_cost"),
                        resultSet.getString("service_status")
                ));
            }
            resultSet.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public ArrayList<TransactionsDataProvider.Payments> getPaymentDetails() {
        ArrayList<TransactionsDataProvider.Payments> data = new ArrayList<>();
        String query = "SELECT * FROM payments;";
        try {
            resultSet = getCon().prepareStatement(query).executeQuery();
            while(resultSet.next()) {
                data.add(new TransactionsDataProvider.Payments(
                        resultSet.getInt("record_id"),
                        resultSet.getInt("job_id"),
                        resultSet.getTimestamp("entry_date"),
                        resultSet.getString("pay_reference"),
                        resultSet.getString("pay_method"),
                        resultSet.getString("service_items_cost"),
                        resultSet.getString("discount_amount"),
                        resultSet.getString("labour_cost"),
                        resultSet.getString("collected_amount")
                ));
            }
            resultSet.close();
        }catch (Exception ex) {
            ex.printStackTrace();
            logError(ex, "getPaymentDetails");
        }

        return data;
    }

    public List<NotificationRecords.LogsRecord> fetchSystemNotificationLogs(int shopId) {
        List<NotificationRecords.LogsRecord> items = new ArrayList<>();
        try {
            String query = """
                    SELECT * FROM notification_logs
                    WHERE SHOP_ID = ?
                    ORDER BY log_id DESC LIMIT 15;
                    """;
            prepare = getCon().prepareStatement(query);
            prepare.setInt(1, shopId);
            resultSet = prepare.executeQuery();
            while(resultSet.next()) {
                items.add(new NotificationRecords.LogsRecord(
                        resultSet.getInt("log_id"),
                        resultSet.getString("title"),
                        resultSet.getString("content"),
                        resultSet.getTimestamp("date_logged"),
                        resultSet.getInt("shop_id")
                ));
            }
            prepare.close();
            resultSet.close();
        }catch (Exception ex) {
            ex.printStackTrace();
            logError(ex, "notificationLogs");
        }
        return items;
    }

    public Map<String, String> shopInformation(int id) {
        Map<String, String> data = new HashMap<>();
        String query = "SELECT * FROM auto_mechanics.mechanic_shops WHERE record_id = '"+id+"';";
        //record_id, shop_name, address, mobile_number, other_number, slogan, email, sms_id, date_created, is_active, activation_key
        try {
            resultSet = getCon().prepareStatement(query).executeQuery();
            if(resultSet.next()) {
                data.put("id", resultSet.getString("record_id"));
                data.put("shop_name", resultSet.getString("shop_name"));
                data.put("address", resultSet.getString("address"));
                data.put("mobile_number", resultSet.getString("mobile_number"));
                data.put("email", resultSet.getString("email"));
                data.put("description", resultSet.getString("slogan"));
                data.put("weekdays", resultSet.getString("weekdays"));
                data.put("weekends", resultSet.getString("weekends"));
            }
            resultSet.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public List<UsersDataProvider.LoginUserRecord> fetchUserByUsername(String username) {
        List<UsersDataProvider.LoginUserRecord> data = new ArrayList<>();
        String query = """
                SELECT\s
                    user_id,\s
                    username,
                    CASE\s
                        WHEN user_role = 'admin' THEN full_name
                        WHEN user_role = 'mechanic' THEN full_name
                        WHEN user_role = 'customer' THEN customer_name
                        ELSE full_name
                    END AS combined_name,
                    CASE\s
                		WHEN c.is_active = TRUE THEN 'active'
                        WHEN e.is_active = TRUE THEN 'active'
                    END AS active_status,
                    user_role,\s
                    user_password,\s
                    u.shop_id
                FROM users AS u
                LEFT JOIN employees AS e ON u.reference_id = e.record_id
                LEFT JOIN customers AS c ON u.reference_id = c.record_id
                WHERE username = lower(?) AND (c.is_active = TRUE OR e.is_active = TRUE)
                """;
        try {
            prepare = getCon().prepareStatement(query);
            prepare.setString(1, username.toLowerCase());
            resultSet = prepare.executeQuery();
            if (resultSet.next()) {
                data.add(new UsersDataProvider.LoginUserRecord(
                        resultSet.getInt("user_id"),
                        resultSet.getInt("shop_id"),
                        resultSet.getString("username"),
                        resultSet.getString("user_role"),
                        resultSet.getString("combined_name"),
                        resultSet.getString("active_status"),
                        resultSet.getString("user_password")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

}//end of class...
