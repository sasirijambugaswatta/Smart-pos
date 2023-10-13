package lk.ijse.dep11.pos.db;

import lk.ijse.dep11.pos.tm.Customer;
import lk.ijse.dep11.pos.tm.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderDataAccessTest {

    @BeforeEach
    void setUp() throws SQLException {
        SingleConnectionDataSource.getInstance().getConnection().setAutoCommit(false);
    }

    @AfterEach
    void tearDown() throws SQLException {
        SingleConnectionDataSource.getInstance().getConnection().rollback();
        SingleConnectionDataSource.getInstance().getConnection().setAutoCommit(true);
    }

    @Test
    void existsOrderByCustomerId() throws SQLException {
        assertDoesNotThrow(()-> assertFalse(OrderDataAccess.existsOrderByCustomerId("ABC")));
        CustomerDataAccess.saveCustomer(new Customer("ABC", "Crazy", "Panadura"));
        SingleConnectionDataSource.getInstance().getConnection().createStatement()
            .executeUpdate("INSERT INTO \"order\" (id, customer_id) VALUES ('111111', 'ABC')");
        assertDoesNotThrow(()-> assertTrue(OrderDataAccess.existsOrderByCustomerId("ABC")));
    }

    @Test
    void existsOrderByItemCode() throws SQLException {
        assertDoesNotThrow(()->assertFalse(OrderDataAccess.existsOrderByItemCode("Crazy Item Code")));

        ItemDataAccess.saveItem(new Item("II12345678", "Crazy Item", 5, new BigDecimal("1250")));
        CustomerDataAccess.saveCustomer(new Customer("ABC", "Crazy", "Panadura"));
        SingleConnectionDataSource.getInstance().getConnection().createStatement()
                .executeUpdate("INSERT INTO \"order\" (id, customer_id) VALUES ('111111', 'ABC')");
        SingleConnectionDataSource.getInstance().getConnection().createStatement()
                .executeUpdate("INSERT INTO order_item (order_id, item_code, qty, unit_price) VALUES ('111111', 'II12345678', 2, 1250.00)");
        assertTrue(OrderDataAccess.existsOrderByItemCode("II12345678"));
    }

    @Test
    void getLastOrderId() throws SQLException {
        ResultSet rst = SingleConnectionDataSource.getInstance().getConnection().createStatement()
                .executeQuery("SELECT COUNT(id) FROM \"order\"");
        rst.next();
        int count = rst.getInt(1);
        if (count == 0){
            assertNull(OrderDataAccess.getLastOrderId());

            CustomerDataAccess.saveCustomer(new Customer("ABC", "Crazy", "Panadura"));
            SingleConnectionDataSource.getInstance().getConnection().createStatement()
                    .executeUpdate("INSERT INTO \"order\" (id, customer_id) VALUES ('111111', 'ABC')");
            assertNotNull(OrderDataAccess.getLastOrderId());
        }else {
            assertNotNull(OrderDataAccess.getLastOrderId());
        }
    }
}