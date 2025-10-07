import api from "../../../api/api";

export const UsersAPI = {
  create: (payload) => api.post("/users/admin/register", payload),
  // Cambio: endpoint correcto y nombres de params (all + sortBy / sortDir)
  list: ({ page = 0, size = 10, sortBy = "id", sortDir = "DESC" } = {}) =>
    api.get("/users/admin/all", {
      params: {
        page,
        size,
        sortBy,
        sortDir,
      },
    }),
  delete: (id) => api.delete(`/users/admin/${id}`),
  get: (id) => api.get(`/users/admin/${id}`),
  update: (id, payload) => api.put(`/users/admin/${id}`, payload),

  advanceSearch: ({
    searchTerm = "",
    searchType = "username", // "username" o "name"
    page = 0,
    size = 10,
    sortBy = "id",
    sortDir = "DESC",
  } = {}) =>
    api.get("/users/admin/search", {
      params: {
        searchTerm,
        searchType,
        page,
        size,
        sortBy,
        sortDir,
      },
    }),
  getRoles: () => api.get("/role/admin/all"),
  // ...existing methods...
};

export const ALL_ROLES = [
  { value: "ROLE_SUPERUSER", label: "Superusuario" },
  { value: "ROLE_ADMIN", label: "Administrador" },
  { value: "ROLE_USER", label: "Usuario" },
];

export function availableRolesFor(currentRole) {
  if (currentRole === "ROLE_SUPERUSER") return ALL_ROLES;
  if (currentRole === "ROLE_ADMIN")
    return ALL_ROLES.filter(
      (r) => r.value !== "ROLE_SUPERUSER" && r.label !== "Superusuario"
    );
  return [];
}
