package ir.maktab.forthphase.service;

import ir.maktab.forthphase.data.dto.CustomerLoginDto;
import ir.maktab.forthphase.data.dto.OpinionDto;
import ir.maktab.forthphase.data.dto.searchrequest.CustomerSearchRequest;
import ir.maktab.forthphase.data.model.*;
import ir.maktab.forthphase.data.model.enums.OrderStatus;
import ir.maktab.forthphase.data.model.enums.Role;
import ir.maktab.forthphase.data.repository.CustomerRepository;
import ir.maktab.forthphase.exceptions.*;
import ir.maktab.forthphase.util.OrderUtil;
import ir.maktab.forthphase.validation.EmailValidation;
import ir.maktab.forthphase.validation.NationalCodeValidation;
import ir.maktab.forthphase.validation.PasswordValidation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

import static ir.maktab.forthphase.data.model.enums.OrderStatus.*;
import static ir.maktab.forthphase.util.ProposalUtil.findProposalByExpertEmail;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final SubServicesService subServicesService;
    private final OrderService orderService;
    private final ExpertService expertService;
    private final ProposalService proposalService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public void register(Customer customer) {
        if (NationalCodeValidation.isValidNationalCode(customer.getNationalCode()))
            throw new InvalidNationalCodeException();
        Optional<Customer> byEmail = customerRepository.findByEmail(customer.getEmail());
        if (byEmail.isPresent())
            throw new DuplicateEmailException();
        customer.setRole(Role.ROLE_CUSTOMER);
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customerRepository.save(customer);
    }

    public double findIdByEmail(String customerEmail) {
        return customerRepository.findCustomerIdByEmail(customerEmail);
    }

    public CustomerLoginDto login(String email, String password) {
        if (EmailValidation.isValidateEmail(email))
            throw new InvalidEmailException();
        Customer customer = customerRepository.findByEmail(email).orElseThrow(NoSuchUserFound::new);
        if (customer.getPassword().equals(password))
            return modelMapper.map(customer, CustomerLoginDto.class);
        else
            throw new InvalidPasswordException();
    }

    public void addNewOrder(String customerEmail, Order order) {

        Customer customer = customerRepository.findByEmail(customerEmail).orElseThrow(NoSuchUserFound::new);
        SubServices subServiceByName = subServicesService.findSubServiceByName(order.getSubServiceName());
        if (!subServiceByName.getServices().getName().equals(order.getServiceName()))
            throw new ServiceNameNotFoundException();
        if (OrderUtil.isOkPriceOfOrder(order.getCost(), subServiceByName.getBasePrice()))
            throw new InvalidPriceException();
        if (OrderUtil.isOkTimeOfOrder(order.getRequiredDate(), new Date()))
            throw new InvalidRequiredDateException();
        order.setOrderStatus(WAIT_FOR_EXPERT_PROPOSAL);
        order.setCustomer(customer);
        order.setOpinion(null);
        orderService.saveOrder(order);
    }

    public Set<Proposal> showAllOfProposalsOrderedByProposalCost(String orderCode) {
        return proposalService.showProposalsOrderedByCost(orderCode);
    }

    public Set<Proposal> showAllOfProposalsOrderedByExpertRate(String orderCode) {
        return proposalService.showProposalsOrderedByExpertRate(orderCode);
    }

    public void chooseExpertForOrder(String orderCode, String expertEmail) {

        Set<Proposal> proposals = proposalService.getProposalsByOrderCode(orderCode);

        Proposal byExpertEmail = findProposalByExpertEmail(proposals, expertEmail);
        if (Objects.isNull(byExpertEmail))
            throw new NoSuchUserFound();

        Order orderByCode = orderService.findOrderByCode(orderCode);
        if (!orderByCode.getOrderStatus().equals(WAIT_FOR_CHOOSING_EXPERT))
            throw new InvalidOrderStatusException();
        orderByCode.setAcceptedExpertEmail(expertEmail);
        orderByCode.setCost(byExpertEmail.getCost());
        orderByCode.setOrderStatus(WAIT_FOR_COMING_EXPERT);
        orderService.saveOrder(orderByCode);
    }

    public void setStartedStatusForOrder(String orderCode) {
        Order orderByCode = orderService.findOrderByCode(orderCode);
        Proposal proposal = proposalService.
                findProposalByOrderCodeAndExpertEmail(orderCode, orderByCode.getAcceptedExpertEmail());
        if (proposal.getTimeOfDoing().compareTo(new Date()) < 0)
            throw new InvalidRequiredDateException();
        if (!orderByCode.getOrderStatus().equals(WAIT_FOR_COMING_EXPERT))
            throw new InvalidOrderStatusException();
        orderByCode.setOrderStatus(START);
        proposal.setTimeOfStart(new Date());
        proposalService.saveEditedProposal(proposal);
        orderService.saveOrder(orderByCode);
    }

    public void setDoneStatusForOrder(String orderCode) {
        Order orderByCode = orderService.findOrderByCode(orderCode);
        Proposal proposal = proposalService.
                findProposalByOrderCodeAndExpertEmail(orderCode, orderByCode.getAcceptedExpertEmail());
        if (!orderByCode.getOrderStatus().equals(START))
            throw new InvalidOrderStatusException();

        orderByCode.setOrderStatus(DONE);
        orderService.saveOrder(orderByCode);

        expertService.setExpertRating(orderByCode.getAcceptedExpertEmail(),
                proposal.getTimeOfStart(),
                new Date(),
                proposal.getNeededTime());
    }

    public void changePassword(String emailCustomer, String newPassword, String confirmedPassword) {
        if (!newPassword.equals(confirmedPassword))
            throw new UnequalPasswordsException();
        if (!PasswordValidation.isValidatePassword(newPassword))
            throw new InvalidPasswordException();
        Customer customer = customerRepository.findByEmail(emailCustomer).orElseThrow(NoSuchUserFound::new);
        customer.setPassword(passwordEncoder.encode(newPassword));
        customerRepository.save(customer);
    }

    public Set<Order> showAllOrders(String emailCustomer) {
        Customer customer = customerRepository.findByEmail(emailCustomer).orElseThrow(NoSuchUserFound::new);
        return customer.getOrders();
    }

    public List<Order> showCustomerOrderByStatus(String customerEmail, String status) {
        OrderStatus orderStatus = valueOf(status);
        return orderService.showCustomerOrdersByOrderStatus(customerEmail, orderStatus);
    }

    public void payingServiceFee(String orderCode) {
        Order order = orderService.findOrderByCode(orderCode);
        Customer customer = customerRepository
                .findByEmail(order
                        .getCustomer()
                        .getEmail())
                .orElseThrow(NoSuchUserFound::new);
        if (customer.getCredit() < order.getCost())
            throw new NotEnoughMoneyException();
        if (order.getOrderStatus().equals(PAID))
            throw new InvalidOrderStatusException();
        customer.setCredit(customer.getCredit() - order.getCost());
        order.setOrderStatus(PAID);
        orderService.saveOrder(order);
        expertService.getCostOfService(order.getAcceptedExpertEmail(), order.getCost());
        customerRepository.save(customer);
    }

    public void addOpinionForOrder(OpinionDto opinionDto, String orderCode) {
        Order orderByCode = orderService.findOrderByCode(orderCode);
        if (Objects.nonNull(orderByCode.getOpinion()))
            throw new DuplicateOpinionAddingException();
        Opinion opinion = new Opinion(Double.parseDouble(opinionDto.getRate()),
                opinionDto.getOpinionText());
        orderByCode.setOpinion(opinion);
        Expert expert = expertService.addOpinionOnAcceptedOrder(orderByCode.getAcceptedExpertEmail());
        opinion.setExpert(expert);
        orderService.saveOrder(orderByCode);
    }

    public List<Customer> applyFilter(CustomerSearchRequest request) {
        Specification<Customer> searchFilter = CustomerRepository.searchFilter(request);
        List<Customer> all = customerRepository.findAll(searchFilter);
        if (checkRequestFields(request))
            return customerRepository.findAll();
        applyRegisterDate(request, all);
        return all;
    }

    public double getCredit(String customerEmail) {
        Customer customer = customerRepository.findByEmail(customerEmail).orElseThrow(NoSuchUserFound::new);
        return customer.getCredit();
    }

    public List<Order> showHistoryOfOrder(String customerEmail) {
        return orderService.showCustomerOrders(customerEmail);
    }

    private boolean checkRequestFields(CustomerSearchRequest request) {
        return request.getLastName() == null && request.getFirstName() == null
                && request.getEmail() == null && request.getNationalCode() == null
                && request.getRegisterDate() == null && request.getSendOrdersCount() == 0;
    }

    private void applyRegisterDate(CustomerSearchRequest request, List<Customer> all) {
        if (request.getRegisterDate() != null) {
            List<Customer> customers = customerRepository.findCustomersByRegisterDate(request.getRegisterDate());
            for (Customer customer : customers)
                if (!all.contains(customer))
                    all.add(customer);
        }
    }
}
