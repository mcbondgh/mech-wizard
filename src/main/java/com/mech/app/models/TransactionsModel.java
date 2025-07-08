package com.mech.app.models;

import com.mech.app.configfiles.ErrorLoggerTemplate;
import com.mech.app.dataproviders.dao.DAO;
import com.mech.app.dataproviders.transactions.TransactionsDataProvider;

import java.time.LocalDateTime;

public class TransactionsModel extends DAO {

    public int updateServiceTransaction(TransactionsDataProvider.UpdateRecord dataModel) {
        String serviceQuery = """
                UPDATE service_requests
                SET service_status = 'paid', last_updated = DEFAULT WHERE id = ?;
                """;
        String customerAccountQuery = """
                UPDATE customer_account
                SET account_balance = ?, last_updated = DEFAULT WHERE customer_id = ?;
                """;
        String paymentsQuery = """
                INSERT INTO payments(job_id, pay_reference, pay_method, service_items_cost, labour_cost, collected_amount)
                VALUES (?, ?, ?, ?, ?, ?);
                """;
        String logsQuery = """
                INSERT INTO transaction_logs(customer_id, transaction_date, transaction_type, transaction_id, payment_method,
                notes, amount)
                VALUES(?, current_timestamp(), 'service payment', ?, ?, 'payment for service', ?);
                """;
        try {
            prepare = getCon().prepareStatement(serviceQuery);
            prepare.setInt(1, dataModel.serviceId());
            int counter = prepare.executeUpdate();

            prepare = getCon().prepareStatement(customerAccountQuery);
            prepare.setDouble(1, dataModel.accountBal());
            prepare.setInt(2, dataModel.customerId());
            counter += prepare.executeUpdate();

            prepare = getCon().prepareStatement(paymentsQuery);
            prepare.setInt(1, dataModel.jobId());
            prepare.setString(2, dataModel.reference());
            prepare.setString(3, dataModel.payMethod());
            prepare.setDouble(4, dataModel.serviceCost());
            prepare.setDouble(5, dataModel.labourAmount());
            prepare.setDouble(6, dataModel.payableAmount());
            counter += prepare.executeUpdate();

            prepare = getCon().prepareStatement(logsQuery);
            prepare.setInt(1, dataModel.customerId());
            prepare.setString(2, dataModel.reference());
            prepare.setString(3, dataModel.payMethod());
            prepare.setDouble(4, dataModel.payableAmount());
            counter += prepare.executeUpdate();
            return counter;
        }catch (Exception ex ) {
            ex.printStackTrace();
            new ErrorLoggerTemplate(LocalDateTime.now().toString(), ex.getLocalizedMessage(), "updateServiceTransaction").logErrorToFile();
            logError(ex, "updateServiceTransaction");
        }
        return 0;
    }
}
