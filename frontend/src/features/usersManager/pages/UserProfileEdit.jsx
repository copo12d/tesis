import { useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Checkbox, Box, Text } from "@chakra-ui/react";
import AuthContext from "@/context/AuthContext";
import { UsersAPI } from "../api/user.api";
import { UserForm } from "../components/UserForm";

export function UserProfileEdit() {
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();

  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const handleSubmit = async (form) => {
    setSaving(true);
    try {
      const ok = await UsersAPI.updateProfile(profile.id, form);
      if (ok) navigate("/dashboard");
    } catch (error) {
      console.error("Error al actualizar perfil:", error);
    } finally {
      setSaving(false);
    }
  };

  useEffect(() => {
    async function fetchProfile() {
      try {
        if (!user?.sub) return;

        // Paso 1: obtener el ID por username
        const { data: idResponse } = await UsersAPI.getIdByUsername(user.sub);
        const userId = idResponse?.data;
        console.log("Obtenido userId:", userId);
        if (!userId) throw new Error("No se pudo obtener el ID");

        // Paso 2: obtener el perfil por ID
        const { data: profileResponse } = await UsersAPI.getProfile(userId);
        const userData = profileResponse?.data;
        if (!userData) throw new Error("No se pudo obtener el perfil");
        console.log("Obtenido userData:", userData);

        setProfile(userData);
      } catch (error) {
        console.error("Error al cargar perfil:", error);
      } finally {
        setLoading(false);
      }
    }

    fetchProfile();
  }, [user?.sub]);

  if (loading || !profile) return <Text px={6}>Cargando perfil...</Text>;

  return (
    <>
      <UserForm
        initialValues={profile}
        loading={loading || saving}
        onSubmit={handleSubmit}
        submitText="Guardar"
        title="Editar perfil"
        includeRole={false}
        fields={["fullName", "userName", "email"]}
      />

      <Box mt={4} px={6}>
        <Text fontWeight="bold" mb={2}>Estado de verificación</Text>
        <Checkbox.Root isDisabled isChecked={profile.verified}>
          <Checkbox.HiddenInput />
          <Checkbox.Control>
            <Checkbox.Indicator />
          </Checkbox.Control>
          <Checkbox.Label>Cuenta verificada</Checkbox.Label>
        </Checkbox.Root>
      </Box>

    </>
  );
}
