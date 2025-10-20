package com.tesisUrbe.backend.settingsManagement.repository;

import com.tesisUrbe.backend.entities.setting.UbicationSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UbicationSettingRepository extends JpaRepository<UbicationSetting, Long> {}