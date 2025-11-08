import { useCallback, useEffect, useRef, useState, useContext } from "react";
import { NotificationsAPI } from "../api/api.notifications";
import AuthContext from "@/context/AuthContext"; // añadido

export function useNotifications({
  autoFetch = false,
  intervalMs = 10_000,
  onStatusChange,
  notifyOnEveryPollWhenHasItems = false, // <-- NUEVO
} = {}) {
  const { user } = useContext(AuthContext) || {}; // añadido
  const isAdmin =
    user?.role === "ROLE_ADMIN" || user?.role === "ROLE_SUPERUSER"; // igual que AdminSection

  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [lastMessage, setLastMessage] = useState(null);

  const timerRef = useRef(null);
  const prevHadItemsRef = useRef(null);
  const onChangeRef = useRef(onStatusChange);

  useEffect(() => {
    onChangeRef.current = onStatusChange;
  }, [onStatusChange]);

  const parseApiMessage = (res) =>
    res?.data?.meta?.message || res?.data?.message || "No hay contenedores llenos";

  const fetchContainersFull = useCallback(async () => {
    // Si no es admin o superuser, no hacer la llamada ni emitir nada
    if (!isAdmin) {
      return { success: true, items: [], skipped: true };
    }

    setLoading(true);
    setError(null);
    try {
      const res = await NotificationsAPI.inform();
      const content = res?.data?.data?.content ?? res?.data?.data ?? [];
      const msg = parseApiMessage(res);

      setItems(content);
      setLastMessage(msg);

      const hasItems = Array.isArray(content) && content.length > 0;
      const prevHadItems = prevHadItemsRef.current;

      const shouldNotify =
        prevHadItems === null ||
        prevHadItems !== hasItems ||
        (notifyOnEveryPollWhenHasItems && hasItems); // <-- notifica siempre si hay llenos

      if (shouldNotify) {
        onChangeRef.current?.({
          hasItems,
          count: content.length,
          message: msg,
          items: content,
        });
      }

      prevHadItemsRef.current = hasItems;
      return { success: true, items: content, message: msg };
    } catch (err) {
      const msg =
        err?.response?.data?.errors?.[0]?.message ||
        err?.response?.data?.meta?.message ||
        err?.response?.data?.error ||
        err?.message ||
        "Error al consultar notificaciones";
      setError(msg);
      onChangeRef.current?.({ hasItems: false, count: 0, message: msg, items: [] });
      return { success: false, error: msg };
    } finally {
      setLoading(false);
    }
  }, [notifyOnEveryPollWhenHasItems, isAdmin]); // dependencia actualizada

  const start = useCallback(() => {
    if (timerRef.current) return;
    timerRef.current = setInterval(fetchContainersFull, intervalMs);
  }, [fetchContainersFull, intervalMs]);

  const stop = useCallback(() => {
    if (timerRef.current) {
      clearInterval(timerRef.current);
      timerRef.current = null;
    }
  }, []);

  useEffect(() => {
    if (!autoFetch) return;
    fetchContainersFull();         // primera carga
    start();                       // inicia polling
    return () => stop();           // limpia al desmontar/cambiar intervalMs/autoFetch
  }, [autoFetch, start, stop, fetchContainersFull]);

  return {
    items,
    loading,
    error,
    lastMessage,
    refetch: fetchContainersFull,
    start,
    stop,
    running: !!timerRef.current,
  };
}