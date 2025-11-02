import { Center, Stack, Heading, Text, Box, Badge, Button, Spinner } from "@chakra-ui/react";
import { MdQrCodeScanner } from "react-icons/md";
import { useMobileContainer } from "../hooks/useMobileContainer";
import { ConfirmDialog } from "@/components/ConfirmDialog";
import { useReportContainer } from "../hooks/useReportContainer";
import { useNavigate, useParams } from "react-router-dom";
import AuthContext from "@/context/AuthContext";
import { useContext, useEffect } from "react";

export default function MobileReportPage() {
  const { container, loading, error } = useMobileContainer();
  const { reportContainer, loading: reportingLoading } = useReportContainer();
  const navigate = useNavigate();
  const { id } = useParams();
  const { user, sessionExpired } = useContext(AuthContext) || {};
  const isAuthenticated = !!user && sessionExpired === false;

  // Guarda el containerId en localStorage cuando esté disponible
  useEffect(() => {
    if (container?.id) {
      localStorage.setItem("containerId", container.id);
    }
  }, [container?.id]);

  // Fija el contenedor actual en localStorage al abrir el reporte
  useEffect(() => {
    if (id) {
      localStorage.setItem("mobile.containerId", String(id));
    }
  }, [id]);

  const handleReport = async () => {
    if (!container?.serial) return;
    const ok = await reportContainer(container.serial);
    if (ok) {
      navigate("/mobile/thanks");
    }
  };

  const handleCollect = () => {
    // No redirige automáticamente; solo al hacer clic
    navigate("/mobile/containers/collect/");
  };

  return (
    // Fondo a pantalla completa (cubre todo, sin “rectángulo negro”)
    <Box as="main" w="100vw" minH="100svh" position="relative">
      <Box position="fixed" inset="0" bg="#e6f4ea" zIndex={0} />

      <Center minH="100svh" position="relative" zIndex={1} px={4}>
        <Stack
          boxShadow="md"
          bg="white"
          p={8}
          rounded="md"
          w="100%"              // antes: w={340}
          maxW="360px"          // límite para móvil
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

          {isAuthenticated && (
            <Button colorPalette="teal" w="100%" mt={0} onClick={handleCollect}>
              Recolectar
            </Button>
          )}

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
    </Box>
  );
}