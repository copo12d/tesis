import { useState, useCallback, useEffect, useRef } from "react";
import { ContainerTypeAPI } from "../api/containerType.api";
import toast from "react-hot-toast";

export function useContainerTypeList({
  initialPage = 1,
  pageSize = 10,
  sortBy = "id",
  sortDir = "DESC",
  autoFetch = true,
  debounceMs = 400,
} = {}) {
  const [items, setItems] = useState([]);
  const [page, setPage] = useState(initialPage);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [error, setError] = useState(null);
  const [totalPages, setTotalPages] = useState(0);

  const debounceRef = useRef();

  const fetchContainerTypes = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await ContainerTypeAPI.list({
        name: searchTerm,
        page: page - 1,
        size: pageSize,
        sortBy,
        sortDir,
      });
      const content = res?.data?.data?.content || [];
      const totalElements = res?.data?.data?.totalElements ?? content.length;
      const totalPages = res?.data?.data?.totalPages ?? 1;
      setItems(content);
      setTotal(totalElements);
      setTotalPages(totalPages);
    } catch (e) {
      const msg =
        e?.response?.data?.meta?.message ||
        e?.response?.data?.message ||
        e.message ||
        "Error buscando tipos de contenedor";
      toast.error(msg);
      setError(msg);
      setItems([]);
      setTotal(0);
      setTotalPages(0);
    } finally {
      setLoading(false);
    }
  }, [searchTerm, page, pageSize, sortBy, sortDir]);

  // Debounce para búsqueda
  useEffect(() => {
    if (!autoFetch) return;
    if (debounceRef.current) clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => {
      setPage(1); // Reinicia a la primera página si cambia el término
      fetchContainerTypes();
    }, debounceMs);
    return () => clearTimeout(debounceRef.current);
    // eslint-disable-next-line
  }, [searchTerm, autoFetch]);

  // Fetch inicial y cuando cambian dependencias principales
  useEffect(() => {
    if (autoFetch) fetchContainerTypes();
  }, [fetchContainerTypes, autoFetch, page, pageSize, sortBy, sortDir]);

  // Búsqueda manual
  const handleSearch = () => {
    setPage(1);
    fetchContainerTypes();
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
    refetch: fetchContainerTypes,
    searchTerm,
    setSearchTerm,
    handleSearch,
  };
}