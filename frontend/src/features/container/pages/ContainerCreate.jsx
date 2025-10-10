import { useNavigate } from "react-router-dom";
import { ContainerForm } from "../components/ContainerForm";
import { useCreateContainer } from "../hooks/useCreateContainer";

export function ContainerCreate() {
  const navigate = useNavigate();
  const { create, loading } = useCreateContainer({
    emitToasts: true,
  });

  return (
    <ContainerForm
      loading={loading}
      onSubmit={async (values) => {
        const ok = await create(values);
        if (ok) navigate("/container/list");
      }}
      submitText="Crear contenedor"
      title="Registrar contenedor"
    />
  );
}