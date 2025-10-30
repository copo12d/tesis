import { useParams, useNavigate } from "react-router-dom";
import { useUpdateUser } from "../hooks/useUpdateUser";
import { UserForm } from "../components/UserForm";
import { Spinner, Stack, Text, Box, Button } from "@chakra-ui/react";
import { Link } from "react-router-dom";

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
      py={3}
    >
       <Box>
        <Link to="/users/all">
          <Button variant="link" color="teal.700" size="sm">
            Volver al listado de usuarios
          </Button>
        </Link>
      </Box>
      {loading ? (
        <Stack align="center" p={8}>
          <Spinner />
          <Text>Cargando usuario...</Text>
        </Stack>
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
          // Solo muestra los campos principales, sin password ni repeatPassword
          fields={["fullName", "userName", "email", "role"]}
        />
      )}
    </Box>
  );
}
