import { useParams, useNavigate } from "react-router-dom";
import { useMemo, useEffect } from "react";
import { useContainerType } from "../hooks/useContainerType";
import { ContainerTypeForm } from "../components/ContainerTypeForm";

export function ContainerTypeEdit() {
  const { id } = useParams();
  const {
    containerType,
    loading,
    updating,
    updateContainerType,
    fetchContainerType,
    error,
  } = useContainerType(id);
  const navigate = useNavigate();

  useEffect(() => {
    fetchContainerType();
  }, [id, fetchContainerType]);

  const initialValues = useMemo(
    () => ({
      name: containerType?.name || "",
      description: containerType?.description || "",
    }),
    [containerType?.name, containerType?.description]
  );

  const handleSubmit = async (form) => {
    const ok = await updateContainerType(form);
    if (ok) navigate("/container-type/list");
  };

  if (error) {
    return (
      <div style={{ padding: 40, textAlign: "center" }}>
        <p>Error cargando tipo de contenedor.</p>
      </div>
    );
  }

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