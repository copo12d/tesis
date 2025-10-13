import { useParams, Link as RouterLink } from "react-router-dom";
import { Box, Stack, Heading, Text, Badge, Button, Flex } from "@chakra-ui/react";
import { useBatchDetails } from "../hooks/useBatchDetails";
import { useEffect, useState } from "react";
import { BatchAPI } from "../api/api.batch";
import { toast } from "react-hot-toast";
import { GenericTable } from "@/components/GenericTable";
import { MdInventory2 } from "react-icons/md";
import { useBatchHeader } from "../hooks/useBatchHeader";
import { useProcessBatch } from "../hooks/useProcessBatch";

export function BatchDetailPage() {
  const { id } = useParams();
  const {
    details,
    loading,
    filters,
    search,
    refetch,
    pagination,
    setPage,
    setPageSize,
  } = useBatchDetails(id);
  const [processing, setProcessing] = useState(false);

  // Usa el nuevo hook
  const { header, loadingHeader, refetchHeader } = useBatchHeader(id);
  const { processBatch } = useProcessBatch();

  const handleProcess = async () => {
    const ok = await processBatch(id);
    if (ok) {
      refetch();
      refetchHeader();
    }
  };

  // Formatea la fecha para mostrar solo la parte de fecha y hora
  const fecha = header?.creationDate
    ? new Date(header.creationDate).toLocaleDateString()
    : "";
  const hora = header?.creationDate
    ? new Date(header.creationDate).toLocaleTimeString()
    : "";

  return (
    <Box
      height="100vh"
      overflowY="auto"
      bg="#e6f4ea"
      p={{ base: 2, md: 6 }}
      sx={{
        "::-webkit-scrollbar": { width: "8px" },
        "::-webkit-scrollbar-thumb": { background: "#bdbdbd", borderRadius: "4px" },
      }}
    >
      <Button as={RouterLink} to={-1} variant="link" mb={2} color="teal.700" fontWeight="bold">
        ← Regresar a lotes
      </Button>

      <Heading size="lg" mb={4} color="#222">
        Detalles del Lote
      </Heading>

      {/* Header/Card con los nuevos campos */}
      <Box
        bg="#00695c"
        color="white"
        p={{ base: 6, md: 8 }}
        rounded="md"
        mb={8}
        boxShadow="lg"
        minH="140px"
        display="flex"
        alignItems="center"
        justifyContent="space-between"
      >
        <Stack spacing={1} flex={1}>
          <Flex align="center" mb={2}>
            <MdInventory2 size={32} style={{ marginRight: 10 }} />
            <Text fontWeight="bold" fontSize="2xl" letterSpacing={1}>
              Lote #{header?.id || id}
            </Text>
            <Badge
              ml={3}
              colorPalette={header?.status === "Lote Procesado" ? "green" : "teal"}
              fontSize="md"
              px={3}
              py={1}
              borderRadius="md"
              fontWeight="bold"
              bg={header?.status === "PROCESADO" ? "#43a047" : "#009688"}
              color="white"
            >
              {header?.status }
            </Badge>
          </Flex>
          <Flex gap={8} flexWrap="wrap">
            <Text fontSize="md"><b>Descripción:</b> {header?.description}</Text>
            <Text fontSize="md"><b>Peso total:</b> {header?.totalWeight} kg</Text>
            <Text fontSize="md"><b>Usuario:</b> {header?.createdByUsername}</Text>
            <Text fontSize="md"><b>Fecha:</b> {fecha} - {hora}</Text>
            <Text fontSize="md"><b>Estado:</b> {header?.status}</Text>
          </Flex>
        </Stack>
        <Button
          colorPalette="green"
          size="lg"
          loading={processing}
          onClick={handleProcess}
          disabled={header?.status === "PROCESADO"}
          fontWeight="bold"
          fontSize="lg"
          px={8}
          py={6}
          boxShadow="md"
          ml={8}
        >
          Procesar
        </Button>
      </Box>

      {/* Tabla de detalles con GenericTable */}
      <Box bg="white" rounded="md" boxShadow="sm">
        <GenericTable
          headers={[
            { key: "serial", label: "Serial" },
            { key: "weight", label: "Peso (kg)" },
            { key: "createdByUsername", label: "Usuario" },
            { key: "date", label: "Fecha" },
            { key: "hour", label: "Hora" },
          ]}
          items={details}
          loading={loading}
          searchTerm={filters.serial}
          onSearchTermChange={e => search({ serial: e.target.value })}
          caption="Contenedores asociados"
          bodyBg="white"
          headerBg="teal.700"
          headerColor="white"
          stripe
          page={pagination.page}
          pageSize={pagination.size}
          totalItems={pagination.totalElements}
          onPageChange={setPage}
          onPageSizeChange={setPageSize}
        />
      </Box>
    </Box>
  );
}