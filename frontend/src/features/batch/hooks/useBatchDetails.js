import { useEffect, useState, useCallback } from "react";
import { BatchAPI } from "../api/api.batch";
import { toast } from "react-hot-toast";

export function useBatchDetails(batchId) {
  const [details, setDetails] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({ serial: "", start: "", end: "" });
  const [pagination, setPagination] = useState({
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 1,
  });

  const fetchDetails = useCallback(() => {
    if (!batchId) return;
    setLoading(true);
    BatchAPI.getBatchDetails(batchId, {
      serial: filters.serial,
      start: filters.start,
      end: filters.end,
      page: pagination.page,
      size: pagination.size,
    })
      .then(res => {
        const data = res.data?.data;
        setDetails(data?.content ?? []);
        setPagination({
          page: data?.pageable?.pageNumber ?? 0,
          size: data?.pageable?.pageSize ?? 10,
          totalElements: data?.totalElements ?? 0,
          totalPages: data?.totalPages ?? 1,
        });
      })
      .catch(err => {
        toast.error(
          err?.response?.data?.meta?.message ||
          "No se pudieron obtener los detalles del lote"
        );
      })
      .finally(() => setLoading(false));
  }, [batchId, filters, pagination.page, pagination.size]);

  useEffect(() => {
    fetchDetails();
  }, [fetchDetails]);

  // Para actualizar los filtros y refrescar la búsqueda
  const search = (newFilters) => setFilters(f => ({ ...f, ...newFilters }));

  // Para cambiar de página
  const setPage = (page) => setPagination(p => ({ ...p, page }));
  const setPageSize = (size) => setPagination(p => ({ ...p, size }));

  return {
    details,
    loading,
    filters,
    search,
    refetch: fetchDetails,
    pagination,
    setPage,
    setPageSize,
  };
}