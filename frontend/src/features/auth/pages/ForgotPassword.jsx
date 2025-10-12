import {
  Box,
  Button,
  Heading,
  Input,
  Stack,
  Text,
  Center,
  Field,
  InputGroup,
} from "@chakra-ui/react";
import { useState } from "react";
import { Link } from "react-router-dom";
import { LiaUser } from "react-icons/lia";
import { usePasswordRecoveryRequest } from "../hooks/usePasswordRecoveryRequest";

export default function ForgotPassword() {
  const [value, setValue] = useState("");
  const { requestRecovery, loading } = usePasswordRecoveryRequest();

  const handleSubmit = async (e) => {
    e.preventDefault();
    // Decide si es email o username (puedes mejorar esta lógica si lo deseas)
    const isEmail = value.includes("@");
    const payload = isEmail
      ? { userName: "", email: value }
      : { userName: value, email: "" };
    await requestRecovery(payload);
  };

  return (
    <Center h="100vh" bg="#e6f4ea">
      <Stack boxShadow="md" bg="white" p={10} rounded={"md"} w={600}>
        <Heading color={"#00695c"} mb={6} textAlign="center">
          Recuperar contraseña
        </Heading>
        <form onSubmit={handleSubmit}>
          <Stack spacing={6} my={4}>
            <Field.Root required>
              <Field.Label htmlFor="userOrEmail" color={"black"}>
                Usuario o correo electrónico
              </Field.Label>
              <InputGroup
                startAddon={<LiaUser />}
                startAddonProps={{ bg: "#009688", px: 3 }}
              >
                <Input
                  id="userOrEmail"
                  name="userOrEmail"
                  placeholder="Introduce tu usuario o correo"
                  colorPalette={"black"}
                  value={value}
                  onChange={(e) => setValue(e.target.value)}
                  size="lg"
                  color={"blackAlpha.900"}
                  w="100%"
                  _placeholder={{ pl: 2 }}
                  autoFocus
                  pl={2}
                />
              </InputGroup>
            </Field.Root>
            <Button
              type="submit"
              bg="#009688"
              color="white"
              size="lg"
              loading={loading}
              loadingText="Enviando..."
              marginTop={4}
              spinnerPlacement="end"
              _hover={{ bg: "#00695c" }}
            >
              Recuperar contraseña
            </Button>
          </Stack>
        </form>
        <Text mt={4} textAlign="center" fontSize="sm">
          <Link to="/login" style={{ color: "#2b6cb0" }}>
            Volver al inicio de sesión
          </Link>
        </Text>
      </Stack>
    </Center>
  );
}