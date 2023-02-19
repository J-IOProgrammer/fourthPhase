package ir.maktab.forthphase.controller;

import ir.maktab.forthphase.config.MessageSourceConfiguration;
import ir.maktab.forthphase.data.dto.ServiceDto;
import ir.maktab.forthphase.data.dto.SubServicesDto;
import ir.maktab.forthphase.data.dto.searchrequest.ExpertSearchRequest;
import ir.maktab.forthphase.data.dto.searchrequest.UserSearchRequest;
import ir.maktab.forthphase.data.model.Services;
import ir.maktab.forthphase.data.model.SubServices;
import ir.maktab.forthphase.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    final MessageSourceConfiguration messageSource;
    private final ModelMapper modelMapper;

    public AdminController(AdminService adminService,
                           MessageSourceConfiguration messageSource,
                           ModelMapper modelMapper) {
        this.adminService = adminService;
        this.messageSource = messageSource;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/new_service")
    public void addNewService(@RequestBody ServiceDto serviceDto) {
        Services service = modelMapper.map(serviceDto, Services.class);
        adminService.addNewService(service);
    }

    @PostMapping("/new_sub_service")
    public void addNewSubService(@RequestBody SubServicesDto subServicesDto,
                                 @RequestParam("serviceName") String serviceName) {
        SubServices subService = modelMapper.map(subServicesDto, SubServices.class);
        adminService.addNewSubService(subService, serviceName);
    }

    @DeleteMapping("/delete_expert_from_sub_service")
    public void deleteExpertFromSubService(@RequestParam("expertEmail") String expertEmail,
                                           @RequestParam("subServiceName") String subServiceName) {
        adminService.deleteExpertFromSubService(expertEmail, subServiceName);
    }

    @PostMapping("/add_expert_to_sub_service")
    public void addExpertToSubService(@RequestParam("expertEmail") String expertEmail,
                                      @RequestParam("subServiceName") String subServiceName) {
        adminService.addExpertToSubService(expertEmail, subServiceName);
    }

    @GetMapping("/all_services")
    public String showAllServices() {
        return adminService.showAllServices().toString();
    }

    @PutMapping("/edit_price")
    public void editSubServicePrice(@RequestParam("subServicesName") String subServicesName,
                                    @RequestParam("newCost") double newCost) {
        adminService.editSubServicePrice(subServicesName, newCost);
    }

    @PutMapping("/edit_description")
    public void editSubServicesDescription(@RequestParam("subServicesName") String subServicesName,
                                           @RequestParam("newDescription") String newDescription) {
        adminService.editSubServicesDescription(subServicesName, newDescription);
    }

    @PostMapping("/accept_expert/{expertEmail}")
    public void acceptExpert(@PathVariable String expertEmail) {
        adminService.acceptExpert(expertEmail);
    }

    @GetMapping("/all_new_experts")
    public String showListOfNewExperts() {
        return adminService.showListOfNewExperts().toString();
    }

    @GetMapping("/search_customer")
    public String getCustomersByFilter(@RequestParam String firstName,
                                       @RequestParam String lastName,
                                       @RequestParam String email) {
        UserSearchRequest userSearchRequest = new UserSearchRequest(firstName, lastName, email);
        return adminService.showListOfCustomersByApplyFilter(userSearchRequest).toString();
    }

    @GetMapping("/search_expert")
    public String getExpertsByFilter(@RequestParam String firstName,
                                     @RequestParam String lastName,
                                     @RequestParam String email,
                                     @RequestParam String status,
                                     @RequestParam String subServiceName) {
        ExpertSearchRequest expertSearchRequest =
                new ExpertSearchRequest(firstName, lastName, email, status, subServiceName);
        return adminService.showListOfExpertsByApplyFilter(expertSearchRequest).toString();
    }
}
