import { useNavigate } from "react-router-dom";
import { useContainerType } from "../hooks/useContainerType";
import { ContainerTypeForm } from "../components/ContainerTypeForm";

export  function ContainerTypeCreate() {
  const { registerContainerType, loading } = useContainerType();
  const navigate = useNavigate();

  const handleSubmit = async (form) => {
    const ok = await registerContainerType(form);
    if (ok) navigate("/container-type/list");
  };

  return (
    <ContainerTypeForm
      loading={loading}
      onSubmit={handleSubmit}
      submitText="Guardar"
      title="Registrar tipo de contenedor"
    />
  );
}