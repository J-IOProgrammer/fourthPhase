package ir.maktab.forthphase.controller;

import ir.maktab.forthphase.config.MessageSourceConfiguration;
import ir.maktab.forthphase.config.SecurityUtil;
import ir.maktab.forthphase.data.dto.ExpertSaveRequestDto;
import ir.maktab.forthphase.data.dto.ProposalDto;
import ir.maktab.forthphase.data.model.Expert;
import ir.maktab.forthphase.data.model.Proposal;
import ir.maktab.forthphase.exceptions.InvalidRequestForDoNotExistSubServiceException;
import ir.maktab.forthphase.exceptions.InvalidTokenException;
import ir.maktab.forthphase.exceptions.ReVerifyException;
import ir.maktab.forthphase.exceptions.SendProposalOnInvalidSubServiceException;
import ir.maktab.forthphase.service.ExpertService;
import ir.maktab.forthphase.util.TokenProducer;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@Slf4j
@RequestMapping("/expert")
public class ExpertController {

    private final ExpertService expertService;
    private final ModelMapper modelMapper;
    private String verifyToken;
    final MessageSourceConfiguration messageSource;

    public ExpertController(ExpertService expertService,
                            ModelMapper modelMapper,
                            MessageSourceConfiguration messageSource) {
        this.expertService = expertService;
        this.modelMapper = modelMapper;
        this.messageSource = messageSource;
    }

    @PostMapping("/new")
    public String addNewExpert(@Valid @RequestBody ExpertSaveRequestDto saveRequestDto) {
        log.info("... adding new customer with info: '{}' ...", saveRequestDto);
        Expert expert = expertService.PrepareNewObject(saveRequestDto);
        expertService.register(expert);
        return messageSource.getMessage("ok.message.successful_operation");
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

    @PostMapping("/verify/{expertEmail}")
    public String verifyEmail(@PathVariable String expertEmail) {
        verifyToken = TokenProducer.generateToken();
        return "http://localhost:8080/expert/confirm_verifying/" + verifyToken + "/" + expertEmail;
    }

    @PostMapping("/confirm_verifying/{token}/{expertEmail}")
    public String verifyEmailByToken(@PathVariable String token, @PathVariable String expertEmail) {
        if (token.contains(expertEmail))
            throw new InvalidTokenException();
        verifyToken += expertEmail;
        log.info("... user : '{}' verifying with token : '{}' ", expertEmail, verifyToken);
        expertService.verifyEmail(expertEmail);
        return messageSource.getMessage("ok.message.successful_operation");
    }

    @ExceptionHandler(InvalidRequestForDoNotExistSubServiceException.class)
    public ResponseEntity<?> handleInvalidRequestForDoNotExistSubServiceException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                messageSource.getMessage("errors.message.invalid_request_for_do_not_exist_sub_service"));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidTokenException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                messageSource.getMessage("errors.message.invalid_token"));
    }

    @ExceptionHandler(SendProposalOnInvalidSubServiceException.class)
    public ResponseEntity<?> handleSendProposalOnInvalidSubServiceException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                messageSource.getMessage("errors.message.send_proposal_on_invalid_sub_service"));
    }

    @ExceptionHandler(SendProposalOnInvalidSubServiceException.class)
    public ResponseEntity<?> handleImageSizeException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                messageSource.getMessage("errors.message.image_size_not_ok"));
    }

    @ExceptionHandler(ReVerifyException.class)
    public ResponseEntity<?> handleReVerifyException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                messageSource.getMessage("errors.message.re_verify_email"));
    }
}
