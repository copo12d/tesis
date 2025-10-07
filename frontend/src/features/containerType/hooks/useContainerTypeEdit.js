import { useState, useEffect } from "react";
import { ContainerTypeAPI } from "../api/containerType.api";
import toast from "react-hot-toast";
import { useNavigate } from "react-router-dom";

/**
 * Hook para editar un tipo de contenedor.
 * - Obtiene los datos actuales por ID y permite actualizar.
 */
export function useContainerTypeEdit(id) {
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [form, setForm] = useState({ name: "", description: "" });
  const navigate = useNavigate();

  // Obtener datos actuales
  useEffect(() => {
    if (!id) return;
    setLoading(true);
    ContainerTypeAPI.getById(id)
      .then(res => {
        setForm({
          name: res.data?.data?.name || "",
          description: res.data?.data?.description || "",
        });
      })
      .catch(err => {
        const apiErrors = err.response?.data?.errors;
        let apiMessage = "Ocurrió un error inesperado";
        if (Array.isArray(apiErrors) && apiErrors.length > 0 && apiErrors[0].message) {
          apiMessage = apiErrors[0].message;
        } else if (err.response?.data?.meta?.message) {
          apiMessage = err.response.data.meta.message;
        }
        toast.error(apiMessage);
        navigate("/container-type/list");
      })
      .finally(() => setLoading(false));
    // eslint-disable-next-line
  }, [id]);

  // Actualizar datos
  const updateContainerType = async (data) => {
    setSaving(true);
    try {
      const res = await ContainerTypeAPI.update(id, data);
      const apiMessage =
        res?.data?.meta?.message || "Tipo de contenedor actualizado correctamente.";
      toast.success(apiMessage);
      navigate("/container-type/list");
      return true;
    } catch (err) {
      const apiErrors = err.response?.data?.errors;
      let apiMessage = "Ocurrió un error inesperado";
      if (Array.isArray(apiErrors) && apiErrors.length > 0 && apiErrors[0].message) {
        apiMessage = apiErrors[0].message;
      } else if (err.response?.data?.meta?.message) {
        apiMessage = err.response.data.meta.message;
      }
      toast.error(apiMessage);
      return false;
    } finally {
      setSaving(false);
    }
  };

  return {
    form,
    setForm,
    loading,
    saving,
    updateContainerType,
  };
}