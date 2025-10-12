import { useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useContainerType } from "../hooks/useContainerType";
import { ContainerTypeForm } from "../components/ContainerTypeForm";

export function ContainerTypeEdit() {
  const { id } = useParams();
  const navigate = useNavigate();
  const {
    containerType,
    loading,
    updating,
    updateContainerType,
    fetchContainerType,
    error,
  } = useContainerType(id);

  useEffect(() => {
    fetchContainerType(id);
  }, [id, fetchContainerType]);

  // Asegúrate de que initialValues nunca sea null/undefined
  const initialValues = containerType || { name: "", description: "" };

  const handleSubmit = async (form) => {
    const ok = await updateContainerType(form);
    if (ok) navigate("/container-type/list");
  };

  return (
    <ContainerTypeForm
      initialValues={initialValues}
      loading={loading || updating}
      onSubmit={handleSubmit}
      submitText="Actualizar"
      title="Editar tipo de contenedor"
    />
  );
}