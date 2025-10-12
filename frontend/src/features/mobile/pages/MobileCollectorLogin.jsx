import { useState, useContext } from "react";
import { useAuth } from "../../auth/hooks/useAuth";
import AuthContext from "../../../context/Authcontext";
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

export default function MobileCollectorLogin() {
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
      navigate("/mobile/recolector");
    } else if (
      result.error &&
      result.error.toLowerCase().includes("bloqueada")
    ) {
      navigate("/account-locked");
    }
  };

  return (
    <Center h="100vh" bg="#e6f4ea">
      <Stack boxShadow="md" bg="white" p={8} rounded={"md"} w="95vw" maxW={350}>
        <Heading color={"#00695c"} size="md" textAlign="center">
          Ingreso recolector
        </Heading>
        <Text fontSize={"md"} color={"#00695c"} textAlign="center">
          Ingresa tus credenciales de recolector
        </Text>
        <form onSubmit={handleSubmit}>
          <Stack spacing={6} my={4}>
            <Field.Root required>
              <Field.Label htmlFor="userName" color={"black"}>
                Nombre de usuario
              </Field.Label>
              <InputGroup
                startAddon={<LiaUser />}
                startAddonProps={{ bg: "#009688", px: 3 }}
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
                  autoFocus
                />
              </InputGroup>

              <Field.Label htmlFor="password" color={"black"}>
                Contraseña
              </Field.Label>
              <InputGroup
                startAddon={<LiaLockSolid />}
                startAddonProps={{ bg: "#009688", px: 3 }}
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
                />
              </InputGroup>
            </Field.Root>
            <Button
              type="submit"
              bg="#009688"
              color="white"
              size="lg"
              isLoading={loading}
              loadingText="Ingresando..."
              marginTop={4}
              spinnerPlacement="end"
              _hover={{ bg: "#00695c" }}
            >
              Ingresar
            </Button>
          </Stack>
        </form>
        <Text mt={2} textAlign="center" fontSize="sm">
          <Button
            variant="link"
            color="#009688"
            onClick={() => navigate("/mobile")}
          >
            Volver
          </Button>
        </Text>
      </Stack>
    </Center>
  );
}