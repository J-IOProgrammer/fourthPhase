package ir.maktab.forthphase.service;

import ir.maktab.thirdphase.data.model.Services;
import ir.maktab.thirdphase.data.model.SubServices;
import ir.maktab.thirdphase.data.repository.SubServicesRepository;
import ir.maktab.thirdphase.exceptions.DuplicateSubServiceNameException;
import ir.maktab.thirdphase.exceptions.SubServiceNameNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SubServicesService {

    private final SubServicesRepository subServicesRepository;
    private final ServicesService servicesService;

    public void addNewSubService(SubServices subServices, String serviceName) {
        Optional<SubServices> optional = subServicesRepository.findByName(subServices.getName());
        if (optional.isPresent())
            throw new DuplicateSubServiceNameException();
        Services serviceByName = servicesService.findServiceByName(serviceName);
        serviceByName.getSubServices().add(subServices);
        subServices.setServices(serviceByName);
        subServicesRepository.save(subServices);
    }

    public SubServices findSubServiceByName(String subServiceName) {
        return subServicesRepository.findByName(subServiceName).
                orElseThrow(SubServiceNameNotFoundException::new);
    }

    public void editSubService(SubServices subServices) {
        subServicesRepository.save(subServices);
    }

    public void editSubServicePrice(String subServiceName, double newPrice) {
        SubServices subServices = subServicesRepository.findByName(subServiceName).
                orElseThrow(SubServiceNameNotFoundException::new);
        subServices.setBasePrice(newPrice);
        editSubService(subServices);
    }

    public void editSubServicesDescription(String subServiceName, String newDescription) {
        SubServices subServices = subServicesRepository.findByName(subServiceName).
                orElseThrow(SubServiceNameNotFoundException::new);
        subServices.setDescription(newDescription);
        editSubService(subServices);
    }

    public Long getSubServiceIdByName(String subServiceName) {
        SubServices subService = subServicesRepository.findByName(subServiceName).
                orElseThrow(SubServiceNameNotFoundException::new);
        return subServicesRepository.getSubServiceIdByName(subService.getName());
    }
}
