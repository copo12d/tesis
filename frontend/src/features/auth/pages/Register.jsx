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
  const { register, loading } = useRegister();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    await register(fullName, userName, password, email );
  };

  return (
    <Center h="100vh" bg="#e6f4ea">
      <Stack boxShadow="md" bg="white" p={10} rounded={"md"} w={600}>
        <Heading color={"#00695c"}>Registro</Heading>
        <Text fontSize={"lg"} color={"#00695c"}>
          Crea tu cuenta
        </Text>
        <form onSubmit={handleSubmit}>
          <Stack spacing={6} my={4}>
            <Field.Root required>
              <Field.Label htmlFor="fullName" color={"black"}>
                Nombre completo
              </Field.Label>
              <InputGroup startAddon={<LiaIdCard />} startAddonProps={{ bg: "#009688", px: 3 }}>
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
                  pl={2}
                />
              </InputGroup>

              <Field.Label htmlFor="userName" color={"black"}>
                Nombre de usuario
              </Field.Label>
              <InputGroup startAddon={<LiaUser />} startAddonProps={{ bg: "#009688", px: 3 }}>
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
                  pl={2}
                />
              </InputGroup>

              <Field.Label htmlFor="email" color={"black"}>
                Email
              </Field.Label>
              <InputGroup startAddon={<LiaEnvelope />} startAddonProps={{ bg: "#009688", px: 3 }}>
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
                  pl={2}
                />
              </InputGroup>

              <Field.Label htmlFor="password" color={"black"}>
                Contraseña
              </Field.Label>
              <InputGroup startAddon={<LiaLockSolid />} startAddonProps={{ bg: "#009688", px: 3 }}>
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
              loadingText="Registrando..."
              spinnerPlacement="end"
              marginTop={4}
              _hover={{ bg: "#00695c" }}
            >
              Registrarse
            </Button>
          </Stack>
        </form>
  <Stack justify={"center"} spacing={4} color={"#00695c"}>
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
              color="#009688"
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