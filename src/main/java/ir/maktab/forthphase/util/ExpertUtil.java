package ir.maktab.forthphase.util;

import ir.maktab.forthphase.data.dto.ExpertLoginDto;
import ir.maktab.forthphase.data.model.Expert;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Component
public class ExpertUtil {

    static ModelMapper modelMapper;

    public static boolean checkImageFormat(BufferedImage bi) {
        return bi.getType() == 5;
    }

    public static byte[] toByteArray(BufferedImage bi, String format)
            throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bi, format, outputStream);
        return outputStream.toByteArray();

    }

    public static BufferedImage toBufferedImage(byte[] bytes) {
        try {
            return ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveImage(BufferedImage bufferedImage) {
        File file = new File("src/images/ExpertImage.jpg");
        try {
            ImageIO.write(bufferedImage, "jpg", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean checkImageSize(String fileName) {
        //fileName = "src/face.jpg";
        Path filePath = Paths.get(fileName);
        FileChannel fileChannel;
        try {
            fileChannel = FileChannel.open(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        long fileSize;
        try {
            fileSize = fileChannel.size();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        long sizeInKB = fileSize / 1024;
        return sizeInKB <= 300;
    }

    public static List<ExpertLoginDto> convertExpertToExpertLoginDto(List<Expert> experts) {
        List<ExpertLoginDto> dtoArrayList = new ArrayList<>();
        for (Expert expert : experts) {
            dtoArrayList.add(modelMapper.map(expert, ExpertLoginDto.class));
        }
        return dtoArrayList;
    }

    public static int calculateDistanceBetweenTime(Date firstTime, Date secondTime, String timeRange) {
        Pattern pattern = Pattern.compile("^*([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]*");
        int startTime = 0;
        int doneTime = 0;
        Matcher firstMatcher = pattern.matcher(firstTime.toString());
        Matcher secondMatcher = pattern.matcher(secondTime.toString());
        while (firstMatcher.find()) {
            startTime = Integer.parseInt(firstMatcher.group(1));
        }
        while (secondMatcher.find()) {
            doneTime = Integer.parseInt(secondMatcher.group(1));
        }
        int workHours = startTime + Integer.parseInt(timeRange);
        if (doneTime == 0)
            doneTime = 24;
        return workHours - doneTime;
    }
}
