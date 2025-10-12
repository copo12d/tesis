import { Center, Stack, Heading, Text, Box, Badge, Button, Spinner } from "@chakra-ui/react";
import { MdQrCodeScanner } from "react-icons/md";
import { useMobileContainer } from "../hooks/useMobileContainer";
import { ConfirmDialog } from "@/components/ConfirmDialog";
import { useState } from "react";
import { useReportContainer } from "../hooks/useReportContainer";
import { toast } from "react-hot-toast";

export default function MobileReportPage() {
  const { container, loading, error } = useMobileContainer();
  const { reportContainer, loading: reportingLoading } = useReportContainer();
  const [reporting, setReporting] = useState(false);

  const handleReport = async () => {
    if (!container?.serial) return;
    await reportContainer(container.serial);
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
              <Badge fontWeight={'bold'} bg={"transparent"} color={container.status === "AVAILABLE" ? "green.700" : "red.700"}>
                {container.status}
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
            <Button colorPalette="teal" px={"70px"} size="lg" disabled={!container} loading={reporting || reportingLoading}>
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

        <Text fontSize="sm" color="gray.500" textAlign="center">
          Escanea el QR de otro contenedor para continuar.
        </Text>
      </Stack>
    </Center>
  );
}