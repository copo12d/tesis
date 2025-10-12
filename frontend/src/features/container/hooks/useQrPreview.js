import { useState } from "react";
import { ContainerAPI } from "../api/container.api";

/**
 * Hook para obtener el QR de un contenedor.
 * @param {number|string} containerId
 */
export function useQrPreview(containerId) {
  const [qrBase64, setQrBase64] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchQr = async () => {
    setLoading(true);
    setError(null);
    setQrBase64(null);
    try {
      const res = await ContainerAPI.getQr(containerId);
      setQrBase64(res?.data?.data || null);
    } catch {
      setError("No se pudo cargar el QR.");
    } finally {
      setLoading(false);
    }
  };

  return { qrBase64, loading, error, fetchQr };
}