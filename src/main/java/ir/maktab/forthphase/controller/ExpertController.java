package ir.maktab.forthphase.controller;

import ir.maktab.forthphase.config.MessageSourceConfiguration;
import ir.maktab.forthphase.config.SecurityUtil;
import ir.maktab.forthphase.data.dto.ExpertSaveRequestDto;
import ir.maktab.forthphase.data.dto.ProposalDto;
import ir.maktab.forthphase.data.model.Expert;
import ir.maktab.forthphase.data.model.Proposal;
import ir.maktab.forthphase.service.ExpertService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.repository.query.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@Slf4j
@RequestMapping("/expert")
public class ExpertController {

    private final ExpertService expertService;
    private final ModelMapper modelMapper;
    final MessageSourceConfiguration messageSource;

    public ExpertController(ExpertService expertService,
                            ModelMapper modelMapper,
                            MessageSourceConfiguration messageSource) {
        this.expertService = expertService;
        this.modelMapper = modelMapper;
        this.messageSource = messageSource;
    }

    @PostMapping("/new")
    public String addNewExpert(@Valid @RequestBody ExpertSaveRequestDto saveRequestDto,
                               HttpServletRequest request) {
        log.info("... adding new customer with info: '{}' ...", saveRequestDto);
        Expert expert = expertService.PrepareNewObject(saveRequestDto);
        expertService.register(expert, getSiteURL(request));
        return "You have signed up successfully!\n" +
                "Please check your email to verify your account.";
    }

    @GetMapping("/orders_by_sub_service")
    public String showOrdersRelatedToSubService(@RequestParam(name = "subServiceName") String subServiceName) {
        Expert expert = (Expert) SecurityUtil.getCurrentUser();
        return expertService.showOrdersRelatedToSubService(expert.getEmail(), subServiceName).toString();
    }

    @PostMapping("/add_new_proposal")
    public void addNewProposalForOrder(@RequestBody ProposalDto proposalDto,
                                       @RequestParam("orderCode") String orderCode) {
        Expert expert = (Expert) SecurityUtil.getCurrentUser();
        Proposal proposal = modelMapper.map(proposalDto, Proposal.class);
        expertService.addNewProposalForOrder(expert.getEmail(), proposal, orderCode);
    }

    @GetMapping("/related_orders/{name}")
    public String showRelatedOrders(@PathVariable("name") String subServiceName) {
        return expertService.showRelatedOrders(subServiceName).toString();
    }

    @PostMapping("/change_password")
    public void changePassword(@RequestParam String password,
                               @RequestParam String confirmedPassword) {
        Expert expert = (Expert) SecurityUtil.getCurrentUser();
        log.info("... change password request by : '{}' ...", expert.getEmail());
        expertService.changePassword(expert.getEmail(), password, confirmedPassword);
    }

    @GetMapping("/opinions")
    public String showExpertOpinions() {
        Expert expert = (Expert) SecurityUtil.getCurrentUser();
        return expertService.getExpertOpinions(expert.getEmail()).toString();
    }

    @PostMapping("/save_image")
    public void saveExpertImage() {
        Expert expert = (Expert) SecurityUtil.getCurrentUser();
        expertService.saveExpertImageToFile(expert.getEmail());
    }

    @GetMapping("/my_rating")
    public double showExpertRating() {
        Expert expert = (Expert) SecurityUtil.getCurrentUser();
        return expertService.getExpertRating(expert.getEmail());
    }

    @GetMapping("/order_history")
    public String showOrderHistory() {
        Expert expert = (Expert) SecurityUtil.getCurrentUser();
        return expertService.showOrderHistory(expert.getEmail()).toString();
    }

    @GetMapping("/verify")
    public String verifyUser(@Param("code") String code) {
        if (expertService.verify(code)) {

            return "Congratulations, your account has been verified.\n" +
                    "you must wait for accepting from admin";
        } else {
            return "Sorry, we could not verify account. It maybe already verified,\n" +
                    "    or verification code is incorrect.";
        }
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }
}
