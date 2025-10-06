import { useCallback, useEffect, useRef, useState } from "react";
import { UsersAPI } from "../api/user.api";
import { toast } from "react-hot-toast";

export function useUserAdvancedSearch({
  initialPage = 1,
  pageSize = 10,
  sortBy = "id",
  sortDir = "DESC",
  autoFetch = true,
  emitToasts = true,
  debounceMs = 400, // <-- nuevo parámetro
} = {}) {
  const [items, setItems] = useState([]);
  const [page, setPage] = useState(initialPage);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [totalPages, setTotalPages] = useState(0);

  // Estados para búsqueda
  const [searchTerm, setSearchTerm] = useState("");
  const [searchType, setSearchType] = useState("username");

  // Referencia para el timeout de debounce
  const debounceRef = useRef();

  const fetchUsers = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const params = {
        page: page - 1,
        size: pageSize,
        sortBy,
        sortDir,
        searchTerm,
        searchType,
      };
      const res = await UsersAPI.advanceSearch(params);
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
        "Error buscando usuarios";
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
      setPage(1); // Reinicia a la primera página si cambia el término o tipo
      fetchUsers();
    }, debounceMs);
    return () => clearTimeout(debounceRef.current);
    // eslint-disable-next-line
  }, [searchTerm, searchType, autoFetch]);

  // Fetch inicial si autoFetch está activo
  useEffect(() => {
    if (autoFetch) fetchUsers();
     
  }, [fetchUsers, autoFetch]);

  // Búsqueda manual
  const handleSearch = () => {
    setPage(1);
    fetchUsers();
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
    refetch: fetchUsers,
    searchTerm,
    setSearchTerm,
    searchType,
    setSearchType,
    handleSearch,
  };
}