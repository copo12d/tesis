import { useEffect, useMemo, useState } from "react";
import { ContainerAPI } from "../api/container.api";

export function useContainerEdit(id) {
  const [loading, setLoading] = useState(true);
  const [container, setContainer] = useState(null);
  const [error, setError] = useState("");
  const [coords, setCoords] = useState(null);

  useEffect(() => {
    if (!Number.isFinite(id)) {
      setError("ID inválido.");
      setLoading(false);
      return;
    }
    let alive = true;
    (async () => {
      try {
        setLoading(true);
        const res = await ContainerAPI.getByIdEdit(id);
        const data = res?.data?.data ?? res?.data ?? null;
        if (!alive) return;
        setContainer(data);
        const lat = Number(data?.latitude);
        const lng = Number(data?.longitude);
        if (Number.isFinite(lat) && Number.isFinite(lng)) {
          setCoords([lat, lng]);
        } else {
          setCoords(null);
        }
      } catch {
        if (alive) setError("No se pudo cargar el contenedor.");
      } finally {
        if (alive) setLoading(false);
      }
    })();
    return () => {
      alive = false;
    };
  }, [id]);

  const markerHandlers = useMemo(
    () => ({
      dragend(e) {
        const { lat, lng } = e.target.getLatLng();
        setCoords([lat, lng]);
      },
    }),
    []
  );

  const makeUpdatePayload = (values) => ({
    ...values,
    ...(Array.isArray(coords)
      ? { latitude: coords[0], longitude: coords[1] }
      : {}),
  });

  return {
    loading,
    container,
    error,
    coords,
    setCoords,
    markerHandlers,
    makeUpdatePayload,
  };
}