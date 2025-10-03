import { IconButton, Stack, Spinner, Text, Heading } from "@chakra-ui/react";
import { LiaEditSolid, LiaTrashAltSolid } from "react-icons/lia";
import { useUsersList } from "../hooks/useUsersList";
import { useDeleteUser } from "../hooks/useDeleteUser";
import { GenericTable } from "@/components/GenericTable";
import { ConfirmDialog } from "@/components/ConfirmDialog";
import { useNavigate } from "react-router-dom";

const headers = [
  { key: "fullName", label: "Nombre" },
  { key: "userName", label: "Usuario" },
  { key: "email", label: "Email" },
  { key: "role", label: "Rol" },
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
    refetch,
  } = useUsersList({
    initialPage: 1,
    pageSize: 10,
    sortBy: "id",
    sortDir: "DESC",
  });

  const { remove, deletingId } = useDeleteUser();

  const handleEdit = (row) => {
    navigate(`/users/edit/${row.id}`);
  };

  const handleDelete = async (row) => {
    const ok = await remove(row.id);
    if (ok) refetch();
  };
  

  if (loading && items.length === 0) {
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
    <Stack bg={"whiteAlpha.900"} h={"100vh"} >
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