package com.quimify.api.health;

import java.util.ArrayList;
import java.util.List;

import com.quimify.api.error.ErrorService;
import com.quimify.api.inorganic.InorganicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import com.quimify.api.classification.ClassificationService;
import com.quimify.api.molecularmass.MolecularMassService;
import com.quimify.api.organic.OrganicService;

@Service
@RequestScope
class HealthService {

    @Autowired
    InorganicService inorganicService;

    @Autowired
    OrganicService organicService;

    @Autowired
    MolecularMassService molecularMassService;

    @Autowired
    ClassificationService classificationService;

    @Autowired
    ErrorService errorService;

    // Public:

    public HealthResult checkHealth() {
        List<String> errors = new ArrayList<>();

        addErrorIfUnhealthy(inorganicService.isHealthy(), inorganicService.getClass().getName(), errors);
        addErrorIfUnhealthy(organicService.isHealthy(), organicService.getClass().getName(), errors);
        addErrorIfUnhealthy(molecularMassService.isHealthy(), molecularMassService.getClass().getName(), errors);
        addErrorIfUnhealthy(classificationService.isHealthy(), classificationService.getClass().getName(), errors);

        if (!errors.isEmpty())
            errorService.log("Unhealthy services", errors.toString(), getClass());

        return new HealthResult(errors.isEmpty(), errors);
    }

    // Private:

    private void addErrorIfUnhealthy(boolean healthy, String locationName, List<String> errors) {
        if (!healthy) {
            String locationNameWithoutPackages = locationName.replaceAll(".*\\.", "");
            errors.add(locationNameWithoutPackages);
        }
    }

}