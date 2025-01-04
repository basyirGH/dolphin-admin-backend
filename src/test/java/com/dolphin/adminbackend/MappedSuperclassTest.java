package com.dolphin.adminbackend;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.dolphin.adminbackend.model.jpa.Customer;
import com.dolphin.adminbackend.model.jpa.User;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
// Since User is a @MappedSuperclass, we can still test inheritance-related 
// functionality indirectly through the Customer entity, which inherits User's fields.
// Spring Boot automatically replaces the data source with an embedded database
// during tests when H2 is available on the classpath. You don’t need to specify
// @AutoConfigureTestDatabase.
/// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test") // Activates application-test.properties

public class MappedSuperclassTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    public void testMappedSuperClass() {
        // Create and persist a Customer instance
        Customer customer = new Customer();
        customer.setEmail("customer@example.com");
        customer.setPassword("securepassword");
        customer.setFullName("John Doe");
        customer.setAge(30);
        customer.setGender("Male");

        entityManager.persist(customer);
        entityManager.flush(); ////Synchronize the persistence context to the underlying database.

        //retrieve from h2 db by email
        Customer retrievedCustomer = entityManager
                .createQuery("SELECT c FROM Customer c WHERE c.email = :email", Customer.class)
                .setParameter("email", "customer@example.com")
                .getSingleResult();

        // Verify the results
        assertThat(retrievedCustomer).isInstanceOf(User.class);
        //Customer retrievedCustomer = (Customer) retrievedUser;
        assertThat(retrievedCustomer.getAge()).isEqualTo(30);
        assertThat(retrievedCustomer.getGender()).isEqualTo("Male");
    }
}
