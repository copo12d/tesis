import { Box, Button } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import { useCreateContainerType } from "../hooks/useCreateContainerType";
import { ContainerTypeForm } from "../components/ContainerTypeForm";
import { Link } from "react-router-dom";

export function ContainerTypeCreate() {
  const navigate = useNavigate();
  const { createContainerType, loading } = useCreateContainerType();

  const initialValues = { name: "", description: "" };

  const handleSubmit = async (form) => {
    const ok = await createContainerType(form);
    if (ok) navigate("/container-type/list");
  };

  return (
    <Box
      h="100vh"
      overflowY="auto"
      bg="gray.50"
      px={4}
      py={14}
    >
      <Box pt={4} mt={-14}>
        <Link to="/container-type/list">
          <Button variant="link" color="teal.700" size="sm">
            Ir al listado de tipos de contenedores
          </Button>
        </Link>
      </Box>
      <ContainerTypeForm
        initialValues={initialValues}
        loading={loading}
        onSubmit={handleSubmit}
        submitText="Registrar"
        title="Registrar tipo de contenedor"
      />
    </Box>
  );
}
