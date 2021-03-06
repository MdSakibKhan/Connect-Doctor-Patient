package com.sakib.connectdoctorpatient.helper;

import com.sakib.connectdoctorpatient.exception.NotFoundException;
import com.sakib.connectdoctorpatient.model.*;
import com.sakib.connectdoctorpatient.service.MedicalHistoryService;
import com.sakib.connectdoctorpatient.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static com.sakib.connectdoctorpatient.controller.MedialHistoryController.MEDICAL_HISTORY_CMD;
import static com.sakib.connectdoctorpatient.model.Role.PATIENT;
import static com.sakib.connectdoctorpatient.util.SessionUtil.getUser;

/**
 * @author sakib.khan
 * @since 3/5/22
 */
@Component
public class MedicalHistoryHelper {

    @Autowired
    private MessageSourceAccessor msa;

    @Autowired
    private PatientService patientService;

    @Autowired
    private MedicalHistoryService medicalHistoryService;

    public void setupReferenceData(long medicalHistoryId, ModelMap model) throws NotFoundException {
        model.addAttribute(MEDICAL_HISTORY_CMD, getMedicalHistory(medicalHistoryId));
        setupCategory(model);
    }

    public void setupCategory(ModelMap model) {
        model.addAttribute("categories", Category.values());
    }

    public void setupList(long patientId, ModelMap model) {
        model.addAttribute("medicalHistories", getMedicalHistories(getUser(), patientId));
    }

    public void setupSuccessMessage(Action action, RedirectAttributes ra) {
        ra.addFlashAttribute("message", msa.getMessage("success." + action));
    }

    private MedicalHistory getMedicalHistory(long medicalHistoryId) throws NotFoundException {
        if (medicalHistoryId == 0) {
            return new MedicalHistory();
        }

        MedicalHistory medicalHistory = medicalHistoryService.findById(medicalHistoryId);

        checkNull(medicalHistory, medicalHistoryId);

        return medicalHistory;
    }

    private List<MedicalHistory> getMedicalHistories(User user, long patientId) {
        List<MedicalHistory> medicalHistories = new ArrayList<>();

        if (patientId != 0) {
            Patient patient = patientService.findById(patientId);
            medicalHistories = medicalHistoryService.findByPatient(patient);
        } else if (user.getRole() == PATIENT) {
            medicalHistories = medicalHistoryService.findByPatient((Patient) user);
        }

        return medicalHistories;
    }

    private void checkNull(MedicalHistory medicalHistory, long medicalHistoryId) throws NotFoundException {
        if (isNull(medicalHistory)) {
            String message = msa.getMessage("exception.medical.history.invalid");
            message = message.replace("{id}", String.valueOf(medicalHistoryId));
            throw new NotFoundException(message);
        }
    }
}
