package ir.maktab.forthphase.service;

import ir.maktab.thirdphase.data.dto.CustomerLoginDto;
import ir.maktab.thirdphase.data.dto.searchrequest.UserSearchRequest;
import ir.maktab.thirdphase.data.model.*;
import ir.maktab.thirdphase.exceptions.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class CustomerServiceTests {

    @Autowired
    private CustomerService customerService;

    static Customer customer;
    static Order order;
    static Proposal proposal;
    static Opinion opinion;

    @SneakyThrows
    @BeforeAll
    public static void setUp() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        customer = Customer.builder()
                .address("mashhad").build();
        customer.setFirstName("sanaz");
        customer.setLastName("sadeghi");
        customer.setPassword("Sana1234");
        customer.setEmail("customer@gmail.com");
        customer.setNationalCode("0111131111");

        proposal = Proposal.builder()
                .cost(200000)
                .neededTime("2 hours")
                .timeOfDoing(dateFormat.parse("2023-10-10"))
                .expertEmail("expert@gmail.com")
                .orderCode("Abcdef123456")
                .build();

        order = Order.builder()
                .address("Tehran")
                .cost(130000)
                .requiredDate(dateFormat.parse("2023-9-9"))
                .description("cleaning home")
                .orderCode("ABcdef123456")
                .serviceName("Home")
                .writeInDate(new Date())
                .subServiceName("wash").build();

        opinion = Opinion.builder()
                .rate(4.7)
                .opinionText("that was good")
                .build();
    }

    @Test
    @SneakyThrows
    public void registerNewCustomerTest() {
        customerService.register(customer);
    }

    @Test
    public void registerNewCustomerWithDuplicateEmailTest() {
        Assertions.assertThrows(DuplicateEmailException.class,
                () -> customerService.register(customer));
    }

    @Test
    @SneakyThrows
    public void loginTest() {
        CustomerLoginDto login = customerService.login("fatemeh@gmail.com", "Fate1234");
        assertNotNull(login);
    }

    @Test
    public void loginWithInvalidEmailTest() {
        Assertions.assertThrows(NoSuchUserFound.class,
                () -> customerService.login("fake@gmail.com", customer.getPassword()));
    }

    @Test
    public void loginWithInvalidPasswordTest() {
        Assertions.assertThrows(InvalidPasswordException.class,
                () -> customerService.login("fatemeh@gmail.com", "fake"));
    }

    @Test
    @SneakyThrows
    public void addNewOrderTest() {
        customerService.addNewOrder(customer.getEmail(), order);
    }

    @Test
    @SneakyThrows
    public void showAllOfProposalsOrderedByProposalCostTest() {
        Set<Proposal> proposals = customerService.showAllOfProposalsOrderedByProposalCost("abcdef123456");
        assertNotNull(proposals);
    }

    @Test
    @SneakyThrows
    public void showAllOfProposalsOrderedByExpertRateTest() {
        Set<Proposal> proposals = customerService.showAllOfProposalsOrderedByExpertRate("abcdef123456");
        assertNotNull(proposals);
    }

    @Test
    @SneakyThrows
    public void chooseExpertForOrderTest() {
        customerService.chooseExpertForOrder("Abcdef123456", "expert@gmail.com");
    }

    @Test
    @SneakyThrows
    public void setStartedStatusForOrder() {
        Assertions.assertThrows(InvalidRequiredDateException.class,
                () -> customerService.setStartedStatusForOrder("abcdef123456"));
    }

    @Test
    @SneakyThrows
    public void setDoneStatusForOrderTest() {
        customerService.setDoneStatusForOrder("abcdef123456");
    }

    @Test
    @SneakyThrows
    public void changePasswordTest() {
        customerService.changePassword(customer.getEmail(), "1234AbCd", "1234AbCd");
    }

    @Test
    @SneakyThrows
    public void changePasswordWithUnequalPasswordsTest() {
        Assertions.assertThrows(UnequalPasswordsException.class,
                () -> customerService.changePassword(customer.getEmail(), "1234A", "1234AbCd"));
    }

    @Test
    @SneakyThrows
    public void showAllOrdersTest() {
        Set<Order> orders = customerService.showAllOrders("fatemeh@gmail.com");
        assertNotNull(orders);
    }

    @Test
    @SneakyThrows
    public void addOpinionForOrderTest() {
        customerService.addOpinionForOrder(opinion, "abcdef123456");
    }

    @Test
    @SneakyThrows
    public void applyFilterForExpertTest() {
        UserSearchRequest request = new UserSearchRequest();
        request.setLastName("akbari");
        request.setFirstName("fatemeh");
        request.setEmail("fereshteh@gmail.com");
        List<Customer> customers = customerService.applyFilter(request);
        customers.forEach(System.out::println);
    }
}
