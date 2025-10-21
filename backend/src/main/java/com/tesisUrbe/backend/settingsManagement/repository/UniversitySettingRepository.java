package com.tesisUrbe.backend.settingsManagement.repository;

import com.tesisUrbe.backend.entities.setting.UniversitySetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UniversitySettingRepository extends JpaRepository<UniversitySetting, Long> {}