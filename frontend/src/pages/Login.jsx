import { useState } from "react";
import { useAuth } from "../hooks/useAuth";
import AuthContext from "../context/Authcontext";
import { useContext } from "react";
import { useNavigate } from "react-router-dom";
import {
  Center,
  Heading,
  Stack,
  Text,
  Button,
  Input,
  InputGroup,
  Field,
} from "@chakra-ui/react";
import { LiaUser, LiaLockSolid } from "react-icons/lia";

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
    }
  };

  return (
    <Center h="100vh" bg="purple.200">
      <Stack boxShadow="md" bg="whiteAlpha.900" p={10} rounded={"md"} w={600}>
        <Heading color={"blackAlpha.900"}>Iniciar Sesión</Heading>
        <Text fontSize={"lg"} color={"gray.600"}>
          Por favor, ingresa tus credenciales
        </Text>
        <form onSubmit={handleSubmit}>
          <Stack spacing={6} my={4}>
            <Field.Root required >
              <Field.Label htmlFor="userName" color={"black"}>
                Nombre de usuario
              </Field.Label>
              <InputGroup
                startAddon={<LiaUser />}
                startAddonProps={{ bg: "purple.500", px: 3 }}
              >
                <Input
                  id="userName"
                  name="userName"
                  placeholder="Nombre de usuario"
                  colorPalette={"black"}
                  value={userName}
                  onChange={handleInputChange(setUserName)}
                  size="lg"
                  color={"blackAlpha.900"}
                  w="100%"
                  _placeholder={{ pl: 2 }}
                />
              </InputGroup>

              <Field.Label htmlFor="password" color={"black"}>
                Contraseña
              </Field.Label>
              <InputGroup
                startAddon={<LiaLockSolid />}
                startAddonProps={{ bg: "purple.500", px: 3 }}
              >
                <Input
                  id="password"
                  name="password"
                  type="password"
                  placeholder="Contraseña"
                  value={password}
                  onChange={handleInputChange(setPassword)}
                  size="lg"
                  color={"blackAlpha.900"}
                  w="100%"
                  _placeholder={{ pl: 2 }}
                  css={{ "--error-color": "red" }}
                  
                />
              </InputGroup>
            </Field.Root>
            <Button
              type="submit"
              colorPalette={"purple"}
              size="lg"
              loading={loading}
              loadingText="Iniciando..."
              marginTop={4}
              spinnerPlacement="end"
            >
              Iniciar Sesión
            </Button>
          </Stack>
        </form>
        <Stack justify={"center"} spacing={4} color={"gray.600"}>
          <Text
            as={"div"}
            textAlign={"center"}
            display={"inline-flex"}
            alignItems={"center"}
            justifyContent={"center"}
          >
            <span>No tienes una cuenta? </span>
            <Button
              variant="link"
              colorPalette={"purple"}
              onClick={() => navigate("/register")}
            >
              Regístrate
            </Button>
          </Text>
          <Button
            variant="link"
            colorPalette={"purple"}
            onClick={() => navigate("/olvidaste-contraseña")}
          >
            Olvidaste tu contraseña?
          </Button>
        </Stack>
      </Stack>
    </Center>
  );
}
