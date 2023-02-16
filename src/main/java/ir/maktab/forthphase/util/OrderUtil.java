package ir.maktab.forthphase.util;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class OrderUtil {

    public static String codeGenerator() {
        return RandomStringUtils.randomAlphanumeric(12);
    }

    public static boolean isOkPriceOfOrder(double customerPrice, double subServiceBaseCost) {
        return !(customerPrice >= subServiceBaseCost);
    }

    public static boolean isOkTimeOfOrder(Date requiredDate, Date submitDate) {
        return requiredDate.compareTo(submitDate) <= 0 && requiredDate.compareTo(submitDate) != 0;
    }

    public static Date convertStringToDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
