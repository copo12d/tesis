import { useState, useContext } from "react";
import AuthContext from "@/context/AuthContext";
import { useNavigate, useLocation } from "react-router-dom";
import {
  Center,
  Heading,
  Stack,
  Text,
  Button,
  Input,
  InputGroup,
  Field,
  Box,
} from "@chakra-ui/react";
import { LiaUser, LiaLockSolid } from "react-icons/lia";
import { useMobileCollectorLogin } from "../hooks/useMobileCollectorLogin";

export default function MobileCollectorLogin() {
  const [userName, setUserName] = useState("");
  const [password, setPassword] = useState("");

  const { handleLogin, loading, error, setError } = useMobileCollectorLogin();
  const { login } = useContext(AuthContext);
  const navigate = useNavigate();
  const location = useLocation();
  const redirectTo = location.state?.from || "/mobile/containers/collect";

  const handleInputChange = (setter) => (e) => {
    setter(e.target.value);
    if (error) setError("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const result = await handleLogin(userName, password);
    if (result.success) {
      login(result.accessToken, result.refreshToken);
      navigate(redirectTo, { replace: true });
    }
  };

  return (
    <Box as="main" w="100vw" minH="100svh" position="relative">
      <Box position="fixed" inset="0" bg="#e6f4ea" zIndex={0} />
      <Center minH="100svh" position="relative" zIndex={1} px={4}>
        <Box w="100%" maxW="400px">
          <Stack
            boxShadow="md"
            bg="white"
            p={4}
            rounded="md"
            w="100%"
            spacing={6}
          >
            <Heading color="#00695c" size="md" textAlign="center">
              Ingreso recolector
            </Heading>
            <Text fontSize="md" color="#00695c" textAlign="center">
              Ingresa tus credenciales de recolector
            </Text>
            <form onSubmit={handleSubmit}>
              <Stack spacing={6} my={2}>
                <Field.Root required>
                  <Field.Label htmlFor="userName" color="black">
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
                      value={userName}
                      onChange={handleInputChange(setUserName)}
                      size="lg"
                      color="blackAlpha.900"
                      autoFocus
                    />
                  </InputGroup>

                  <Field.Label htmlFor="password" color="black" mt={2}>
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
                      color="blackAlpha.900"
                    />
                  </InputGroup>
                </Field.Root>
                <Button
                  type="submit"
                  bg="#009688"
                  color="white"
                  size="lg"
                  loading={loading}
                  loadingText="Ingresando..."
                  spinnerPlacement="end"
                  _hover={{ bg: "#00695c" }}
                  w="100%"
                >
                  Ingresar
                </Button>
              </Stack>
            </form>
            <Text mt={0} textAlign="center" fontSize="sm">
              <Button
                variant="link"
                color="#009688"
                onClick={() => navigate(-1)}
              >
                Volver
              </Button>
            </Text>
          </Stack>
        </Box>
      </Center>
    </Box>
  );
}