import { Stack, Spinner, Text, Button } from "@chakra-ui/react";
import { useBatchList } from "../hooks/useBatchList";
import { GenericTable } from "@/components/GenericTable";
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { useDeleteBatch } from "../hooks/useDeleteBatch";
import { ConfirmDialog } from "@/components/ConfirmDialog";
import { IconButton } from "@chakra-ui/react";
import { LiaTrashAltSolid, LiaEyeSolid } from "react-icons/lia";

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
  const { remove, deletingId } = useDeleteBatch();

  const [hasLoaded, setHasLoaded] = useState(false);

  useEffect(() => {
    if (!loading) setHasLoaded(true);
  }, [loading]);

  const handleDelete = async (row) => {
    const ok = await remove(row.id);
    if (ok) {
      refetch();
    }
  };

  // Acciones por fila (puedes personalizar)
  const renderActions = (batch) => (
    <>
      <IconButton
        aria-label="Ver"
        size="xs"
        variant="subtle"
        colorPalette="blue"
        onClick={() => navigate(`/batch/${batch.id}`)}
      >
        <LiaEyeSolid />
      </IconButton>
      <ConfirmDialog
        title="Eliminar lote"
        description={`¿Seguro que deseas eliminar el lote "${batch.description}"? Esta acción no se puede deshacer.`}
        confirmText="Eliminar"
        cancelText="Cancelar"
        confirmColorPalette="red"
        loading={deletingId === batch.id}
        onConfirm={() => handleDelete(batch)}
        trigger={
          <IconButton
            aria-label="Eliminar"
            size="xs"
            variant="subtle"
            colorPalette="red"
            disabled={deletingId === batch.id}
            ml={2}
          >
            <LiaTrashAltSolid />
          </IconButton>
        }
      />
    </>
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