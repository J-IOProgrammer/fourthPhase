package ir.maktab.forthphase.service;

import ir.maktab.forthphase.config.MessageSourceConfiguration;
import ir.maktab.forthphase.data.dto.ExpertLoginDto;
import ir.maktab.forthphase.data.dto.ExpertSaveRequestDto;
import ir.maktab.forthphase.data.dto.searchrequest.ExpertSearchRequest;
import ir.maktab.forthphase.data.model.Expert;
import ir.maktab.forthphase.data.model.Order;
import ir.maktab.forthphase.data.model.Proposal;
import ir.maktab.forthphase.data.model.SubServices;
import ir.maktab.forthphase.data.model.enums.ExpertStatus;
import ir.maktab.forthphase.data.repository.ExpertRepository;
import ir.maktab.forthphase.exceptions.*;
import ir.maktab.forthphase.util.ExpertUtil;
import ir.maktab.forthphase.util.SubServicesUtil;
import ir.maktab.forthphase.validation.EmailValidation;
import ir.maktab.forthphase.validation.NationalCodeValidation;
import ir.maktab.forthphase.validation.PasswordValidation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static ir.maktab.forthphase.data.model.enums.ExpertStatus.NEW;
import static ir.maktab.forthphase.data.model.enums.OrderStatus.WAIT_FOR_CHOOSING_EXPORT;
import static ir.maktab.forthphase.data.model.enums.OrderStatus.WAIT_FOR_EXPORT_PROPOSAL;
import static ir.maktab.forthphase.util.ProposalUtil.isOkPriceOfProposal;
import static ir.maktab.forthphase.util.ProposalUtil.isOkTimeOfProposal;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpertService {

    private final ModelMapper modelMapper;
    private final SubServicesService subServicesService;
    private final ExpertRepository expertRepository;
    private final SubServicesUtil subServicesUtil;
    private final OrderService orderService;
    private final ProposalService proposalService;
    private final OpinionService opinionService;
    final MessageSourceConfiguration messageSource;

    public void register(Expert expert) {
        if (EmailValidation.isValidateEmail(expert.getEmail()))
            throw new InvalidEmailException();
        if (!PasswordValidation.isValidatePassword(expert.getPassword()))
            throw new InvalidPasswordException();
        if (NationalCodeValidation.isValidNationalCode(expert.getNationalCode()))
            throw new InvalidNationalCodeException();
        if (!ExpertUtil.checkImageFormat(
                ExpertUtil.toBufferedImage(expert.getImage())))
            throw new InvalidImageFormatException();
        Optional<Expert> byEmail = expertRepository.findByEmail(expert.getEmail());
        if (byEmail.isPresent())
            throw new DuplicateEmailException();

        expert.setExpertStatus(NEW);
        expertRepository.save(expert);
    }

    public ExpertLoginDto login(String email, String password) {
        if (EmailValidation.isValidateEmail(email))
            throw new InvalidEmailException();
        Optional<Expert> expert = expertRepository.findByEmail(email);
        if (expert.isEmpty())
            throw new NoSuchUserFound();
        if (!expert.get().getPassword().equals(password))
            throw new InvalidPasswordException();
        if (!expert.get().isActive() || expert.get().getExpertStatus().equals(NEW))
            throw new DeActiveAccountException();
        else
            return modelMapper.map(expert, ExpertLoginDto.class);
    }

    public void deleteExpertFromSubService(String expertEmail, String subServiceName) {
        SubServices subServices;
        Expert expert = expertRepository.findByEmail(expertEmail).
                orElseThrow(NoSuchUserFound::new);
        subServices = subServicesService.findSubServiceByName(subServiceName);
        expert.getSubServices().remove(subServices);
        subServices.getExpertList().remove(expert);
        subServicesService.editSubService(subServices);
        expertRepository.save(expert);
    }

    public void addExpertToSubService(String expertEmail, String subServiceName) {
        SubServices subServices;
        Expert expert = expertRepository.findByEmail(expertEmail).
                orElseThrow(NoSuchUserFound::new);
        subServices = subServicesService.findSubServiceByName(subServiceName);
        if (subServicesUtil.isThereSubServiceName(expert.getSubServices(), subServiceName))
            throw new DuplicateSubServiceNameException();
        expert.getSubServices().add(subServices);
        subServices.getExpertList().add(expert);
        subServicesService.editSubService(subServices);
        expertRepository.save(expert);
    }

    public void changeExpertStatus(String expertEmail, ExpertStatus newStatus) {
        Expert expert = expertRepository.findByEmail(expertEmail).
                orElseThrow(NoSuchUserFound::new);
        if (expert.getExpertStatus().equals(newStatus))
            throw new ExpertStatusSetAgainException();
        expert.setExpertStatus(newStatus);
        expert.setActive(true);
        expertRepository.save(expert);
    }

    public void saveExpertImageToFile(String expertEmail) {
        Expert expert = expertRepository.findByEmail(expertEmail).orElseThrow(NoSuchUserFound::new);
        BufferedImage bufferedImage = ExpertUtil.toBufferedImage(expert.getImage());
        ExpertUtil.saveImage(bufferedImage);
    }

    public Set<Order> showOrdersRelatedToSubService(String expertEmail, String subServiceName) {
        Expert expert = expertRepository.findByEmail(expertEmail).orElseThrow(NoSuchUserFound::new);
        SubServices subServiceByName = subServicesService.findSubServiceByName(subServiceName);
        if (expert.getSubServices().contains(subServiceByName))
            return orderService.findOrdersBySubServiceName(subServiceName);
        throw new InvalidRequestForDoNotExistSubServiceException();
    }

    public void addNewProposalForOrder(String expertEmail, Proposal proposal, String orderCode) {

        Expert expert = expertRepository.findByEmail(expertEmail).orElseThrow(NoSuchUserFound::new);

        Order orderByCode = orderService.findOrderByCode(orderCode);

        SubServices subServiceByName = subServicesService.findSubServiceByName(orderByCode.getSubServiceName());

        if (expert.getSubServices().contains(subServiceByName)) {
            proposal.setOrderCode(orderByCode.getOrderCode());
            orderByCode.getProposals().add(proposal);

            if (!isOkPriceOfProposal(proposal.getCost(), orderByCode.getCost()))
                throw new InvalidPriceException();
            if (isOkTimeOfProposal(orderByCode.getRequiredDate(), proposal.getTimeOfDoing()))
                throw new InvalidRequiredDateException();

            if (orderByCode.getProposals().size() == 1)
                orderByCode.setOrderStatus(WAIT_FOR_CHOOSING_EXPORT);

            proposal.setExpertRate(expert.getRating());
            proposal.setExpertEmail(expertEmail);
            proposal.setOrder(orderByCode);
            proposalService.saveEditedProposal(proposal);
        } else
            throw new SendProposalOnInvalidSubServiceException();
    }

    public Set<Order> showRelatedOrders(String subServiceName) {
        Set<Order> orders = orderService
                .findOrderByStatusAndSubServiceName(WAIT_FOR_EXPORT_PROPOSAL, subServiceName);
        orders.addAll(orderService
                .findOrderByStatusAndSubServiceName(WAIT_FOR_CHOOSING_EXPORT, subServiceName));
        return orders;
    }

    public List<Expert> showListOfNewExperts() {
        return expertRepository.findExpertsByExpertStatus(NEW);
    }

    public void getCostOfService(String expertEmail, double cost) {
        Expert expert = expertRepository.findByEmail(expertEmail).orElseThrow(NoSuchUserFound::new);
        expert.setCredit(expert.getCredit() + (cost * 0.7));
        expertRepository.save(expert);
    }

    public Expert addOpinionOnAcceptedOrder(String expertEmail) {
        Expert expert = expertRepository.findByEmail(expertEmail).orElseThrow(NoSuchUserFound::new);
        expert.setRating(opinionService.calcExpertRate(expertEmail));
        expertRepository.save(expert);
        return expert;
    }

    public void changePassword(String expertEmail, String newPassword, String confirmedPassword) {
        if (!newPassword.equals(confirmedPassword))
            throw new UnequalPasswordsException();
        Expert expert = expertRepository.findByEmail(expertEmail).orElseThrow(NoSuchUserFound::new);
        expert.setPassword(newPassword);
        expertRepository.save(expert);
    }

    public List<String> getExpertOpinions(String expertEmail) {
        return opinionService.getExpertOpinions(expertEmail);
    }

    private Expert findExpertByEmail(String email) {
        return expertRepository.findByEmail(email).orElseThrow(NoSuchUserFound::new);
    }

    public List<Expert> applyFilter(ExpertSearchRequest request) {
        Specification<Expert> expertSpecification = ExpertRepository.searchFilter(request);
        List<Expert> all = expertRepository.findAll(expertSpecification);
        if (request.getStatus().equalsIgnoreCase("max") &&
                !all.contains(findMaxRating()))
            all.add(findMaxRating());
        if (request.getStatus().equalsIgnoreCase("min") &&
                !all.contains(findMinRating()))
            all.add(findMinRating());
        if (request.getSubServiceName() != null) {
            List<Expert> bySubServicesName = expertRepository
                    .findExpertsBySubServicesName(subServicesService
                            .getSubServiceIdByName(request
                                    .getSubServiceName()));
            if (!all.contains(bySubServicesName))
                all.addAll(bySubServicesName);
        }
        return all;
    }

    public void setExpertRating(String expertEmail, Date startTime, Date doneTime, String neededTime) {
        Expert expertByEmail = findExpertByEmail(expertEmail);
        int betweenTime = ExpertUtil.calculateDistanceBetweenTime(startTime, doneTime, neededTime);
        double expertRate = expertByEmail.getRating() + betweenTime;
        if (expertRate < 0)
            expertByEmail.setActive(false);
        if (expertRate > 5)
            expertByEmail.setRating(5);
        expertByEmail.setRating(expertRate);
        expertRepository.save(expertByEmail);
    }

    public Expert PrepareNewObject(ExpertSaveRequestDto requestDto){
        if (!ExpertUtil.checkImageSize(requestDto.getImage()))
            throw new ImageSizeException();
        BufferedImage image;
        byte[] jpgs;
        try {
            image = ImageIO.read(new File(requestDto.getImage()));
            jpgs = ExpertUtil.toByteArray(image, "jpg");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Expert expert = modelMapper.map(requestDto, Expert.class);
        expert.setImage(jpgs);
        return expert;
    }

    private Expert findMaxRating() {
        return expertRepository.findMaxRating().orElseThrow(NoSuchUserFound::new);
    }

    private Expert findMinRating() {
        return expertRepository.findMinRating().orElseThrow(NoSuchUserFound::new);
    }
}
