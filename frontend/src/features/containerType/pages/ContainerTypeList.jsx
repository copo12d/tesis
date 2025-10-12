import { Stack, Spinner, Text, IconButton } from "@chakra-ui/react";
import { GenericTable } from "../../../components/GenericTable";
import { useNavigate } from "react-router-dom";
import { useContainerTypeList } from "../hooks/useContainerTypeList";
import { useEffect, useState } from "react";
import { LiaEditSolid, LiaTrashAltSolid } from "react-icons/lia";
import { ConfirmDialog } from "../../../components/ConfirmDialog";
import { useDeleteContainerType } from "../hooks/useDeleteContainerType";

const headers = [
  { key: "name", label: "Nombre" },
  { key: "description", label: "Descripción" },
];

const searchMenuItems = [
  { value: "name", label: "Nombre" },
];

export function ContainerTypeList() {
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
    refetch,
  } = useContainerTypeList({
    initialPage: 1,
    pageSize: 10,
    sortBy: "id",
    sortDir: "DESC",
    autoFetch: true,
    debounceMs: 400,
  });

  const { remove, deletingId } = useDeleteContainerType({ onSuccess: refetch });

  const [hasLoaded, setHasLoaded] = useState(false);

  useEffect(() => {
    if (!loading) setHasLoaded(true);
  }, [loading]);

  const handleEdit = (row) => {
    navigate(`/container-type/edit/${row.id}`);
  };

  const handleDelete = async (row) => {
    await remove(row.id);
  };

  if (loading && !hasLoaded) {
    return (
      <Stack p={6} align="center">
        <Spinner />
        <Text fontSize="sm" color="gray.600">
          Cargando tipos de contenedor...
        </Text>
      </Stack>
    );
  }

  return (
    <Stack bg={"whiteAlpha.900"} h={"100vh"}>
      <GenericTable
        headers={headers}
        items={items}
        page={page}
        totalPages={totalPages}
        totalElements={total}
        onAdd={() => navigate("/container-type/new")}
        sizes={["lg"]}
        caption={`Lista de tipos de contenedor (${total})`}
        cardTitle={"Lista de tipos de contenedor"}
        onPageChange={(newPage) => {
          setPage(newPage.page);
        }}
        // --- Props para barra de búsqueda ---
        menuItems={searchMenuItems}
        menuButtonText={searchMenuItems.find((i) => i.value === "name")?.label}
        searchTerm={searchTerm}
        onSearchTermChange={(e) => setSearchTerm(e.target.value)}
        // --- Fin props búsqueda ---
        renderActions={(row) => {
          const isDeleting = deletingId === row.id;
          return (
            <>
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
                title="Eliminar tipo de contenedor"
                description={`¿Seguro que deseas eliminar "${row.name}"? Esta acción no se puede deshacer.`}
                confirmText="Eliminar"
                cancelText="Cancelar"
                confirmColorPalette="teal"           // Botón confirmar: teal
                cancelColorPalette="gray"            // Botón cancelar: gris
                confirmVariant="solid"
                cancelVariant="outline"
                contentColorPalette="teal"           // Borde y detalles: teal
                contentBg="white"                    // Fondo del modal: blanco
                headerBg="teal.700"                  // Header: teal oscuro
                headerBorderColor="teal.600"         // Borde header: teal
                footerBg="white"                     // Footer: blanco
                backdropBg="blackAlpha.400"          // Fondo oscuro tras modal
                titleColor="whiteAlpha.900"                // Título: teal oscuro
                descriptionColor="gray.700"          // Descripción: gris oscuro
                iconColor="teal.400"                 // Icono: teal claro
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
            </>
          );
        }}
      />
    </Stack>
  );
}