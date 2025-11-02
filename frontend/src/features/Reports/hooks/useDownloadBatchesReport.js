import { useMemo, useState } from "react";
import { toast } from "react-hot-toast";
import { ReportsAPI } from "../api/api.reports";

const toDate = (d) => (d ? new Date(d) : null);
const isValidDate = (d) => d instanceof Date && !isNaN(d);

export function useDownloadBatchesReport() {
  const [startDate, setStartDate] = useState(""); // "YYYY-MM-DD" opcional
  const [endDate, setEndDate] = useState("");     // "YYYY-MM-DD" opcional
  const [loading, setLoading] = useState(false);

  // No obliga fechas: solo valida si existen y, si ambas existen, el orden
  const rangeError = useMemo(() => {
    const s = toDate(startDate);
    const e = toDate(endDate);

    if (startDate && !isValidDate(s)) return "Fecha inicio inválida";
    if (endDate && !isValidDate(e)) return "Fecha fin inválida";
    if (startDate && endDate && s > e) return "La fecha inicio no puede ser mayor que la fin";
    return "";
  }, [startDate, endDate]);

  const isRangeValid = !rangeError;

  const downloadBatchesReport = async (override) => {
    const s = override?.startDate ?? startDate;
    const e = override?.endDate ?? endDate;

    const sDate = s ? toDate(s) : null;
    const eDate = e ? toDate(e) : null;

    // Valida solo lo presente
    if (s && !isValidDate(sDate)) {
      toast.error("Fecha inicio inválida.");
      throw new Error("Fecha inicio inválida");
    }
    if (e && !isValidDate(eDate)) {
      toast.error("Fecha fin inválida.");
      throw new Error("Fecha fin inválida");
    }
    if (s && e && sDate > eDate) {
      toast.error("La fecha inicio no puede ser mayor que la fin.");
      throw new Error("Rango invertido");
    }

    const params = {};
    if (s) params.fechaInicio = s; // mapear a los nombres que pide el backend
    if (e) params.fechaFin = e;

    setLoading(true);
    try {
      return await ReportsAPI.downloadAllBatches(params); // usar el nombre correcto
    } catch (err) {
      toast.error("No se pudo descargar el reporte de lotes.");
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const setRange = ({ start, end }) => {
    setStartDate(start ?? "");
    setEndDate(end ?? "");
  };

  return {
    startDate,
    setStartDate,
    endDate,
    setEndDate,
    setRange,
    isRangeValid,
    rangeError,
    loading,
    downloadBatchesReport,
  };
}