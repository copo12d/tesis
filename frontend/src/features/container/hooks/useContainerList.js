import { useCallback, useEffect, useRef, useState } from "react";
import { ContainerAPI } from "../api/container.api";
import { toast } from "react-hot-toast";

/**
 * Hook para manejar la lista de contenedores con paginación, búsqueda y debounce.
 */
export function useContainerList({
  initialPage = 1,
  pageSize = 10,
  sortBy = "serial",
  sortDir = "asc",
  autoFetch = true,
  emitToasts = true,
  debounceMs = 400,
  initialSearchType = "serial", // <-- nuevo
} = {}) {
  const [items, setItems] = useState([]);
  const [page, setPage] = useState(initialPage);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [totalPages, setTotalPages] = useState(0);

  // Estado para búsqueda
  const [searchTerm, setSearchTerm] = useState("");
  const [searchType, setSearchType] = useState(initialSearchType); // <-- nuevo

  // Referencia para el timeout de debounce
  const debounceRef = useRef();

  const fetchContainers = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const params = {
        page: page - 1,
        size: pageSize,
        sortBy,
        sortDir,
      };
      if (searchTerm && searchType) {
        params[searchType] = searchTerm; // <-- clave: serial: valor
      }
      const res = await ContainerAPI.list(params);
      const content = res?.data?.data?.content || [];
      const totalElements = res?.data?.data?.totalElements ?? content.length;
      const totalPages = res?.data?.data?.totalPages;
      setItems(content);
      setTotal(totalElements);
      setTotalPages(totalPages);
    } catch (e) {
      const msg =
        e?.response?.data?.meta?.message ||
        e?.response?.data?.message ||
        e.message ||
        "Error buscando contenedores";
      if (emitToasts) toast.error(msg);
      setError(msg);
      setItems([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  }, [page, pageSize, sortBy, sortDir, searchTerm, searchType, emitToasts]);

  // Búsqueda automática con debounce al escribir
  useEffect(() => {
    if (!autoFetch) return;
    if (debounceRef.current) clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => {
      setPage(1); // Reinicia a la primera página si cambia el término
      fetchContainers();
    }, debounceMs);
    return () => clearTimeout(debounceRef.current);
    // eslint-disable-next-line
  }, [searchTerm, autoFetch]);

  // Fetch inicial si autoFetch está activo
  useEffect(() => {
    if (autoFetch) fetchContainers();
  }, [fetchContainers, autoFetch]);

  // Búsqueda manual
  const handleSearch = () => {
    setPage(1);
    fetchContainers();
  };

  return {
    items,
    total,
    page,
    pageSize,
    loading,
    error,
    totalPages,
    setPage,
    refetch: fetchContainers,
    searchTerm,
    setSearchTerm,
    handleSearch,
  };
}