package ir.maktab.forthphase.service;

import ir.maktab.forthphase.data.dto.searchrequest.CustomerSearchRequest;
import ir.maktab.forthphase.data.dto.searchrequest.ExpertSearchRequest;
import ir.maktab.forthphase.data.dto.searchrequest.OrderSearchRequest;
import ir.maktab.forthphase.data.model.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static ir.maktab.forthphase.data.model.enums.ExpertStatus.ACCEPTED;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final ExpertService expertService;
    private final ServicesService servicesService;
    private final SubServicesService subServicesService;
    private final CustomerService customerService;
    private final OrderService orderService;

    public void addNewService(Services service) {
        servicesService.addNewService(service);
    }

    public void addNewSubService(SubServices subService, String serviceName) {
        subServicesService.addNewSubService(subService, serviceName);
    }

    public void deleteExpertFromSubService(String expertEmail, String subServiceName) {
        expertService.deleteExpertFromSubService(expertEmail, subServiceName);
    }

    public void addExpertToSubService(String expertEmail, String subServiceName) {
        expertService.addExpertToSubService(expertEmail, subServiceName);
    }

    public List<Services> showAllServices() {
        return servicesService.showAllServices();
    }

    public void editSubServicePrice(String subServicesName, double newCost) {
        subServicesService.editSubServicePrice(subServicesName, newCost);
    }

    public void editSubServicesDescription(String subServicesName, String newDescription) {
        subServicesService.editSubServicesDescription(subServicesName, newDescription);
    }

    public void acceptExpert(String expertEmail) {
        expertService.changeExpertStatus(expertEmail, ACCEPTED);
    }

    public List<Expert> showListOfNewExperts() {
        return expertService.showListOfNewExperts();
    }

    public List<Expert> showListOfExpertsByApplyFilter(ExpertSearchRequest request) {
        return expertService.applyFilter(request);
    }

    public List<Customer> showListOfCustomersByApplyFilter(CustomerSearchRequest request) {
        return customerService.applyFilter(request);
    }

    public List<Order> showListOfOrdersByApplyFilter(OrderSearchRequest request) {
        return orderService.applyFilter(request);
    }

    public int countCustomerOrders(String customerEmail) {
        return orderService.showCustomerOrders(customerEmail).size();
    }

    public int countDoneOrdersByExpert(String expertEmail) {
        return orderService.showDoneOrdersByExpert(expertEmail).size();
    }
}
