import { useState } from "react";
import { ReportsAPI } from "../api/api.reports";
import { toast } from "react-hot-toast";

function downloadFile(response, filename = "reporte_usuarios.pdf") {
  const url = window.URL.createObjectURL(new Blob([response.data]));
  const link = document.createElement("a");
  link.href = url;
  link.setAttribute("download", filename);
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
}

export function useDownloadUsersReport() {
  const [loading, setLoading] = useState(false);

  const downloadUsersReport = async () => {
    setLoading(true);
    try {
      const response = await ReportsAPI.downloadUsers();
      downloadFile(response, "reporte_usuarios.pdf");
      toast.success("El reporte de usuarios se ha descargado.");
    } catch {
      toast.error("No se pudo descargar el reporte de usuarios.");
    } finally {
      setLoading(false);
    }
  };

  return { downloadUsersReport, loading };
}