import { IconButton, Stack, Spinner, Text, Box, Button, } from "@chakra-ui/react";
import { LiaEditSolid, LiaTrashAltSolid } from "react-icons/lia";
import { AiOutlineEye } from "react-icons/ai";
import { useNavigate } from "react-router-dom";
import { useContainerList } from "../hooks/useContainerList";
import { useEffect, useState } from "react";
import { GenericTable } from "@/components/GenericTable";
import { ConfirmDialog } from "@/components/ConfirmDialog";
import { QrPreviewDialog } from "../components/QrpreviewDialog";
import { useDeleteContainer } from "../hooks/useDeleteContainer";
import { Link } from "react-router-dom";

const headers = [
  { key: "serial", label: "Serial" },
  { key: "latitude", label: "Latitud" },
  { key: "longitude", label: "Longitud" },
  { key: "capacity", label: "Capacidad (L)" },
  { key: "status", label: "Estado" },
  { key: "containerTypeName", label: "Tipo" },
  { key: "createdAt", label: "Creado" },
];

const searchMenuItems = [
  { value: "serial", label: "Serial" },
];

export function ContainerList() {
  const navigate = useNavigate();
  const {
    items,
    total,
    loading,
    page,
    setPage,
    totalPages,
    searchTerm,
    setSearchTerm,
    searchType,
    setSearchType,
    refetch,
  } = useContainerList({
    initialPage: 1,
    pageSize: 10,
    sortBy: "serial",
    sortDir: "asc",
    autoFetch: true,
    debounceMs: 400,
    initialSearchType: "serial",
  });

  const { remove, deletingId } = useDeleteContainer();

  const handleEdit = (row) => {
    navigate(`/container/edit/${row.id}`);
  };

  const handleDelete = async (row) => {
    const ok = await remove(row.id);
    if (ok) {
      refetch();
    }
  };

  const [hasLoaded, setHasLoaded] = useState(false);

  useEffect(() => {
    if (!loading) setHasLoaded(true);
  }, [loading]);

  if (loading && !hasLoaded) {
    return (
      <Stack p={6} align="center">
        <Spinner />
        <Text fontSize="sm" color="gray.600">
          Cargando contenedores...
        </Text>
      </Stack>
    );
  }

  return (
    <Stack bg={"white"} h={"100vh"}>
      <Box pt={5} pl={6}>
        <Link to="/container-type/list">
          <Button variant="link" color="teal.700" size="sm">
            Ir a tipos de contenedores
          </Button>
        </Link>
      </Box>
      <GenericTable
        headers={headers}
        items={items}
        page={page}
        totalPages={totalPages}
        totalElements={total}
        onAdd={() => navigate("/container/new")}
        sizes={["lg"]}
        caption={`Lista de contenedores (${total})`}
        cardTitle={"Lista de contenedores"}
        onPageChange={(newPage) => {
          setPage(newPage.page);
        }}
        // --- Props para barra de búsqueda ---
        menuItems={searchMenuItems}
        menuButtonText={
          searchMenuItems.find((i) => i.value === searchType)?.label ||
          searchMenuItems[0].label
        }
        searchTerm={searchTerm}
        onSearchTermChange={(e) => setSearchTerm(e.target.value)}
        searchType={searchType}
        onSearchTypeChange={setSearchType}
        // --- Fin props búsqueda ---
        renderActions={(row) => {
          const isDeleting = deletingId === row.id;
          return (
            <Stack direction="row" spacing={1}>
              <QrPreviewDialog
                containerId={row.id}
                serial={row.serial}
                trigger={
                  <IconButton
                    aria-label="Ver QR"
                    size="xs"
                    variant="subtle"
                    colorPalette="blue"
                    mx={0.5}
                  >
                    <AiOutlineEye />
                  </IconButton>
                }
              />
              <IconButton
                aria-label="Editar"
                size="xs"
                variant="subtle"
                colorPalette="green"
                onClick={() => handleEdit(row)}
                mx={0.5}
              >
                <LiaEditSolid />
              </IconButton>
              <ConfirmDialog
                title="Eliminar contenedor"
                description={`¿Seguro que deseas eliminar el contenedor "${row.serial}"? Esta acción no se puede deshacer.`}
                confirmText="Eliminar"
                cancelText="Cancelar"
                confirmColorPalette="red"
                loading={isDeleting}
                onConfirm={() => handleDelete(row)}
                trigger={
                  <IconButton
                    aria-label="Eliminar"
                    size="xs"
                    variant="subtle"
                    colorPalette="red"
                    disabled={isDeleting}
                  >
                    <LiaTrashAltSolid />
                  </IconButton>
                }
              />
            </Stack>
          );
        }}
      />
    </Stack>
  );
}