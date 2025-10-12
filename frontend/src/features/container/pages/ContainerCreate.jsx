import { Box } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import { ContainerForm } from "../components/ContainerForm";
import { useCreateContainer } from "../hooks/useCreateContainer";

export function ContainerCreate() {
  const navigate = useNavigate();
  const { create, loading } = useCreateContainer({
    emitToasts: true,
  });

  return (
    <Box
      h="100vh"
      overflowY="auto"
      bg="gray.50" 
      px={4}
      py={14}
    >
      <ContainerForm
        loading={loading}
        onSubmit={async (values) => {
          const ok = await create(values);
          if (ok) navigate("/container/list");
        }}
        submitText="Crear contenedor"
        title="Registrar contenedor"
      />
    </Box>
  );
}
