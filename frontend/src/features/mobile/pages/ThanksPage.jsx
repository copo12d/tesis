import { Center, Stack, Heading, Text, Button } from "@chakra-ui/react";
import { MdCheckCircle } from "react-icons/md";
import { useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";

export default function ThanksPage() {
  const navigate = useNavigate();

  const rawId = localStorage.getItem("containerId");
  const containerId =
    rawId && rawId !== "null" && rawId !== "undefined" ? rawId : null;

  const goToReport = useCallback(() => {
    if (!containerId) return; // no navegar si no hay ID
    navigate(`/mobile/containers/${containerId}`, { replace: true });
  }, [navigate, containerId]);

  useEffect(() => {
    if (!containerId) return; // no auto-redirect sin ID
    const t = setTimeout(goToReport, 5000);
    return () => clearTimeout(t);
  }, [goToReport, containerId]);

  return (
    <Center minH="100vh" bg="#e6f4ea">
      <Stack
        align="center"
        spacing={6}
        bg="white"
        p={8}
        rounded="md"
        boxShadow="md"
        w="90%"
        maxW="420px"
      >
        <MdCheckCircle size={80} color="#38A169" />
        <Heading color="#38A169" size="lg" textAlign="center">
          ¡Muchas gracias!
        </Heading>
        <Text fontSize="xl" color="gray.700" textAlign="center">
          Tu reporte fue enviado exitosamente.
        </Text>
        <Button
          w="100%"
          bg="#009688"
          color="white"
          size="lg"
          _hover={{ bg: "#00695c" }}
          onClick={goToReport}
          disabled={!containerId}
        >
          Volver a reportar
        </Button>
        <Text fontSize="sm" color="gray.600" textAlign="center">
          {containerId
            ? "Serás redirigido automáticamente en 5 segundos."
            : "No hay ID de contenedor; no se puede redirigir automáticamente."}
        </Text>
      </Stack>
    </Center>
  );
}