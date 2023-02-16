package ir.maktab.forthphase.controller;

import ir.maktab.thirdphase.config.MessageSourceConfiguration;
import ir.maktab.thirdphase.data.dto.CustomerLoginDto;
import ir.maktab.thirdphase.data.dto.CustomerOrderDto;
import ir.maktab.thirdphase.data.dto.OpinionDto;
import ir.maktab.thirdphase.data.dto.PayingInformation;
import ir.maktab.thirdphase.data.model.Customer;
import ir.maktab.thirdphase.data.model.Opinion;
import ir.maktab.thirdphase.data.model.Order;
import ir.maktab.thirdphase.exceptions.NoSuchProposalFoundException;
import ir.maktab.thirdphase.exceptions.NotEnoughMoneyException;
import ir.maktab.thirdphase.service.CustomerService;
import ir.maktab.thirdphase.util.OrderUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
@Validated
@Slf4j
public class CustomerController {

    private final CustomerService customerService;
    private final ModelMapper modelMapper;
    final MessageSourceConfiguration messageSource;

    public CustomerController(CustomerService customerService, ModelMapper modelMapper, MessageSourceConfiguration messageSource) {
        this.customerService = customerService;
        this.modelMapper = modelMapper;
        this.messageSource = messageSource;
    }

    @PostMapping("/new")
    public String addNewCustomer(@RequestBody CustomerLoginDto customerLoginDto) {
        log.info("... adding new customer with info: '{}' ...", customerLoginDto);
        Customer customer = modelMapper.map(customerLoginDto, Customer.class);
        customerService.register(customer);
        log.info("... successfully ...");
        return messageSource.getMessage("ok.message.successful_operation");
    }

    @PostMapping("/add_order")
    public String addNewOrder(@RequestParam(name = "email") String customerEmail,
                              @RequestParam(name = "requiredDate") String requiredDate,
                              @RequestBody CustomerOrderDto orderDto) {
        log.info("... adding new order with info: '{}' by user: {} ...", orderDto, customerEmail);
        orderDto.setRequiredDate(OrderUtil.convertStringToDate(requiredDate));
        Order order = modelMapper.map(orderDto, Order.class);
        order.setOrderCode(OrderUtil.codeGenerator());
        customerService.addNewOrder(customerEmail, order);
        log.info("... successfully add order ...");
        return messageSource.getMessage("ok.message.successful_operation");
    }

    @GetMapping("/proposals_ordered_by_cost/{orderCode}")
    public String getProposalsOrderedByCost(@PathVariable String orderCode) {
        log.info("... get request for proposals of order with code: '{}' ...", orderCode);
        return customerService.showAllOfProposalsOrderedByProposalCost(orderCode).toString();
    }

    @GetMapping("/proposals_ordered_by_expert_rate/{orderCode}")
    public String getProposalsOrderedByExpertRate(@PathVariable String orderCode) {
        return customerService.showAllOfProposalsOrderedByExpertRate(orderCode).toString();
    }

    @PostMapping("/choose_expert")
    public String setExpertForOrder(@RequestParam("orderCode") String orderCode,
                                    @RequestParam("expertEmail") String expertEmail) {
        customerService.chooseExpertForOrder(orderCode, expertEmail);
        return messageSource.getMessage("ok.message.successful_operation");
    }

    @PostMapping("/done/{orderCode}")
    public String setDoneOrder(@PathVariable String orderCode) {
        log.info("... start set done order with code : {} ...", orderCode);
        customerService.setDoneStatusForOrder(orderCode);
        log.info("... order status set done successfully ...");
        return messageSource.getMessage("ok.message.successful_operation");
    }

    @PostMapping("/change_password")
    public String changePassword(@RequestParam String emailCustomer,
                                 @RequestParam String password,
                                 @RequestParam String confirmedPassword) {
        customerService.changePassword(emailCustomer, password, confirmedPassword);
        return messageSource.getMessage("ok.message.successful_operation");
    }

    @GetMapping("/all_orders/{customerEmail}")
    public String showAllOrders(@PathVariable String customerEmail) {
        return customerService.showAllOrders(customerEmail).toString();
    }

    @PostMapping("/add_opinion")
    public void addOpinionForOrder(@RequestBody OpinionDto opinionDto,
                                   @RequestParam(name = "orderCode") String orderCode) {
        Opinion opinion = modelMapper.map(opinionDto, Opinion.class);
        customerService.addOpinionForOrder(opinion, orderCode);
        log.info("... successfully ...");
    }

    @PostMapping("/pay")
    public String cardPayment(@Valid @ModelAttribute PayingInformation payingInformation) {
        customerService.payingServiceFee(payingInformation.getOrderCode());
        log.info("... successfully ...");
        return messageSource.getMessage("ok.message.successful_operation");
    }

    @PostMapping("/pay_from_credit/{orderCode}")
    public String payWithCredit(@PathVariable String orderCode) {
        customerService.payingServiceFee(orderCode);
        return messageSource.getMessage("ok.message.successful_operation");
    }

    @ExceptionHandler(NotEnoughMoneyException.class)
    public ResponseEntity<?> handleNotEnoughMoneyException() {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(messageSource
                        .getMessage("errors.message.not_enough_money"));
    }

    @ExceptionHandler(NoSuchProposalFoundException.class)
    public ResponseEntity<?> handleNoSuchProposalFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                messageSource.getMessage("errors.message.no_such_proposal_found"));
    }
}
