package ir.maktab.forthphase.service;

import ir.maktab.thirdphase.data.dto.AdminLoginDto;
import ir.maktab.thirdphase.data.dto.searchrequest.ExpertSearchRequest;
import ir.maktab.thirdphase.data.dto.searchrequest.UserSearchRequest;
import ir.maktab.thirdphase.data.model.*;
import ir.maktab.thirdphase.data.repository.AdminRepository;
import ir.maktab.thirdphase.exceptions.InvalidPasswordException;
import ir.maktab.thirdphase.exceptions.NoSuchUserFound;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import static ir.maktab.thirdphase.data.model.enums.ExpertStatus.ACCEPTED;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final ModelMapper modelMapper;
    private final ExpertService expertService;
    private final ServicesService servicesService;
    private final SubServicesService subServicesService;
    private final AdminRepository adminRepository;
    private final CustomerService customerService;

    public AdminLoginDto login(String username, String password) {
        Admin byUsername = adminRepository.findByUsername(username).orElseThrow(
                NoSuchUserFound::new);
        if (!byUsername.getPassword().equals(password))
            throw new InvalidPasswordException();
        return modelMapper.map(byUsername, AdminLoginDto.class);
    }

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

    public List<Customer> showListOfCustomersByApplyFilter(UserSearchRequest request) {
        return customerService.applyFilter(request);
    }
}
