package com.quimify.api.settings;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// This class implements connections to the DB automatically thanks to the JPA library.

@Repository
interface SettingsRepository extends CrudRepository<SettingsModel, Integer> {

    SettingsModel findByVersion(Integer version);

}
