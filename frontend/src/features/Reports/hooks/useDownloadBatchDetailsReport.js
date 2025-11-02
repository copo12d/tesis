import { useEffect, useState } from "react";
import { ReportsAPI } from "../api/api.reports";
import { toast } from "react-hot-toast";

export function useDownloadBatchDetailsReport() {
  const [loading, setLoading] = useState(false);

  // NUEVO: listado de lotes para el combo
  const [batches, setBatches] = useState([]);
  const [loadingBatches, setLoadingBatches] = useState(true);

  const fetchBatches = async () => {
    setLoadingBatches(true);
    try {
      const res = await ReportsAPI.getAllBatches(); // usa el endpoint existente
      const list = res?.data?.data?.content ?? [];
      setBatches(Array.isArray(list) ? list : []);
    } catch {
      setBatches([]);
    } finally {
      setLoadingBatches(false);
    }
  };

  useEffect(() => {
    fetchBatches();
  }, []);

  const downloadBatchDetails = async ({ batchId }) => {
    const id = Number(batchId);
    if (!Number.isFinite(id) || id <= 0) {
      toast.error("Selecciona un lote válido.");
      throw new Error("batchId inválido");
    }
    setLoading(true);
    try {
      // Ajusta si tu API requiere path param en lugar de body
      return await ReportsAPI.downloadBatch(id);
    } catch (e) {
      toast.error("No se pudo descargar el detalle del lote.");
      throw e;
    } finally {
      setLoading(false);
    }
  };

  return { downloadBatchDetails, loading, batches, loadingBatches};
}