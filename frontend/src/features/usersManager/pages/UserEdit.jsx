import { useParams, useNavigate } from "react-router-dom";
import { useUpdateUser } from "../hooks/useUpdateUser";
import { UserForm } from "../components/UserForm";
import { Spinner, Stack, Text, Box } from "@chakra-ui/react";

export function UserEdit() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user, loading, updating, error, updateUser } = useUpdateUser(id);

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
          <Text>Cargando usuario...</Text>
        </Stack>
      ) : error ? (
        <Box color="red.500" p={4}>
          Error al cargar usuario.
        </Box>
      ) : !user ? (
        <Box p={4}>
          Usuario no encontrado.
        </Box>
      ) : (
        <UserForm
          initialValues={user}
          loading={updating}
          onSubmit={async (values) => {
            const ok = await updateUser(values);
            if (ok) navigate("/users/all");
          }}
          submitText="Actualizar usuario"
          title="Editar usuario"
        />
      )}
    </Box>
  );
}
