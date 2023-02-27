package ir.maktab.forthphase.controller;

import ir.maktab.forthphase.config.MessageSourceConfiguration;
import ir.maktab.forthphase.config.SecurityUtil;
import ir.maktab.forthphase.data.dto.CustomerLoginDto;
import ir.maktab.forthphase.data.dto.CustomerOrderDto;
import ir.maktab.forthphase.data.dto.OpinionDto;
import ir.maktab.forthphase.data.dto.PayingInformation;
import ir.maktab.forthphase.data.model.Customer;
import ir.maktab.forthphase.data.model.Order;
import ir.maktab.forthphase.exceptions.DuplicateOpinionAddingException;
import ir.maktab.forthphase.exceptions.NoSuchProposalFoundException;
import ir.maktab.forthphase.exceptions.NotEnoughMoneyException;
import ir.maktab.forthphase.service.CustomerService;
import ir.maktab.forthphase.util.OrderUtil;
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

    public CustomerController(CustomerService customerService, ModelMapper modelMapper,
                              MessageSourceConfiguration messageSource) {
        this.customerService = customerService;
        this.modelMapper = modelMapper;
        this.messageSource = messageSource;
    }

    @PostMapping("/new")
    public String addNewCustomer(@Valid @RequestBody CustomerLoginDto customerLoginDto) {
        log.info("... adding new customer with info: '{}' ...", customerLoginDto);
        Customer customer = modelMapper.map(customerLoginDto, Customer.class);
        customerService.register(customer);
        log.info("... successfully ...");
        return messageSource.getMessage("ok.message.successful_operation");
    }

    @PostMapping("/add_order")
    public String addNewOrder(@RequestParam(name = "requiredDate") String requiredDate,
                              @RequestBody CustomerOrderDto orderDto) {
        Customer customer = (Customer) SecurityUtil.getCurrentUser();
        log.info("... adding new order with info: '{}' by user: {} ...", orderDto, customer.getEmail());
        orderDto.setRequiredDate(OrderUtil.convertStringToDate(requiredDate));
        Order order = modelMapper.map(orderDto, Order.class);
        order.setOrderCode(OrderUtil.codeGenerator());
        customerService.addNewOrder(customer.getEmail(), order);
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

    @PutMapping("/done/{orderCode}")
    public String setDoneOrder(@PathVariable String orderCode) {
        log.info("... set done order with code : {} ...", orderCode);
        customerService.setDoneStatusForOrder(orderCode);
        log.info("... order status set done successfully ...");
        return messageSource.getMessage("ok.message.successful_operation");
    }

    @PutMapping("/start/{orderCode}")
    public String setStartOrder(@PathVariable String orderCode) {
        log.info("... set start order with code : {} ...", orderCode);
        customerService.setStartedStatusForOrder(orderCode);
        log.info("... order status set start successfully ...");
        return messageSource.getMessage("ok.message.successful_operation");
    }

    @PostMapping("/change_password")
    public String changePassword(@RequestParam String emailCustomer,
                                 @RequestParam String password,
                                 @RequestParam String confirmedPassword) {
        customerService.changePassword(emailCustomer, password, confirmedPassword);
        return messageSource.getMessage("ok.message.successful_operation");
    }

    @GetMapping("/all_orders")
    public String showAllOrders() {
        Customer customer = (Customer) SecurityUtil.getCurrentUser();
        return customerService.showAllOrders(customer.getEmail()).toString();
    }

    @PostMapping("/add_opinion")
    public void addOpinionForOrder(@Valid @RequestBody OpinionDto opinionDto,
                                   @RequestParam(name = "orderCode") String orderCode) {
        customerService.addOpinionForOrder(opinionDto, orderCode);
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

    @GetMapping("/show_credit")
    public double showCredit() {
        Customer customer = (Customer) SecurityUtil.getCurrentUser();
        log.info("... current user : {} ...", customer.toString());
        return customerService.getCredit(customer.getEmail());
    }

    @GetMapping("/order_history")
    public String showOrderHistory() {
        Customer customer = (Customer) SecurityUtil.getCurrentUser();
        return customerService.showHistoryOfOrder(customer.getEmail()).toString();
    }
}
