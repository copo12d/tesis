import { useState } from "react";
import { useRegister } from "../hooks/useRegister";
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
import { LiaUser, LiaLockSolid, LiaEnvelope, LiaIdCard } from "react-icons/lia";

export function Register() {
  const [fullName, setFullName] = useState("");
  const [userName, setUserName] = useState("");
  const [password, setPassword] = useState("");
  const [email, setEmail] = useState("");
  const { register, loading, error } = useRegister();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    await register(fullName, userName, password, email );
  };

  return (
    <Center h="100vh" bg="purple.200">
      <Stack boxShadow="md" bg="whiteAlpha.900" p={10} rounded={"md"} w={600}>
        <Heading color={"blackAlpha.900"}>Registro</Heading>
        <Text fontSize={"lg"} color={"gray.600"}>
          Crea tu cuenta
        </Text>
        <form onSubmit={handleSubmit}>
          <Stack spacing={6} my={4}>
            <Field.Root required>
              <Field.Label htmlFor="fullName" color={"black"}>
                Nombre completo
              </Field.Label>
              <InputGroup startAddon={<LiaIdCard />} startAddonProps={{ bg: "purple.500", px: 3 }}>
                <Input
                  id="fullName"
                  name="fullName"
                  placeholder="Nombre completo"
                  value={fullName}
                  onChange={e => setFullName(e.target.value)}
                  size="lg"
                  color={"blackAlpha.900"}
                  w="100%"
                  _placeholder={{ pl: 2 }}
                />
              </InputGroup>

              <Field.Label htmlFor="userName" color={"black"}>
                Nombre de usuario
              </Field.Label>
              <InputGroup startAddon={<LiaUser />} startAddonProps={{ bg: "purple.500", px: 3 }}>
                <Input
                  id="userName"
                  name="userName"
                  placeholder="Nombre de usuario"
                  value={userName}
                  onChange={e => setUserName(e.target.value)}
                  size="lg"
                  color={"blackAlpha.900"}
                  w="100%"
                  _placeholder={{ pl: 2 }}
                />
              </InputGroup>

              <Field.Label htmlFor="email" color={"black"}>
                Email
              </Field.Label>
              <InputGroup startAddon={<LiaEnvelope />} startAddonProps={{ bg: "purple.500", px: 3 }}>
                <Input
                  id="email"
                  name="email"
                  type="email"
                  placeholder="Correo electrónico"
                  value={email}
                  onChange={e => setEmail(e.target.value)}
                  size="lg"
                  color={"blackAlpha.900"}
                  w="100%"
                  _placeholder={{ pl: 2 }}
                />
              </InputGroup>

              <Field.Label htmlFor="password" color={"black"}>
                Contraseña
              </Field.Label>
              <InputGroup startAddon={<LiaLockSolid />} startAddonProps={{ bg: "purple.500", px: 3 }}>
                <Input
                  id="password"
                  name="password"
                  type="password"
                  placeholder="Contraseña"
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  size="lg"
                  color={"blackAlpha.900"}
                  w="100%"
                  _placeholder={{ pl: 2 }}
                  
                />
              </InputGroup>
            </Field.Root>
            <Button
              type="submit"
              colorPalette={"purple"}
              size="lg"
              loading={loading}
              loadingText="Registrando..."
              spinnerPlacement="end"
              marginTop={4}
              _hover={{ bg: "purple.300" }}
            >
              Registrarse
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
            <span>¿Ya tienes una cuenta? </span>
            <Button
              variant="link"
              colorPalette={"purple"}
              onClick={() => navigate("/login")}
            >
              Iniciar sesión
            </Button>
          </Text>
        </Stack>
      </Stack>
    </Center>
  );
}