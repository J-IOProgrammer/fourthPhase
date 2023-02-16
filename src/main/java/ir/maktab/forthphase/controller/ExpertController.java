package ir.maktab.forthphase.controller;

import ir.maktab.thirdphase.config.MessageSourceConfiguration;
import ir.maktab.thirdphase.data.dto.ExpertSaveRequestDto;
import ir.maktab.thirdphase.data.dto.ProposalDto;
import ir.maktab.thirdphase.data.model.Expert;
import ir.maktab.thirdphase.data.model.Proposal;
import ir.maktab.thirdphase.exceptions.InvalidRequestForDoNotExistSubServiceException;
import ir.maktab.thirdphase.exceptions.SendProposalOnInvalidSubServiceException;
import ir.maktab.thirdphase.service.ExpertService;
import ir.maktab.thirdphase.util.ExpertUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@RestController
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
    public String addNewExpert(@RequestBody ExpertSaveRequestDto saveRequestDto) {
        log.info("... adding new customer with info: '{}' ...", saveRequestDto);
        if (!ExpertUtil.checkImageSize(saveRequestDto.getImage()))
            return messageSource.getMessage("errors.message.image_size_not_ok");
        BufferedImage image;
        byte[] jpgs;
        try {
            image = ImageIO.read(new File(saveRequestDto.getImage()));
            jpgs = ExpertUtil.toByteArray(image, "jpg");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Expert expert = modelMapper.map(saveRequestDto, Expert.class);
        expert.setImage(jpgs);
        expertService.register(expert);
        return messageSource.getMessage("ok.message.successful_operation");
    }

    @GetMapping("/orders_by_sub_service")
    public String showOrdersRelatedToSubService(@RequestParam(name = "email") String expertEmail,
                                                @RequestParam(name = "subServiceName") String subServiceName) {
        return expertService.showOrdersRelatedToSubService(expertEmail, subServiceName).toString();
    }

    @PostMapping("/add_new_proposal")
    public void addNewProposalForOrder(@RequestParam("email") String expertEmail,
                                       @RequestBody ProposalDto proposalDto,
                                       @RequestParam("orderCode") String orderCode) {
        Proposal proposal = modelMapper.map(proposalDto, Proposal.class);
        expertService.addNewProposalForOrder(expertEmail, proposal, orderCode);
    }

    @GetMapping("/related_orders/{name}")
    public String showRelatedOrders(@PathVariable("name") String subServiceName) {
        return expertService.showRelatedOrders(subServiceName).toString();
    }

    @PostMapping("/change_password")
    public void changePassword(@RequestParam String expertEmail,
                               @RequestParam String password,
                               @RequestParam String confirmedPassword) {
        log.info("... change password request by : '{}' ...", expertEmail);
        expertService.changePassword(expertEmail, password, confirmedPassword);
    }

    @GetMapping("/opinions/{email}")
    public String showExpertOpinions(@PathVariable("email") String expertEmail) {
        return expertService.getExpertOpinions(expertEmail).toString();
    }

    @PostMapping("/save_image/{email}")
    public void saveExpertImage(@PathVariable("email") String expertEmail) {
        expertService.saveExpertImageToFile(expertEmail);
    }

    @ExceptionHandler(InvalidRequestForDoNotExistSubServiceException.class)
    public ResponseEntity<?> handleInvalidRequestForDoNotExistSubServiceException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                messageSource.getMessage("errors.message.invalid_request_for_do_not_exist_sub_service"));
    }

    @ExceptionHandler(SendProposalOnInvalidSubServiceException.class)
    public ResponseEntity<?> handleSendProposalOnInvalidSubServiceException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                messageSource.getMessage("errors.message.send_proposal_on_invalid_sub_service"));
    }
}
