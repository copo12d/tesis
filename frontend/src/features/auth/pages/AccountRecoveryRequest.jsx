import {
  Center,
  Stack,
  Heading,
  Text,
  Button,
  Box,
} from "@chakra-ui/react";
import { useState } from "react";
import { Link } from "react-router-dom";
import { LiaUser } from "react-icons/lia";
import { IconInputField } from "@/components/ui/IconInputField";
import { useAccountRecoveryRequest } from "../hooks/useAccountRecoveryRequest";

export function AccountRecoveryRequest() {
  const [value, setValue] = useState("");
  const { requestAccountRecovery, loading } = useAccountRecoveryRequest();

  const handleSubmit = async (e) => {
    e.preventDefault();
    const raw = (value || "").trim();
    const isEmail = raw.includes("@");
    const payload = isEmail ? { email: raw } : { userName: raw }; // solo 1 campo
    await requestAccountRecovery(payload);
  };

  return (
    <Box as="main" w="100vw" minH="100svh" position="relative">
      <Box position="fixed" inset="0" bg="#e6f4ea" zIndex={0} />
      <Center minH="100svh" position="relative" zIndex={1}>
        <Stack boxShadow="md" bg="white" p={10} rounded="md" w={600} spacing={6}>
          <Heading color="#00695c" textAlign="center">
            Recuperar cuenta
          </Heading>

          <form onSubmit={handleSubmit}>
            <Stack spacing={6} mt={4}>
              <IconInputField
                label="Usuario o correo electrónico"
                name="userOrEmail"
                value={value}
                onChange={(e) => setValue(e.target.value)}
                placeholder="Introduce tu usuario o correo"
                icon={<LiaUser />}
                iconProps={{ bg: "#009688", px: 3 }}
                required
                inputProps={{
                  w: "100%",
                  pl: 2,
                  _placeholder: { pl: 2 },
                  autoFocus: true,
                }}
              />

              <Button
                type="submit"
                bg="#009688"
                color="white"
                size="lg"
                loading={loading}
                loadingText="Enviando..."
                spinnerPlacement="end"
                _hover={{ bg: "#00695c" }}
                mt={2}
              >
                Enviar solicitud
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
    </Box>
  );
}