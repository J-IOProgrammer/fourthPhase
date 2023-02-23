package ir.maktab.forthphase.service;

import ir.maktab.forthphase.config.MessageSourceConfiguration;
import ir.maktab.forthphase.data.dto.ExpertSaveRequestDto;
import ir.maktab.forthphase.data.dto.searchrequest.ExpertSearchRequest;
import ir.maktab.forthphase.data.model.Expert;
import ir.maktab.forthphase.data.model.Order;
import ir.maktab.forthphase.data.model.Proposal;
import ir.maktab.forthphase.data.model.SubServices;
import ir.maktab.forthphase.data.model.enums.ExpertStatus;
import ir.maktab.forthphase.data.model.enums.Role;
import ir.maktab.forthphase.data.repository.ExpertRepository;
import ir.maktab.forthphase.exceptions.*;
import ir.maktab.forthphase.util.ExpertUtil;
import ir.maktab.forthphase.util.SubServicesUtil;
import ir.maktab.forthphase.validation.NationalCodeValidation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static ir.maktab.forthphase.data.model.enums.ExpertStatus.WAIT_FOR_ACCEPT;
import static ir.maktab.forthphase.data.model.enums.ExpertStatus.WAIT_FOR_VERIFY_EMAIL;
import static ir.maktab.forthphase.data.model.enums.OrderStatus.WAIT_FOR_CHOOSING_EXPERT;
import static ir.maktab.forthphase.data.model.enums.OrderStatus.WAIT_FOR_EXPERT_PROPOSAL;
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
    private final BCryptPasswordEncoder passwordEncoder;
    final MessageSourceConfiguration messageSource;

    public void register(Expert expert) {
        if (NationalCodeValidation.isValidNationalCode(expert.getNationalCode()))
            throw new InvalidNationalCodeException();
        if (!ExpertUtil.checkImageFormat(
                ExpertUtil.toBufferedImage(expert.getImage())))
            throw new InvalidImageFormatException();
        Optional<Expert> byEmail = expertRepository.findByEmail(expert.getEmail());
        if (byEmail.isPresent())
            throw new DuplicateEmailException();

        expert.setExpertStatus(WAIT_FOR_VERIFY_EMAIL);
        expert.setPassword(passwordEncoder.encode(expert.getPassword()));
        expert.setRole(Role.ROLE_EXPERT);
        expertRepository.save(expert);
    }

    public void deleteExpertFromSubService(String expertEmail, String subServiceName) {
        SubServices subServices;
        Expert expert = findExpertByEmail(expertEmail);
        subServices = subServicesService.findSubServiceByName(subServiceName);
        expert.getSubServices().remove(subServices);
        subServices.getExpertList().remove(expert);
        subServicesService.editSubService(subServices);
        expertRepository.save(expert);
    }

    public void addExpertToSubService(String expertEmail, String subServiceName) {
        SubServices subServices;
        Expert expert = findExpertByEmail(expertEmail);
        subServices = subServicesService.findSubServiceByName(subServiceName);
        if (subServicesUtil.isThereSubServiceName(expert.getSubServices(), subServiceName))
            throw new DuplicateSubServiceNameException();
        if (expert.getExpertStatus().equals(WAIT_FOR_VERIFY_EMAIL)
                || expert.getExpertStatus().equals(WAIT_FOR_ACCEPT))
            throw new DeActiveAccountException();
        expert.getSubServices().add(subServices);
        subServices.getExpertList().add(expert);
        subServicesService.editSubService(subServices);
        expertRepository.save(expert);
    }

    public void changeExpertStatus(String expertEmail, ExpertStatus newStatus) {
        Expert expert = findExpertByEmail(expertEmail);
        if (expert.getExpertStatus().equals(newStatus))
            throw new ExpertStatusSetAgainException();
        expert.setExpertStatus(newStatus);
        expert.setActive(true);
        expertRepository.save(expert);
    }

    public void saveExpertImageToFile(String expertEmail) {
        Expert expert = findExpertByEmail(expertEmail);
        if (expert.getExpertStatus().equals(WAIT_FOR_VERIFY_EMAIL)
                || expert.getExpertStatus().equals(WAIT_FOR_ACCEPT))
            throw new DeActiveAccountException();
        BufferedImage bufferedImage = ExpertUtil.toBufferedImage(expert.getImage());
        ExpertUtil.saveImage(bufferedImage);
    }

    public Set<Order> showOrdersRelatedToSubService(String expertEmail, String subServiceName) {
        Expert expert = findExpertByEmail(expertEmail);
        if (expert.getExpertStatus().equals(WAIT_FOR_VERIFY_EMAIL)
                || expert.getExpertStatus().equals(WAIT_FOR_ACCEPT))
            throw new DeActiveAccountException();
        SubServices subServiceByName = subServicesService.findSubServiceByName(subServiceName);
        if (expert.getSubServices().contains(subServiceByName))
            return orderService.findOrdersBySubServiceName(subServiceName);
        throw new InvalidRequestForDoNotExistSubServiceException();
    }

    public void addNewProposalForOrder(String expertEmail, Proposal proposal, String orderCode) {

        Expert expert = findExpertByEmail(expertEmail);

        if (expert.getExpertStatus().equals(WAIT_FOR_VERIFY_EMAIL)
                || expert.getExpertStatus().equals(WAIT_FOR_ACCEPT))
            throw new DeActiveAccountException();

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
                orderByCode.setOrderStatus(WAIT_FOR_CHOOSING_EXPERT);

            proposal.setExpertRate(expert.getRating());
            proposal.setExpertEmail(expertEmail);
            proposal.setOrder(orderByCode);
            proposalService.saveEditedProposal(proposal);
        } else
            throw new SendProposalOnInvalidSubServiceException();
    }

    public Set<Order> showRelatedOrders(String subServiceName) {
        Set<Order> orders = orderService
                .findOrderByStatusAndSubServiceName(WAIT_FOR_EXPERT_PROPOSAL, subServiceName);
        orders.addAll(orderService
                .findOrderByStatusAndSubServiceName(WAIT_FOR_CHOOSING_EXPERT, subServiceName));
        return orders;
    }

    public List<Expert> showListOfNewExperts() {
        List<Expert> experts = expertRepository.findExpertsByExpertStatus(WAIT_FOR_VERIFY_EMAIL);
        experts.addAll(expertRepository.findExpertsByExpertStatus(WAIT_FOR_ACCEPT));
        return experts;

    }

    public void getCostOfService(String expertEmail, double cost) {
        Expert expert = findExpertByEmail(expertEmail);
        expert.setCredit(expert.getCredit() + (cost * 0.7));
        expertRepository.save(expert);
    }

    public Expert addOpinionOnAcceptedOrder(String expertEmail) {
        Expert expert = findExpertByEmail(expertEmail);
        if (expert.getExpertStatus().equals(WAIT_FOR_VERIFY_EMAIL)
                || expert.getExpertStatus().equals(WAIT_FOR_ACCEPT))
            throw new DeActiveAccountException();
        expert.setRating(opinionService.calcExpertRate(expertEmail));
        expertRepository.save(expert);
        return expert;
    }

    public void changePassword(String expertEmail, String newPassword, String confirmedPassword) {
        if (!newPassword.equals(confirmedPassword))
            throw new UnequalPasswordsException();
        Expert expert = findExpertByEmail(expertEmail);
        if (expert.getExpertStatus().equals(WAIT_FOR_VERIFY_EMAIL)
                || expert.getExpertStatus().equals(WAIT_FOR_ACCEPT))
            throw new DeActiveAccountException();
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
        if (checkRequestFields(request))
            return expertRepository.findAll();
        applyMaxOrMinStatus(request, all);
        applySubServiceName(request, all);
        applyRegisterDate(request, all);
        return all;
    }

    public List<Order> showOrderHistory(String expertEmail) {
        return orderService.showDoneOrdersByExpert(expertEmail);
    }

    public void setExpertRating(String expertEmail, Date startTime, Date doneTime, String neededTime) {
        Expert expertByEmail = findExpertByEmail(expertEmail);
        int betweenTime = ExpertUtil.calculateDistanceBetweenTime(startTime, doneTime, neededTime);
        if (betweenTime < 0) {
            double expertRate = expertByEmail.getRating() + betweenTime;
            if (expertRate < 0)
                expertByEmail.setActive(false);
            expertByEmail.setRating(expertRate);
        }
        expertRepository.save(expertByEmail);
    }

    public Expert PrepareNewObject(ExpertSaveRequestDto requestDto) {
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

    public void verifyEmail(String expertEmail) {
        Expert expert = findExpertByEmail(expertEmail);
        if (!expert.getExpertStatus().equals(WAIT_FOR_VERIFY_EMAIL))
            throw new ReVerifyException();
        changeExpertStatus(expertEmail, WAIT_FOR_ACCEPT);
    }

    public double getExpertRating(String expertEmail) {
        Expert expert = findExpertByEmail(expertEmail);
        return expert.getRating();
    }

    private Expert findMaxRating() {
        return expertRepository.findMaxRating().orElseThrow(NoSuchUserFound::new);
    }

    private Expert findMinRating() {
        return expertRepository.findMinRating().orElseThrow(NoSuchUserFound::new);
    }

    private boolean checkRequestFields(ExpertSearchRequest request) {
        return request.getLastName() == null && request.getFirstName() == null
                && request.getEmail() == null && request.getNationalCode() == null
                && request.getRegisterDate() == null && request.getStatus() == null
                && request.getDoneOrdersCount() == 0 && request.getSubServiceName() == null;
    }

    private void applyMaxOrMinStatus(ExpertSearchRequest request, List<Expert> all) {
        if (request.getStatus().equalsIgnoreCase("max") &&
                !all.contains(findMaxRating()))
            all.add(findMaxRating());
        if (request.getStatus().equalsIgnoreCase("min") &&
                !all.contains(findMinRating()))
            all.add(findMinRating());
    }

    private void applySubServiceName(ExpertSearchRequest request, List<Expert> all) {
        List<Expert> bySubServicesName = expertRepository
                .findExpertsBySubServicesName(subServicesService
                        .getSubServiceIdByName(request
                                .getSubServiceName()));
        for (Expert expert : bySubServicesName)
            if (!all.contains(expert))
                all.add(expert);
    }

    private void applyRegisterDate(ExpertSearchRequest request, List<Expert> all) {
        if (request.getRegisterDate() != null) {
            List<Expert> experts = expertRepository.findExpertsByRegisterDate(request.getRegisterDate());
            for (Expert expert : experts)
                if (!all.contains(expert))
                    all.add(expert);
        }
    }
}
