package com.quimify.api.notfoundquery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// This class implements not found queries logic.

@Service
public class NotFoundQueryService {

    @Autowired
    NotFoundQueryRepository notFoundQueryRepository; // DB connection

    // Internal:

    public void log(String query, Class<?> location) {
        NotFoundQueryModel notFoundQueryModel = new NotFoundQueryModel();

        String locationName = location.getName().replaceAll(".*\\.", "");

        notFoundQueryModel.setQuery(query);
        notFoundQueryModel.setLocation(locationName);

        notFoundQueryRepository.save(notFoundQueryModel);
    }

}
