import { Box, Button } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import { useCreateUser } from "../hooks/useCreateUser";
import { UserForm } from "../components/UserForm";
import { Link } from "react-router-dom";

export function UserCreate() {
  const navigate = useNavigate();
  const { create, loading, error } = useCreateUser({
    redirectOnSuccess: "/users/all",
    emitToasts: true,
  });

  return (
    <Box
      h="100vh"
      overflowY="auto"
      bg="gray.50"
      px={4}
      py={6}
    >
      <Box>
        <Link to="/users/all">
          <Button variant="link" color="teal.700" size="sm">
            Volver al listado de usuarios
          </Button>
        </Link>
      </Box>
      <UserForm
        loading={loading}
        onSubmit={async (values) => {
          const ok = await create(values);
          if (ok && error) navigate("/users/all");
        }}
        submitText="Crear usuario"
        title="Registrar usuario"
      />
    </Box>
  );
}
