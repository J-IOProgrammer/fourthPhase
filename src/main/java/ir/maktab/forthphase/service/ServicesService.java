package ir.maktab.forthphase.service;

import ir.maktab.thirdphase.data.model.Services;
import ir.maktab.thirdphase.data.repository.ServiceRepository;
import ir.maktab.thirdphase.exceptions.DuplicateServiceNameException;
import ir.maktab.thirdphase.exceptions.ServiceNameNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ServicesService {

    private final ServiceRepository serviceRepository;

    public Services findServiceByName(String serviceName) {
        return serviceRepository.findByName(serviceName).
                orElseThrow(ServiceNameNotFoundException::new);
    }

    public void addNewService(Services services) {
        Optional<Services> optional = serviceRepository.findByName(services.getName());
        if (optional.isPresent())
            throw new DuplicateServiceNameException();
        serviceRepository.save(services);
    }

    public List<Services> showAllServices() {
        return serviceRepository.findAll();
    }
}
