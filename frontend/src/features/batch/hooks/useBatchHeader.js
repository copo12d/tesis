import { useEffect, useState } from "react";
import { BatchAPI } from "../api/api.batch";
import { toast } from "react-hot-toast";

export function useBatchHeader(id) {
  const [header, setHeader] = useState(null);
  const [loadingHeader, setLoadingHeader] = useState(true);

  const fetchHeader = async () => {
    setLoadingHeader(true);
    try {
      const res = await BatchAPI.getOne(id);
      setHeader(res.data?.data || null);
    } catch {
      toast.error("No se pudo cargar el lote");
    } finally {
      setLoadingHeader(false);
    }
  };

  useEffect(() => {
    if (id) fetchHeader();
    // eslint-disable-next-line
  }, [id]);

  return { header, loadingHeader, refetchHeader: fetchHeader };
}