package com.quimify.api.health;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final List<HealthCheck> healthChecks;

    @Autowired
    public InorganicService inorganicService;

    @Autowired
    public OrganicService organicService;

    @Autowired
    public MolecularMassService molecularMassService;

    @Autowired
    public ClassificationService classificatService;

    public HealthService(List<HealthCheck> healthChecks) { // Inject all HealthCheck implementations
        this.healthChecks = healthChecks;
    }

    public Map<String, Object> health() {
        HealthResult overallHealth = new HealthResult();

        for (HealthCheck check : healthChecks) {
            String errorMessage = check.checkHealth();
            if (errorMessage != null) {
                overallHealth.addError(errorMessage);
            }
        }

        // Construct the response map directly
        Map<String, Object> response = new HashMap<>();
        response.put("healthy", overallHealth.isHealthy());
        if (!overallHealth.isHealthy()) {
            response.put("errors", overallHealth.getErrors());
        }

        return response;
    }
}