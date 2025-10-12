import { useState } from "react";
import { ReportsAPI } from "../api/api.reports";
import { toast } from "react-hot-toast";

export function useDownloadUsersReport() {
  const [loading, setLoading] = useState(false);

  const downloadUsersReport = async (params = {}) => {
    setLoading(true);
    try {
      const response = await ReportsAPI.downloadUsers(params);
      // Retorna la respuesta para que el componente maneje la descarga y los toasts
      return response;
    } catch (e) {
      toast.error("No se pudo descargar el reporte de usuarios.");
      throw e;
    } finally {
      setLoading(false);
    }
  };

  return { downloadUsersReport, loading };
}