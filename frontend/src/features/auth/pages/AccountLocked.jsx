import { Center, Stack, Heading, Text, Box } from "@chakra-ui/react";
import { LiaTimesCircleSolid } from "react-icons/lia";
import { Button } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";

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
          <Button
            colorScheme="teal"
            variant="solid"
            onClick={() => navigate("/login")}
          >
            Volver al inicio de sesión
          </Button>
        </Stack>
      </Center>
    </Box>
  );
}