package ir.maktab.forthphase.service;

import ir.maktab.forthphase.data.dto.searchrequest.CustomerSearchRequest;
import ir.maktab.forthphase.data.dto.searchrequest.ExpertSearchRequest;
import ir.maktab.forthphase.data.model.Customer;
import ir.maktab.forthphase.data.model.Expert;
import ir.maktab.forthphase.data.model.Services;
import ir.maktab.forthphase.data.model.SubServices;
import ir.maktab.forthphase.data.model.enums.ExpertStatus;
import ir.maktab.forthphase.exceptions.DuplicateServiceNameException;
import ir.maktab.forthphase.exceptions.DuplicateSubServiceNameException;
import ir.maktab.forthphase.exceptions.NoSuchUserFound;
import ir.maktab.forthphase.exceptions.SubServiceNameNotFoundException;
import ir.maktab.forthphase.util.ExpertUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminServiceTests {

    @Autowired
    private AdminService adminService;

    static Services services;
    static SubServices washSubServices;
    static SubServices cleanSubServices;
    static SubServices cookSubServices;
    static Expert expert;

    @SneakyThrows
    @BeforeAll
    public static void setUp() {

        BufferedImage image = ImageIO.read(new File("src/face.jpg"));
        byte[] jpgs = ExpertUtil.toByteArray(image, "jpg");

        Set<SubServices> subServicesSet = new HashSet<>();
        Set<Expert> expertsSet = new HashSet<>();

        expert = Expert.builder()
                .expertStatus(ExpertStatus.NEW)
                .aboutMe("hello")
                .rating(0)
                .image(jpgs).build();
        expert.setFirstName("addedExpert");
        expert.setEmail("expert@gmail.com");
        expert.setPassword("Expert12");
        expert.setNationalCode("0111846730");

        washSubServices = SubServices.builder()
                .name("washing")
                .description("wash stuff")
                .basePrice(120000)
                .expertList(expertsSet).build();

        cleanSubServices = SubServices.builder()
                .name("cleaning")
                .description("clean stuff")
                .basePrice(156000)
                .expertList(expertsSet).build();

        cookSubServices = SubServices.builder()
                .name("cooking")
                .description("cook food")
                .basePrice(170000)
                .build();

        subServicesSet.add(cleanSubServices);
        subServicesSet.add(washSubServices);
        expertsSet.add(expert);

        services = Services.builder()
                .name("Home activity")
                .subServices(subServicesSet).build();
    }

    @SneakyThrows
    @Test
    public void addNewServiceByAdminTest() {
        adminService.addNewService(services);
    }

    @SneakyThrows
    @Test
    public void addNewSubServiceTest() {
        adminService.addNewSubService(cookSubServices, services.getName());
    }

    @Test
    public void addDuplicateSubServiceTest() {
        Assertions.assertThrows(DuplicateSubServiceNameException.class,
                () -> adminService.addNewSubService(washSubServices, services.getName()));
    }

    @Test
    public void addDuplicateServiceTest() {
        Assertions.assertThrows(DuplicateServiceNameException.class,
                () -> adminService.addNewService(services));
    }

    @Test
    public void showAllServicesTest() {
        List<Services> servicesSet = adminService.showAllServices();
        servicesSet.forEach(System.out::println);
        assertNotNull(servicesSet);
    }

    @Test
    public void editSubServicePriceWithInvalidSubServiceNameTest() {
        Assertions.assertThrows(SubServiceNameNotFoundException.class,
                () -> adminService.editSubServicePrice("FakeName", 20000));
    }

    @SneakyThrows
    @Test
    public void editSubServicePriceWithValidSubServiceNameTest() {
        adminService.editSubServicePrice(cleanSubServices.getName(), 20040);
    }

    @Test
    public void editSubServiceDescriptionWithInvalidSubServiceNameTest() {
        Assertions.assertThrows(SubServiceNameNotFoundException.class,
                () -> adminService.editSubServicesDescription("FakeName", "not valid"));
    }

    @SneakyThrows
    @Test
    public void editSubServiceDescriptionWithValidSubServiceNameTest() {
        adminService.editSubServicesDescription(cleanSubServices.getName(), "cleaning home");
    }

    @Test
    public void acceptExpertWithInvalidEmailTest() {
        Assertions.assertThrows(NoSuchUserFound.class,
                () -> adminService.acceptExpert("fakeEmail@gmail.com"));
    }

    @SneakyThrows
    @Test
    public void acceptExpertWithValidEmailTest() {
        adminService.acceptExpert(expert.getEmail());
    }

    @Test
    public void showListOfNewExpertsTest() {
        List<Expert> experts = adminService.showListOfNewExperts();
        experts.forEach(System.out::println);
        assertNotNull(experts);
    }

    @SneakyThrows
    @Test
    public void addExpertToSubServiceTest() {
        adminService.addExpertToSubService("expert@gmail.com", "wash");
    }

    @Test
    @SneakyThrows
    public void deleteExpertFromSubServiceTest() {
        adminService.deleteExpertFromSubService("expert@gmail.com", "washing");
    }

    @Test
    @SneakyThrows
    public void showCustomersByFilter() {
        CustomerSearchRequest userSearchRequest = new CustomerSearchRequest();
        userSearchRequest.setEmail("fatemeh@gmail.com");
        userSearchRequest.setFirstName("arezoo");
        List<Customer> customers = adminService.showListOfCustomersByApplyFilter(userSearchRequest);
        System.out.println(customers.toString());
    }

    @Test
    @SneakyThrows
    public void showExpertsByFilter() {
        ExpertSearchRequest expertSearchRequest = new ExpertSearchRequest();
        expertSearchRequest.setEmail("sara@gmail.com");
        expertSearchRequest.setFirstName("zahra");
        expertSearchRequest.setStatus("max");
        expertSearchRequest.setSubServiceName("wash");
        List<Expert> experts = adminService.showListOfExpertsByApplyFilter(expertSearchRequest);
        System.out.println(experts.toString());
    }
}
