import { useState } from "react";
import { useRegister } from "../hooks/useRegister";
import { useNavigate } from "react-router-dom";
import {
  Center,
  Heading,
  Stack,
  Text,
  Button,
  Box, // <- agregado
} from "@chakra-ui/react";
import {
  LiaUser,
  LiaLockSolid,
  LiaEnvelope,
  LiaIdCard,
} from "react-icons/lia";
import { IconInputField } from "@/components/ui/IconInputField"; 
import toast from "react-hot-toast";

export function Register() {
  const [fullName, setFullName] = useState("");
  const [userName, setUserName] = useState("");
  const [password, setPassword] = useState("");
  const [email, setEmail] = useState("");
  const [repeatPassword, setRepeatPassword] = useState("");

  const { register, loading } = useRegister();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (repeatPassword !== password) {
      toast.error("Las contraseñas no coinciden");
      return;
    }
    await register(fullName, userName, password, email);
  };

  

  return (
    <Box as="main" w="100vw" minH="100svh" position="relative">
      <Box position="fixed" inset="0" bg="#e6f4ea" zIndex={0} />
      <Center minH="100svh" position="relative" zIndex={1}>
        <Stack boxShadow="md" bg="white" px={10} py={5} rounded="md" w={600} spacing={6}>
          <Heading color="#00695c">Registro</Heading>
          <Text fontSize="lg" color="#00695c">
            Crea tu cuenta
          </Text>

          <form onSubmit={handleSubmit}>
            <Stack spacing={6} mt={4}>
              <IconInputField
                label="Nombre completo"
                name="fullName"
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                placeholder="Nombre completo"
                icon={<LiaIdCard />}
                iconProps={{ bg: "#009688", px: 3 }}
                required
                inputProps={{
                  w: "100%",
                  pl: 2,
                  _placeholder: { pl: 2 },
                }}
              />

              <IconInputField
                label="Nombre de usuario"
                name="userName"
                value={userName}
                onChange={(e) => setUserName(e.target.value)}
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
                label="Correo electrónico"
                name="email"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="Correo electrónico"
                icon={<LiaEnvelope />}
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
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Contraseña"
                icon={<LiaLockSolid />}
                iconProps={{ bg: "#009688", px: 3 }}
                required
                inputProps={{
                  w: "100%",
                  pl: 2,
                  _placeholder: { pl: 2 },
                }}
              />

              <IconInputField
                label="Repita la Contraseña"
                name="repeatPassword"
                type="password"
                value={repeatPassword}
                onChange={(e) => setRepeatPassword(e.target.value)}
                placeholder="Repita la Contraseña"
                icon={<LiaLockSolid />}
                iconProps={{ bg: "#009688", px: 3 }}
                required
                inputProps={{
                  w: "100%",
                  pl: 2,
                  _placeholder: { pl: 2 },
                }}
              />


              <Button
                type="submit"
                bg="#009688"
                color="white"
                size="lg"
                isLoading={loading}
                loadingText="Registrando..."
                spinnerPlacement="end"
                _hover={{ bg: "#00695c" }}
                mt={2}
              >
                Registrarse
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
    </Box>
  );
}
