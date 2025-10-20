import { Box } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import { useCreateUser } from "../hooks/useCreateUser";
import { UserForm } from "../components/UserForm";

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
      py={14}
    >
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
