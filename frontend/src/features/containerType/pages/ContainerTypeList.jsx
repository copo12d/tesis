import { Stack, Spinner, Text, IconButton, Button, Box } from "@chakra-ui/react";
import { GenericTable } from "../../../components/GenericTable";
import { useNavigate } from "react-router-dom";
import { useContainerTypeList } from "../hooks/useContainerTypeList";
import { useEffect, useState } from "react";
import { LiaEditSolid, LiaTrashAltSolid } from "react-icons/lia";
import { ConfirmDialog } from "../../../components/ConfirmDialog";
import { useDeleteContainerType } from "../hooks/useDeleteContainerType";
import { Link } from "react-router-dom";

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
    <Stack bg={"white"} h={"100vh"}>
      <Box pl={4} pt={4} mb={-10}>
        <Link to="/container/list">
          <Button variant="link" color="teal.700" size="sm">
            Ir al listado de contenedores
          </Button>
        </Link>
      </Box>
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
                confirmColorPalette="teal"
                cancelColorPalette="gray"
                confirmVariant="solid"
                cancelVariant="outline"
                contentColorPalette="teal"
                contentBg="white"
                headerBg="teal.700"
                headerBorderColor="teal.600"
                footerBg="white"
                backdropBg="blackAlpha.400"
                titleColor="whiteAlpha.900"
                descriptionColor="gray.700"
                iconColor="teal.400"
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