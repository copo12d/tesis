import { useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useContainerType } from "../hooks/useContainerType";
import { ContainerTypeForm } from "../components/ContainerTypeForm";
import { Box, Spinner, Stack, Text } from "@chakra-ui/react";

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

  const initialValues = containerType || { name: "", description: "" };

  const handleSubmit = async (form) => {
    const ok = await updateContainerType(form);
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
      {loading ? (
        <Stack align="center" p={8}>
          <Spinner />
          <Text>Cargando tipo de contenedor...</Text>
        </Stack>
      ) : error ? (
        <Box color="red.500" p={4}>
          Error al cargar tipo de contenedor.
        </Box>
      ) : !containerType ? (
        <Box p={4}>
          Tipo de contenedor no encontrado.
        </Box>
      ) : (
        <ContainerTypeForm
          initialValues={initialValues}
          loading={loading || updating}
          onSubmit={handleSubmit}
          submitText="Actualizar"
          title="Editar tipo de contenedor"
        />
      )}
    </Box>
  );
}
