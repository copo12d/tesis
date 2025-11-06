import { Box, Button, Center, Heading, Stack, Text, Input, Field } from "@chakra-ui/react";
import { AuthAPI } from "../api/auth.api";
import { useSetPassword } from "../hooks/useSetPassword";

export function PasswordRecoverySetPassword() {
  const { password, setPassword, repeat, setRepeat, loading, handleSubmit } =
    useSetPassword(AuthAPI.passwordRecoverySetPassword, {
      successRedirect: "/login",
      successMessage: "Contraseña restaurada correctamente.",
    });

  return (
    <Box as="main" w="100vw" minH="100svh" position="relative">
      <Box position="fixed" inset="0" bg="#e6f4ea" zIndex={0} />
      <Center minH="100svh" position="relative" zIndex={1}>
        <Stack boxShadow="md" bg="white" p={10} rounded="md" w={{ base: "95vw", md: 520 }} spacing={6}>
          <Heading color="#00695c" textAlign="center">Recuperar contraseña</Heading>
          <Text textAlign="center" color="gray.700">Ingresa tu nueva contraseña para recuperar el acceso.</Text>

          <form onSubmit={handleSubmit}>
            <Stack spacing={5} mt={2}>
              <Field.Root>
                <Field.Label>Nueva contraseña</Field.Label>
                <Input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="********"
                  autoComplete="new-password"
                  color={"blackAlpha.900"}
                />
              </Field.Root>

              <Field.Root>
                <Field.Label>Repite la contraseña</Field.Label>
                <Input
                  type="password"
                  value={repeat}
                  onChange={(e) => setRepeat(e.target.value)}
                  placeholder="********"
                  autoComplete="new-password"
                  color={"blackAlpha.900"}
                />
              </Field.Root>

              <Button
                type="submit"
                bg="#009688"
                color="white"
                size="lg"
                loading={loading}
                loadingText="Guardando..."
                spinnerPlacement="end"
                _hover={{ bg: "#00695c" }}
                mt={2}
              >
                Guardar contraseña
              </Button>
            </Stack>
          </form>

          <Button variant="ghost" onClick={() => window.location.assign("/login")} color="#2b6cb0">Volver al inicio de sesión</Button>
        </Stack>
      </Center>
    </Box>
  );
}