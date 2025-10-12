import { useEffect, useState } from "react";
import { ContainerAPI } from "../api/container.api";

export function useContainerTypes() {
  const [types, setTypes] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let mounted = true;
    ContainerAPI.getTypes()
      .then(res => {
        if (mounted) setTypes(res?.data?.data || []);
      })
      .finally(() => {
        if (mounted) setLoading(false);
      });
    return () => { mounted = false; };
  }, []);

  return { types, loading };
}