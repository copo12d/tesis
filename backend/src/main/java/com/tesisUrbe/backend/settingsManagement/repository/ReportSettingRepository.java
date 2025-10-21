package com.tesisUrbe.backend.settingsManagement.repository;

import com.tesisUrbe.backend.entities.setting.ReportSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportSettingRepository extends JpaRepository<ReportSetting, Long> {}