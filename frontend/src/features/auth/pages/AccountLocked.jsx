import { Center, Stack, Heading, Text, Box } from "@chakra-ui/react";
import { LiaTimesCircleSolid } from "react-icons/lia";
import { Button } from "@chakra-ui/react";
import { Link, useNavigate } from "react-router-dom";

export default function AccountLocked() {
  const navigate = useNavigate();
  return (
    <Box as="main" w="100vw" minH="100svh" position="relative">
      <Box position="fixed" inset="0" bg="#e6f4ea" zIndex={0} />
      <Center minH="100svh" position="relative" zIndex={1}>
        <Stack
          boxShadow="md"
          bg="white"
          p={10}
          rounded={"md"}
          w={500}
          align="center"
          spacing={8}
        >
          <LiaTimesCircleSolid size={120} color="#d32f2f" />
          <Heading color="#d32f2f" textAlign="center">
            Cuenta bloqueada
          </Heading>
          <Text fontSize="lg" color="gray.700" textAlign="center">
            Esta cuenta está bloqueada por demasiados intentos fallidos.<br />
            Si crees que esto es un error, contacta al administrador.
          </Text>
          <Stack direction="column" gap={1}>
            <Button
              variant="solid"
              colorPalette="teal"
              onClick={() => navigate("/login")}
              px={2}
            >
              Volver al inicio de sesión
            </Button>
            <Button
              variant="link"
              color="#009688"
              onClick={() => navigate("/account-recovery")}
            >
              Recuperar cuenta
            </Button>
          </Stack>
        </Stack>
      </Center>
    </Box>
  );
}