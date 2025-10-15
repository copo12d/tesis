import { Center, Stack, Heading, Text, Box, Badge, Button, Spinner } from "@chakra-ui/react";
import { MdQrCodeScanner } from "react-icons/md";
import { useMobileContainer } from "../hooks/useMobileContainer";
import { ConfirmDialog } from "@/components/ConfirmDialog";
import { useReportContainer } from "../hooks/useReportContainer";
import { useNavigate } from "react-router-dom";
import { useEffect } from "react";

export default function MobileReportPage() {
  const { container, loading, error } = useMobileContainer();
  const { reportContainer, loading: reportingLoading } = useReportContainer();
  const navigate = useNavigate();

  // Guarda el containerId en localStorage cuando esté disponible
  useEffect(() => {
    if (container?.id) {
      localStorage.setItem("containerId", container.id);
    }
  }, [container?.id]);

  const handleReport = async () => {
    if (!container?.serial) return;
    const ok = await reportContainer(container.serial);
    if (ok) {
      navigate("/mobile/thanks");
    }
  };

  return (
    <Center h="100vh" bg="#e6f4ea">
      <Stack
        boxShadow="md"
        bg="white"
        p={8}
        rounded="md"
        w={340}
        align="center"
        spacing={6}
      >
        <MdQrCodeScanner size={60} color="#009688" />
        <Heading color="#009688" textAlign="center" size="md">
          Información del Contenedor
        </Heading>

        {loading ? (
          <Spinner color="teal.500" size="lg" />
        ) : !container ? (
          <Text color="red.500" fontWeight="bold">
            {error || "No se encontró el contenedor."}
          </Text>
        ) : (
          <Box w="100%" bg="#f6faf8" p={4} rounded="md" boxShadow="sm">
            <Text fontWeight="bold" color="gray.700">
              Serial:{" "}
              <Text as="span" color="teal.700">
                {container.serial}
              </Text>
            </Text>
            <Text fontWeight="bold" color="gray.700" mt={2}>
              Estado:{" "}
              <Badge fontWeight={'bold'} bg={"transparent"} color={container.status === "Contenedor Vacio" ? "green.700" : "red.700"}>
                {container.status.toUpperCase()}
              </Badge>
            </Text>
            <Text fontWeight="bold" color="gray.700" mt={2}>
              Tipo:{" "}
              <Badge fontWeight={"bold"} bg={"transparent"} color="green.700">{container.containerTypeName}</Badge>
            </Text>
          </Box>
        )}

        <ConfirmDialog
          trigger={
            <Button colorPalette="teal" px={"70px"} size="lg" disabled={!container} loading={reportingLoading}>
              Notificar estado
            </Button>
          }
          title="Confirmar reporte"
          description="¿Está seguro que desea reportar este contenedor?"
          confirmText="Reportar"
          cancelText="Cancelar"
          onConfirm={handleReport}
          loading={reportingLoading}
        />

        <Text
          fontSize="sm"
          color="teal.700"
          textAlign="center"
          fontWeight="bold"
          cursor="pointer"
          _hover={{ textDecoration: "underline" }}
          onClick={() => navigate("/mobile/login")}
        >
          ¿Eres recolector? Inicia sesión
        </Text>
      </Stack>
    </Center>
  );
}