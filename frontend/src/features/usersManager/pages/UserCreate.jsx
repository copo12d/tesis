import { useNavigate } from "react-router-dom";
import { useCreateUser } from "../hooks/useCreateUser";
import { UserForm } from "../components/UserForm";

export function UserCreate() {
  const navigate = useNavigate();
  const { create, loading } = useCreateUser({
    redirectOnSuccess: "/users/all",
    emitToasts: true,
  });

  return (
    <UserForm
      loading={loading}
      onSubmit={async (values) => {
        const ok = await create(values);
        if (ok) navigate("/users/all");
      }}
      submitText="Crear usuario"
      title="Registrar usuario"
    />
  );
}