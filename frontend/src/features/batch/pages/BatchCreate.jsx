import { useNavigate } from "react-router-dom";
import { useRegisterBatch } from "../hooks/useRegisterBatch";
import { BatchForm } from "../components/BatchForm";

export function BatchCreate() {
  const navigate = useNavigate();
  const { registerBatch, loading } = useRegisterBatch();

  return (
    <BatchForm
      loading={loading}
      onSubmit={async (values) => {
        const ok = await registerBatch(values);
        if (ok) navigate("/batch/list")
      }}
      submitText="Registrar lote"
      title="Registrar nuevo lote"
    />
  );
}