package ir.maktab.forthphase.service;

import ir.maktab.forthphase.data.dto.searchrequest.ExpertSearchRequest;
import ir.maktab.forthphase.data.model.Expert;
import ir.maktab.forthphase.data.model.Order;
import ir.maktab.forthphase.data.model.Proposal;
import ir.maktab.forthphase.data.model.enums.ExpertStatus;
import ir.maktab.forthphase.exceptions.DuplicateEmailException;
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
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
public class ExpertServiceTests {

    @Autowired
    ExpertService expertService;
    static Expert duplicateExpert;
    static Expert unavailableExpert;
    static Expert expert;
    static Proposal proposal;

    @SneakyThrows
    @BeforeAll
    public static void setUp() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        BufferedImage image = ImageIO.read(new File("src/face.jpg"));
        byte[] jpgs = ExpertUtil.toByteArray(image, "jpg");

        duplicateExpert = Expert.builder()
                .expertStatus(ExpertStatus.WAIT_FOR_VERIFY_EMAIL)
                .aboutMe("hello")
                .rating(0)
                .image(jpgs)
                .build();
        duplicateExpert.setNationalCode("0888882688");
        duplicateExpert.setEmail("sara@gmail.com");
        duplicateExpert.setPassword("Expert12");

        proposal = Proposal.builder()
                .cost(200000)
                .neededTime("2 hours")
                .timeOfDoing(dateFormat.parse("2023-12-12"))
                .expertEmail("expert@gmail.com")
                .customerEmail("customer@gmail.com")
                .build();

        expert = Expert.builder()
                .expertStatus(ExpertStatus.WAIT_FOR_VERIFY_EMAIL)
                .aboutMe("hello")
                .rating(0)
                .image(jpgs)
                .build();
        expert.setEmail("expert@outlook.ir");
        expert.setPassword("Expert12");
        expert.setNationalCode("0888888899");

        unavailableExpert = Expert.builder()
                .expertStatus(ExpertStatus.WAIT_FOR_VERIFY_EMAIL)
                .aboutMe("hello")
                .rating(0)
                .image(jpgs)
                .build();
        unavailableExpert.setEmail("unavailableExpert@yahoo.com");
        unavailableExpert.setPassword("Expert12");
        unavailableExpert.setNationalCode("0050900900");
    }

    @SneakyThrows
    @Test
    public void signUpNewExpertTest() {
        expertService.register(expert);
    }

    @Test
    public void addDuplicateExpertEmailTest() {
        Assertions.assertThrows(DuplicateEmailException.class,
                () -> expertService.register(duplicateExpert));
    }

    @Test
    @SneakyThrows
    public void showOrdersRelatedToSubServiceTest() {
        Set<Order> orders = expertService.showOrdersRelatedToSubService("sara@gmail.com", "wash");
        assertNotNull(orders);
    }

    @Test
    @SneakyThrows
    public void addNewProposalForOrderTest() {
        expertService.addNewProposalForOrder("fereshteh@gmail.com", proposal, "Abcdef123456");
    }

    @Test
    public void showRelatedOrdersTest() {
        Set<Order> orders = expertService.showRelatedOrders("wash");
        assertNotNull(orders);
    }

    @SneakyThrows
    @Test
    public void getExpertImageFromDatabaseAndSaveToFileTest() {
        expertService.saveExpertImageToFile("expert@gmail.com");
    }

    @Test
    @SneakyThrows
    public void applyFilterForExpertTest() {
        ExpertSearchRequest request = new ExpertSearchRequest();
        request.setLastName("fatemi");
        request.setFirstName("zahra");
        request.setEmail("fereshteh@gmail.com");
        request.setStatus("MAX");
        List<Expert> experts = expertService.applyFilter(request);
        experts.forEach(System.out::println);
    }
}
