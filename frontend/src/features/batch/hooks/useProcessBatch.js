import { useState } from "react";
import { BatchAPI } from "../api/api.batch";
import { toast } from "react-hot-toast";

export function useProcessBatch() {
  const [processing, setProcessing] = useState(false);

  const processBatch = async (id) => {
    setProcessing(true);
    try {
      const res = await BatchAPI.process(id);
      const msg =
        res.data?.meta?.message ||
        "Lote procesado exitosamente";
      toast.success(msg);
      return true;
    } catch (err) {
      const apiMsg =
        err?.response?.data?.errors?.[0]?.message ||
        err?.response?.data?.meta?.message ||
        "No se pudo procesar el lote";
      toast.error(apiMsg);
      return false;
    } finally {
      setProcessing(false);
    }
  };

  return { processBatch, processing };
}