import { useState, useEffect } from "react";
import { UsersAPI } from "../api/user.api";
import toast from "react-hot-toast";

export function useUserProfile(id) {
  const [profile, setProfile] = useState({ fullName: "", userName: "" });
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);

  // Obtener datos del perfil
  useEffect(() => {
    if (!id) return;
    setLoading(true);
    UsersAPI.getProfile(id)
      .then(res => {
        setProfile({
          fullName: res.data?.data?.fullName || "",
          userName: res.data?.data?.userName || "",
        });
      })
      .catch(err => {
        setError(err);
        toast.error(
          err?.response?.data?.meta?.message ||
          err?.response?.data?.message ||
          err.message ||
          "Error obteniendo perfil"
        );
      })
      .finally(() => setLoading(false));
  }, [id]);

  // Actualizar perfil
  const updateProfile = async (data) => {
    setSaving(true);
    try {
      const res = await UsersAPI.updateProfile(id, data);
      toast.success(
        res?.data?.meta?.message || "Perfil actualizado correctamente."
      );
      setProfile(data);
      return true;
    } catch (err) {
      toast.error(
        err?.response?.data?.meta?.message ||
        err?.response?.data?.message ||
        err.message ||
        "Error actualizando perfil"
      );
      return false;
    } finally {
      setSaving(false);
    }
  };

  return { profile, setProfile, loading, saving, error, updateProfile };
}