import { useState, useEffect } from "react";
import { BatchAPI } from "../api/api.batch";

export function useBatchList(initialParams = {}) {
  const [data, setData] = useState({ content: [], totalElements: 0, totalPages: 0 });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [params, setParams] = useState({
    page: 0,
    size: 10,
    sortBy: "creationDate",
    sortDir: "desc",
    ...initialParams,
  });

  const fetchBatches = async (overrideParams = {}) => {
    setLoading(true);
    setError("");
    try {
      const res = await BatchAPI.getAll({ ...params, ...overrideParams });
      setData(res.data?.data?.content || []); // <-- asegúrate de acceder a .data.data
    } catch (e) {
      setError("No se pudieron cargar los lotes.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBatches();
    // eslint-disable-next-line
  }, [params.page, params.size, params.sortBy, params.sortDir]);

  return {
    data,
    loading,
    error,
    params,
    setParams,
    refetch: fetchBatches,
  };
}