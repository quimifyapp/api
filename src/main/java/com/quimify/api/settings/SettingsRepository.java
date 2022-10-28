package com.quimify.api.settings;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// Esta clase implementa conexiones con la DB automáticamente gracias a la librería JPA.

@Repository
public interface SettingsRepository extends CrudRepository<SettingsModel, Integer> {

    SettingsModel findByVersion(Integer version);

}
