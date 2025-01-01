package com.dolphin.adminbackend;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.dolphin.adminbackend.model.User;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.dolphin.adminbackend.model.Customer;

import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles("test") // Activates application-test.properties
public class InheritanceJoinedTest {

    @Autowired
    EntityManager entityManager;

    @Test
    public void testJoinedInheritance() {
        // Create and persist a customer instance
        Customer customer = new Customer();
        customer.setEmail("customer@example.com");
        customer.setPassword("securepassword");
        customer.setFullName("John Doe");
        customer.setAge(30);
        customer.setGender("Male");
        entityManager.persist(customer); // USER is a reserved keyword in many databases, including H2 and MariaDB
        entityManager.flush(); // Synchronize the persistence context to the underlying database.

        // Query the User table to test polymorphic query
        List<User> users = entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();

        // verify that there's one row in user, that it's a customer and has an email
        assertThat(users).hasSize(1);
        assertThat(users.get(0)).isInstanceOf(Customer.class);
        assertThat(users.get(0).getEmail()).isEqualTo("customer@example.com");

        /*
         * Notes about isInstanceOf
         * 
         * User user = new Customer(); // A Customer stored as a User
         * Check if the actual object (Customer) is an instance of Customer
         * System.out.println(user instanceof Customer); // true
         * System.out.println(user instanceof User); // true
         * 
         * It’s essentially saying:
         * 
         * "I expect the object referred to by the user variable to be an instance of Customer."
         * The actual type of the object is being checked, not the declared type of the
         * variable.
         */

        // Query the Customer table directly
        List<Customer> customers = entityManager.createQuery("SELECT c FROM Customer c", Customer.class)
                .getResultList();

        // verify that there's one row in customer, has age and gender
        assertThat(customers).hasSize(1);
        Customer retrievedCustomer = customers.get(0);
        assertThat(retrievedCustomer.getAge()).isEqualTo(30);
        assertThat(retrievedCustomer.getGender()).isEqualTo("Male");
    }

}
