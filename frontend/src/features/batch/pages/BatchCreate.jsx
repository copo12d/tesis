import { useNavigate } from "react-router-dom";
import { useRegisterBatch } from "../hooks/useRegisterBatch";
import { BatchForm } from "../components/BatchForm";
import { Box } from "@chakra-ui/react";

export function BatchCreate() {
  const navigate = useNavigate();
  const { registerBatch, loading } = useRegisterBatch();

  return (
    <Box h="100vh" overflowY="auto" bg="gray.50" px={4} py={14}>
      <BatchForm
        loading={loading}
        onSubmit={async (values) => {
          const ok = await registerBatch(values);
          if (ok) navigate("/batch/list");
        }}
        submitText="Registrar lote"
        title="Registrar nuevo lote"
      />
    </Box>
  );
}
