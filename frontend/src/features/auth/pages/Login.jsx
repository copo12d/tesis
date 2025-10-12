import { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import {
  Center,
  Heading,
  Stack,
  Text,
  Button,
} from "@chakra-ui/react";
import { LiaUser, LiaLockSolid } from "react-icons/lia";
import { useAuth } from "../hooks/useAuth";
import AuthContext from "../../../context/Authcontext";
import { IconInputField } from "@/components/ui/IconInputField";

export function Login() {
  const [userName, setUserName] = useState("");
  const [password, setPassword] = useState("");

  const { loginRequest, loading, error, setError } = useAuth();
  const { login } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleInputChange = (setter) => (e) => {
    setter(e.target.value);
    if (error) setError("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const result = await loginRequest(userName, password);
    if (result.success) {
      login(result.accessToken, result.refreshToken);
      navigate("/");
    } else if (
      result.error &&
      result.error.toLowerCase().includes("bloqueada")
    ) {
      navigate("/account-locked");
    }
  };

  return (
    <Center h="100vh" bg="#e6f4ea">
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
              icon={<LiaUser />}
              iconProps={{ bg: "#009688", px: 3 }}
              required
              inputProps={{
                w: "100%",
                pl: 2,
                _placeholder: { pl: 2 },
              }}
            />

            <IconInputField
              label="Contraseña"
              name="password"
              type="password"
              value={password}
              onChange={handleInputChange(setPassword)}
              placeholder="Contraseña"
              icon={<LiaLockSolid />}
              iconProps={{ bg: "#009688", px: 3 }}
              required
              inputProps={{
                w: "100%",
                pl: 2,
                _placeholder: { pl: 2 },
                css: { "--error-color": "red" },
              }}
            />

            <Button
              type="submit"
              bg="#009688"
              color="white"
              size="lg"
              isLoading={loading}
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
            display="inline-flex"
            alignItems="center"
            justifyContent="center"
          >
            <span>No tienes una cuenta? </span>
            <Button
              variant="link"
              color="#009688"
              onClick={() => navigate("/register")}
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
  );
}
