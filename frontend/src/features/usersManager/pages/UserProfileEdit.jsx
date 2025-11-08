import { useContext, useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import {
  Box,
  Button,
  Stack,
  Skeleton,
  SkeletonText,
  Text,
} from "@chakra-ui/react";
import AuthContext from "@/context/AuthContext";
import { UsersAPI } from "../api/user.api";
import { UserForm } from "../components/UserForm";
import { useUserProfileFromToken } from "../hooks/useUserProfileFromToken";
import { ConfirmDialog } from "@/components/ConfirmDialog";

export function UserProfileEdit() {
  const { user, logout } = useContext(AuthContext)
  const navigate = useNavigate();

  const { profile, loading, error, refresh } = useUserProfileFromToken();
  const [saving, setSaving] = useState(false);

  // Estado para el diálogo y countdown
  const [showLogoutConfirm, setShowLogoutConfirm] = useState(false);
  const [countdown, setCountdown] = useState(5);
  const timerRef = useRef(null);

  const startCountdown = () => {
    clearInterval(timerRef.current);
    setCountdown(5);
    timerRef.current = setInterval(() => {
      setCountdown((c) => {
        if (c <= 1) {
          clearInterval(timerRef.current);
          forceLogout();
          return 0;
        }
        return c - 1;
      });
    }, 1000);
  };

  const forceLogout = () => {
    // Cerrar sesión y redirigir
    try {
      if (logout) logout();
    } finally {
      navigate("/login", { replace: true });
    }
  };

  useEffect(() => {
    return () => {
      clearInterval(timerRef.current);
    };
  }, []);

  const handleSubmit = async (form) => {
    if (!profile?.id) return;
    setSaving(true);
    try {
      await UsersAPI.updateProfile(profile.id, form);
      await refresh();
      // Mostrar diálogo y lanzar countdown
      setShowLogoutConfirm(true);
      startCountdown();
    } catch (err) {
      console.error("Error al actualizar perfil:", err);
    } finally {
      setSaving(false);
    }
  };

  return (
    <Box h="100vh" overflowY="auto" bg="gray.50" px={4} py={6}>
      <Box mb={4}>
        <Button
          variant="link"
          color="teal.700"
          size="sm"
          onClick={() => navigate(-1)}
        >
          Volver
        </Button>
      </Box>

      {loading || error || !profile ? (
        <Stack spacing={6}>
          <Box bg="white" boxShadow="md" p={6} borderRadius="md">
            <Skeleton height="24px" mb={4} />
            <SkeletonText noOfLines={4} spacing="4" />
            <Skeleton height="44px" mt={6} borderRadius="md" />
          </Box>
        </Stack>
      ) : (
        <UserForm
          initialValues={profile}
          loading={saving}
          onSubmit={handleSubmit}
          submitText="Guardar"
          title="Editar perfil"
          includeRole={false}
          fields={["fullName", "userName"]}
        />
      )}

      <ConfirmDialog
        isOpen={showLogoutConfirm}
        onOpenChange={(open) => {
            // Evitar que el usuario cierre antes del logout automático
            if (!open && countdown > 0) {
              // re-abre si intenta cerrar antes de tiempo
              setShowLogoutConfirm(true);
              return;
            }
            setShowLogoutConfirm(open);
          }
        }
        title="Perfil actualizado"
        description={`Tu sesión se cerrará para aplicar los cambios. Serás redirigido al inicio de sesión en ${countdown} segundos.`}
        confirmText="Cerrar sesión ahora"
        onConfirm={forceLogout}
        loading={saving}
        confirmColorPalette="teal"
        hideCloseButton
      />
    </Box>
  );
}
