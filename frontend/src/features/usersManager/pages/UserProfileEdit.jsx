import { UserForm } from "../components/UserForm";
import { useParams, useNavigate } from "react-router-dom";
import { useUserProfile } from "../hooks/useUserprofile";


export function UserProfileEdit() {
  const { id } = useParams(); // <-- Consigue el id de la URL
  const { profile, loading, saving, updateProfile } = useUserProfile(id);
  const navigate = useNavigate();

  const handleSubmit = async (form) => {
    const ok = await updateProfile(form);
    if (ok) navigate("/dashboard");
  };

  return (
    <UserForm
      initialValues={profile}
      loading={loading || saving}
      onSubmit={handleSubmit}
      submitText="Guardar"
      title="Editar perfil"
      includeRole={false}
      fields={["fullName", "userName"]}
    />
  );
}