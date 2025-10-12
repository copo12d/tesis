import { Stack, Spinner, Text, Button } from "@chakra-ui/react";
import { useBatchList } from "../hooks/useBatchList";
import { GenericTable } from "@/components/GenericTable";
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";

const headers = [
  { key: "id", label: "ID" },
  { key: "description", label: "Descripción" },
  { key: "totalWeight", label: "Peso total (kg)" },
  { key: "status", label: "Estado" },
  { key: "creationDate", label: "Fecha de creación" },
  { key: "processedAt", label: "Procesado en" },
  { key: "createdByUsername", label: "Creado por" },
  { key: "processedByUsername", label: "Procesado por" },
];

export function BatchListPage() {
  const navigate = useNavigate();
  const { data, loading, error, params, setParams, refetch } = useBatchList();
  const [hasLoaded, setHasLoaded] = useState(false);

  useEffect(() => {
    if (!loading) setHasLoaded(true);
  }, [loading]);

  // Formatea los datos para la tabla
  const items = (data?.content ?? []).map((batch) => ({
    ...batch,
    totalWeight: `${batch.totalWeight?.toFixed(2) ?? "0.00"} kg`,
    creationDate: batch.creationDate
      ? new Date(batch.creationDate).toLocaleString()
      : "-",
    processedAt: batch.processedAt
      ? new Date(batch.processedAt).toLocaleString()
      : "-",
    processedByUsername: batch.processedByUsername || "-",
    createdByUsername: batch.createdByUsername || "-",
  }));

  // Acciones por fila (puedes personalizar)
  const renderActions = (batch) => (
    <Button
      size="xs"
      colorScheme="teal"
      variant="outline"
      onClick={() => navigate(`/batch/${batch.id}`)}
    >
      Ver
    </Button>
  );

  if (loading && !hasLoaded) {
    return (
      <Stack p={6} align="center">
        <Spinner />
        <Text fontSize="sm" color="gray.600">
          Cargando lotes...
        </Text>
      </Stack>
    );
  }

  return (
    <Stack bg={"whiteAlpha.900"} minH={"100vh"}>
      <GenericTable
        headers={headers}
        items={items}
        page={params.page + 1}
        totalPages={data.totalPages}
        totalElements={data.totalElements}
        cardTitle="Lista de lotes"
        caption={error ? error : undefined}
        loading={loading}
        onPageChange={(newPage) => {
          setParams((prev) => ({ ...prev, page: newPage.page - 1 }));
        }}
        renderActions={renderActions}
        // Si en el futuro agregas búsqueda, aquí puedes poner los props de búsqueda
      />
    </Stack>
  );
}