import { useEffect, useState, useCallback } from "react";
import { UsersAPI } from "../api/user.api";

export function useUpdateUser(id) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(false);
  const [updating, setUpdating] = useState(false);
  const [error, setError] = useState(null);

  // 1 y 2. Traer la información del usuario
  const fetchUser = useCallback(async () => {
    if (!id) return;
    setLoading(true);
    setError(null);
    try {
      const res = await UsersAPI.get(id);
      setUser(res?.data?.data || null);
    } catch (e) {
      setError(e);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    fetchUser();
  }, [fetchUser]);

  // 3. Actualizar usuario
  const updateUser = async (payload) => {
    setUpdating(true);
    setError(null);
    try {
      await UsersAPI.update(id, payload);
      await fetchUser(); // refresca datos tras actualizar
      return true;
    } catch (e) {
      setError(e);
      return false;
    } finally {
      setUpdating(false);
    }
  };

  return {
    user,
    loading,
    updating,
    error,
    fetchUser,
    updateUser,
  };
}