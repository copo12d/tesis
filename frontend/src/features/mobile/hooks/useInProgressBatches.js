import { useEffect, useState } from "react";
import { MobileAPI } from "../api/api.mobile";
import { toast } from "react-hot-toast";

export function useInProgressBatches() {
  const [batches, setBatches] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    MobileAPI.getInProgressBatches()
      .then((res) => {
        setBatches(res.data?.data || []);
      })
      .catch((err) => {
        const apiMsg =
          err?.response?.data?.meta?.message ||
          (err?.response?.data?.errors?.[0]?.message) ||
          "No se pudo obtener la lista de lotes";
        toast.error(apiMsg);
      })
      .finally(() => setLoading(false));
  }, []);

  return { batches, loading };
}