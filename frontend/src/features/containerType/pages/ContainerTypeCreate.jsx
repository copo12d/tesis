import { useNavigate } from "react-router-dom";
import { useCreateContainerType } from "../hooks/useCreateContainerType";
import { ContainerTypeForm } from "../components/ContainerTypeForm";

export function ContainerTypeCreate() {
  const navigate = useNavigate();
  const { createContainerType, loading } = useCreateContainerType();

  const initialValues = { name: "", description: "" };

  const handleSubmit = async (form) => {
    const ok = await createContainerType(form);
    if (ok) navigate("/container-type/list");
  };

  return (
    <ContainerTypeForm
      initialValues={initialValues}
      loading={loading}
      onSubmit={handleSubmit}
      submitText="Registrar"
      title="Registrar tipo de contenedor"
    />
  );
}