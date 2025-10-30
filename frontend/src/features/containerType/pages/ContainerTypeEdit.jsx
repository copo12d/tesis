import { useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useContainerType } from "../hooks/useContainerType";
import { ContainerTypeForm } from "../components/ContainerTypeForm";
import { Box, Spinner, Stack, Text, Button } from "@chakra-ui/react";
import { Link } from "react-router-dom";

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
    <>
      
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
    </>
  );
}
