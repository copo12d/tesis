import { useState, useContext, useCallback, lazy, Suspense } from "react";
import { useNavigate } from "react-router-dom";
import {
  Center,
  Heading,
  Stack,
  Text,
  Button,
  Box,
} from "@chakra-ui/react";
// import { LiaUser, LiaLockSolid } from "react-icons/lia"; // <-- lazy abajo
import { useAuth } from "../hooks/useAuth";
import AuthContext from "@/context/AuthContext";
import { IconInputField } from "@/components/ui/IconInputField";

// Lazy-load de iconos para reducir JS inicial
const LiaUser = lazy(() =>
  import("react-icons/lia").then(m => ({ default: m.LiaUser }))
);
const LiaLockSolid = lazy(() =>
  import("react-icons/lia").then(m => ({ default: m.LiaLockSolid }))
);

export function Login() {
  const [userName, setUserName] = useState("");
  const [password, setPassword] = useState("");

  const { loginRequest, loading, error, setError } = useAuth();
  const { login } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleInputChange = useCallback(
    (setter) => (e) => {
      setter(e.target.value);
      if (error) setError("");
    },
    [error, setError]
  );

  const handleSubmit = useCallback(async (e) => {
    e.preventDefault();
    const result = await loginRequest(userName, password);
    if (result.success) {
      login(result.accessToken, result.refreshToken);
      navigate("/");
    } else if (result.error && result.error.toLowerCase().includes("bloqueada")) {
      navigate("/account-locked");
    }
  }, [loginRequest, userName, password, login, navigate]);

  return (
    // Capa raíz que siempre ocupa todo el viewport
    <Box as="main" w="100vw" minH="100svh" position="relative">
      {/* Fondo fijo que cubre toda la pantalla, independiente del padre */}
      <Box position="fixed" inset="0" bg="#e6f4ea" zIndex={0} />

      {/* Tu contenido tal cual, por encima del fondo */}
      <Center minH="100svh" position="relative" zIndex={1}>
        <Stack boxShadow="md" bg="white" p={10} rounded="md" w={600} spacing={6}>
          <Heading color="#00695c">Iniciar Sesión</Heading>
          <Text fontSize="lg" color="#00695c">
            Por favor, ingresa tus credenciales
          </Text>

          <form onSubmit={handleSubmit}>
            <Stack spacing={6} mt={4}>
              <IconInputField
                label="Nombre de usuario"
                name="userName"
                value={userName}
                onChange={handleInputChange(setUserName)}
                placeholder="Nombre de usuario"
                icon={
                  <Suspense fallback={null}>
                    <LiaUser />
                  </Suspense>
                }
                iconProps={{ bg: "#009688", px: 3 }}
                required
                inputProps={{
                  w: "100%",
                  pl: 2,
                  _placeholder: { pl: 2 },
                  autoComplete: "username",
                }}
              />

              <IconInputField
                label="Contraseña"
                name="password"
                type="password"
                value={password}
                onChange={handleInputChange(setPassword)}
                placeholder="Contraseña"
                icon={
                  <Suspense fallback={null}>
                    <LiaLockSolid />
                  </Suspense>
                }
                iconProps={{ bg: "#009688", px: 3 }}
                required
                inputProps={{
                  w: "100%",
                  pl: 2,
                  _placeholder: { pl: 2 },
                  css: { "--error-color": "red" },
                  autoComplete: "current-password",
                }}
              />

              <Button
                type="submit"
                bg="#009688"
                color="white"
                size="lg"
                loading={loading}
                loadingText="Iniciando..."
                spinnerPlacement="end"
                _hover={{ bg: "#00695c" }}
                mt={2}
              >
                Iniciar Sesión
              </Button>
            </Stack>
          </form>

          <Stack justify="center" spacing={4} color="#00695c">
            <Text
              as="div"
              textAlign="center"
              display="flex"
              alignItems="center"
              justifyContent="center"
            >
              <span>No tienes una cuenta?</span>
              <Button
                variant="link"
                color="#009688"
                onClick={() => navigate("/register")}
                margin={0}
              >
                Regístrate
              </Button>
            </Text>
            <Button
              variant="link"
              color="#009688"
              onClick={() => navigate("/forgot-password")}
            >
              Olvidaste tu contraseña?
            </Button>
          </Stack>
        </Stack>
      </Center>
    </Box>
  );
}
