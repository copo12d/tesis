import { useState } from "react";
import { ReportsAPI } from "../api/api.reports";
import { toast } from "react-hot-toast";

/**
 * Hook para descargar el reporte de contenedores.
 * @returns {object} { downloadContainersReport, loading }
 */
export function useDownloadContainersReport() {
  const [loading, setLoading] = useState(false);

  const downloadContainersReport = async (params = {}) => {
    setLoading(true);
    try {
      const response = await ReportsAPI.downloadContainers(params);
      // Retorna la respuesta para que el componente maneje la descarga
      return response;
    } catch (e) {
      toast.error("No se pudo descargar el reporte de contenedores.");
      throw e;
    } finally {
      setLoading(false);
    }
  };

  return { downloadContainersReport, loading };
}