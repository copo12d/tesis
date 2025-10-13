import { useEffect, useState } from "react";
import { BatchAPI } from "../api/api.batch";
import { toast } from "react-hot-toast";

export function useBatchHeaders() {
  const [headers, setHeaders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    BatchAPI.getBatchHeaders()
      .then(res => setHeaders(res.data?.data ?? []))
      .catch(err => {
        toast.error(
          err?.response?.data?.meta?.message ||
          "No se pudieron obtener los lotes"
        );
      })
      .finally(() => setLoading(false));
  }, []);

  return { headers, loading };
}