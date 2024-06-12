package com.quimify.api.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import com.quimify.api.classification.ClassificationService;
import com.quimify.api.inorganic.InorganicService;
import com.quimify.api.molecularmass.MolecularMassService;
import com.quimify.api.organic.OrganicService;

@Service
@RequestScope // For doing unique every Health Request and errors, then deleting everything
class HealthService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public final InorganicService inorganicService;
    public final OrganicService organicService;
    public final MolecularMassService molecularMassService;
    public final ClassificationService classificatService;

    public HealthService(InorganicService inorganicService, OrganicService organicService,
            MolecularMassService molecularMassService, ClassificationService classificationService) {
        this.inorganicService = inorganicService;
        this.organicService = organicService;
        this.molecularMassService = molecularMassService;
        this.classificatService = classificationService;
    }

    public HealthResult checkOverallHealth() {
        HealthResult overallHealth = new HealthResult(false, ""); // Start pessimistic, no errors yet

        // Check all systems
        checkAndAggregateErrors(overallHealth, inorganicService.healthCheck());
        checkAndAggregateErrors(overallHealth, organicService.healthCheck());
        checkAndAggregateErrors(overallHealth, molecularMassService.healthCheck());
        checkAndAggregateErrors(overallHealth, classificatService.healthCheck());

        // Update the final message and overall status
        if (overallHealth.getErrors().isEmpty()) {
            overallHealth.setPresent(true);
            overallHealth.setMessage("All systems operational");
        } else {
            overallHealth.setMessage("System errors detected: " + overallHealth.getErrors()); // Include errors in the
                                                                                              // message
        }

        return overallHealth;
    }

    private void checkAndAggregateErrors(HealthResult overallHealth, HealthResult subSystemHealth) {
        if (!subSystemHealth.isPresent()) {
            overallHealth.setPresent(false);
            overallHealth.addError(subSystemHealth.getMessage());
            ;
        }
    }
}