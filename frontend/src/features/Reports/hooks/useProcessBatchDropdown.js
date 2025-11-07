import { useState, useEffect } from "react";
import { ReportsAPI } from "../api/api.reports";
import { toast } from "react-hot-toast";

export function useProcessBatchDropdown() {
  const [loadingBatches, setLoadingBatches] = useState(false);
  const [batches, setBatches] = useState([]);

  const fetchBatches = async () => {
    setLoadingBatches(true);
    try {
      const response = await ReportsAPI.getProcessBatchDropdown();
      const data = response?.data?.data ?? [];
      setBatches(data);
    } catch (e) {
      toast.error("No se pudieron cargar los lotes procesados.");
    } finally {
      setLoadingBatches(false);
    }
  };

  useEffect(() => {
    fetchBatches();
  }, []);

  return {
    batches,
    loadingBatches,
  };
}
