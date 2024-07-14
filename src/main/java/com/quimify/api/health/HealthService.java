package com.quimify.api.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import com.quimify.api.classification.ClassificationService;
import com.quimify.api.inorganic.InorganicService;
import com.quimify.api.molecularmass.MolecularMassService;
import com.quimify.api.organic.OrganicService;

@Service
@RequestScope // For doing unique every Health Request and errors, then deleting everything
class HealthService {

    @Autowired
    public InorganicService inorganicService;

    @Autowired
    public OrganicService organicService;

    @Autowired
    public MolecularMassService molecularMassService;

    @Autowired
    public ClassificationService classificatService;

    public HealthResult health() {
        HealthResult overallHealth = new HealthResult(false, ""); // Start pessimistic, no errors yet

        // Check all systems
        checkAndAggregateErrors(overallHealth, inorganicService.checkHealth());
        checkAndAggregateErrors(overallHealth, organicService.checkHealth());
        checkAndAggregateErrors(overallHealth, molecularMassService.checkHealth());
        checkAndAggregateErrors(overallHealth, classificatService.checkHealth());

        // Update the final message and overall status
        if (overallHealth.getErrors().isEmpty()) {
            overallHealth.setHealthy(true);
            overallHealth.setMessage("All systems operational");
        } else {
            overallHealth.setMessage("System errors detected: " + overallHealth.getErrors()); // Include errors in
                                                                                              // message
        }

        return overallHealth;
    }

    private void checkAndAggregateErrors(HealthResult overallHealth, HealthResult subSystemHealth) {
        if (!subSystemHealth.isHealthy()) {
            overallHealth.setHealthy(false);
            overallHealth.addError(subSystemHealth.getMessage());
        }
    }
}