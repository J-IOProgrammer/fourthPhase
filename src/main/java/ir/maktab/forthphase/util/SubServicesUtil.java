package ir.maktab.forthphase.util;

import ir.maktab.forthphase.data.model.SubServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@RequiredArgsConstructor
@Component
public class SubServicesUtil {

    public boolean isThereSubServiceName(Set<SubServices> subServicesList, String subServiceName) {
        for (SubServices services : subServicesList)
            if (services.getName().equals(subServiceName))
                return true;
        return false;
    }

}
