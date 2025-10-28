import { IconButton, Stack, Spinner, Text, Heading } from "@chakra-ui/react";
import { LiaEditSolid, LiaTrashAltSolid } from "react-icons/lia";
import { useDeleteUser } from "../hooks/useDeleteUser";
import { GenericTable } from "@/components/GenericTable";
import { ConfirmDialog } from "@/components/ConfirmDialog";
import { useNavigate } from "react-router-dom";
import { useUserAdvancedSearch } from "../hooks/useUserAdvancedSearch";
import { useEffect, useState } from "react";

const headers = [
  { key: "fullName", label: "Nombre" },
  { key: "userName", label: "Usuario" },
  { key: "email", label: "Email" },
  { key: "roleDescription", label: "Rol" },
];

const searchMenuItems = [
  { value: "username", label: "Usuario" },
  { value: "name", label: "Nombre" },
];

export function UsersList() {
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
  } = useUserAdvancedSearch({
    initialPage: 1,
    pageSize: 10,
    sortBy: "id",
    sortDir: "DESC",
    autoFetch: true, // activa el debounce
    debounceMs: 400,
  });

  const { remove, deletingId } = useDeleteUser();

  const handleEdit = (row) => {
    navigate(`/users/edit/${row.id}`);
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
          Cargando usuarios...
        </Text>
      </Stack>
    );
  }

  return (
    <Stack bg={"white"} h={"100vh"}>
      <GenericTable
        headers={headers}
        items={items}
        page={page}
        totalPages={totalPages}
        totalElements={total}
        onAdd={() => navigate("/users/new")}
        sizes={["lg"]}
        caption={`Lista de usuarios (${total})`}
        cardTitle={"Lista de usuarios"}
        onPageChange={(newPage) => {
          setPage(newPage.page);
        }}
        // --- Props para barra de búsqueda ---
        menuItems={searchMenuItems}
        menuButtonText={searchMenuItems.find((i) => i.value === searchType)?.label}
        searchTerm={searchTerm}
        onSearchTermChange={(e) => setSearchTerm(e.target.value)}
        searchType={searchType}
        onSearchTypeChange={setSearchType}
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
                title="Eliminar usuario"
                description={`¿Seguro que deseas eliminar a "${row.fullName}"? Esta acción no se puede deshacer.`}
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
            </>
          );
        }}
      />
    </Stack>
  );
}