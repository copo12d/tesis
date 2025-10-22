import { useState } from "react";
import { SettingsAPI } from "../api/api.settings";
import { toast } from "react-hot-toast";

export function useReportSetting() {
  const [loading, setLoading] = useState(false);

  const updateReportSetting = async (data) => {
    setLoading(true);
    try {
      await SettingsAPI.updateReport(data);
      toast.success("Estilos de reporte actualizados.");
    } catch {
      toast.error("No se pudo actualizar el reporte.");
    } finally {
      setLoading(false);
    }
  };

  return { updateReportSetting, loading };
}
