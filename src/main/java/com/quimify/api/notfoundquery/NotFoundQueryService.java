package com.quimify.api.notfoundquery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// This class implements not found queries logic.

@Service
public class NotFoundQueryService {

    @Autowired
    NotFoundQueryRepository notFoundQueryRepository;

    // Internal:

    public void log(String query, Class<?> location) {
        String locationName = location.getName().replaceAll(".*\\.", "");

        NotFoundQueryModel notFoundQueryModel = new NotFoundQueryModel(query, locationName);

        notFoundQueryRepository.save(notFoundQueryModel);
    }

}
