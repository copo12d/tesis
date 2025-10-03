import { useParams, useNavigate } from "react-router-dom";
import { useUpdateUser } from "../hooks/useUpdateUser";
import { UserForm } from "../components/UserForm";
import { Spinner, Stack, Text, Box } from "@chakra-ui/react";

export function UserEdit() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user, loading, updating, error, updateUser } = useUpdateUser(id);

  if (loading) {
    return (
      <Stack align="center" p={8}>
        <Spinner />
        <Text>Cargando usuario...</Text>
      </Stack>
    );
  }

  if (error) {
    return (
      <Box color="red.500" p={4}>
        Error al cargar usuario.
      </Box>
    );
  }

  if (!user) {
    return (
      <Box p={4}>
        Usuario no encontrado.
      </Box>
    );
  }

  return (
    <UserForm
      initialValues={user}
      isLoading={updating}
      onSubmit={async (values) => {
        const ok = await updateUser(values);
        if (ok) navigate("/users/all");
      }}
      submitText="Actualizar usuario"
      title="Editar usuario"
    />
  );
}