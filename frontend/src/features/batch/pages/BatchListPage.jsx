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
  const {
    items,
    total,
    loading,
    page,
    setPage,
    totalPages,
    error,
    refetch,
  } = useBatchList();

  const [hasLoaded, setHasLoaded] = useState(false);

  useEffect(() => {
    if (!loading) setHasLoaded(true);
  }, [loading]);

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
        page={page}
        totalPages={totalPages}
        totalElements={total}
        cardTitle="Lista de lotes"
        caption={error ? error : undefined}
        loading={loading}
        onPageChange={(newPage) => {
          setPage(newPage.page);
        }}
        renderActions={renderActions}
        onAdd={() => navigate("/batch/create")}
      />
    </Stack>
  );
}