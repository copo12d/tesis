import { useState } from "react";
import { MobileAPI } from "../api/api.mobile";
import { toast } from "react-hot-toast";

export function useReportContainer() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const reportContainer = async (serial) => {
    setLoading(true);
    setError("");
    setSuccess("");
    try {
      const res = await MobileAPI.reportContainer(serial);
      const msg =
        res?.data?.meta?.message || "Contenedor reportado correctamente";
      setSuccess(msg);
      toast.success(msg);
      return true;
    } catch (err) {
      const apiMsg =
        err?.response?.data?.meta?.message ||
        (err?.response?.data?.errors?.[0]?.message) ||
        "No se pudo reportar el contenedor";
      setError(apiMsg);
      toast.error(apiMsg);
      return false;
    } finally {
      setLoading(false);
    }
  };

  return { reportContainer, loading, error, success };
}