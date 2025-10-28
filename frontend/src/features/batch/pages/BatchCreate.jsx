import { useNavigate } from "react-router-dom";
import { useRegisterBatch } from "../hooks/useRegisterBatch";
import { BatchForm } from "../components/BatchForm";
import { Box, Button } from "@chakra-ui/react";
import { Link } from "react-router-dom";

export function BatchCreate() {
  const navigate = useNavigate();
  const { registerBatch, loading } = useRegisterBatch();

  return (
    <Box h="100vh" overflowY="auto" bg="gray.50" px={4} py={4}>
      <Box>
        <Link to="/batch/list">
          <Button variant="link" color="teal.700" size="sm">
            Volver al listado de lotes
          </Button>
        </Link>
      </Box>
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
