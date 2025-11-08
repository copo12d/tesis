import api from "@/api/api";

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
  getIdByUsername: (username) =>
    api.get("/users/public/idByUsername", {
      params: { username },
    }),
  advanceSearch: ({
    searchTerm = "",
    searchType = "username",
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
  getProfile: (id) => api.get(`/users/public/${id}`),
  updateProfile: (id, payload) => api.put(`/users/public/${id}`, payload),
  // ...existing methods...
};

export async function availableRolesFor(currentRole) {
  try {
    const { data } = await api.get("/role/admin/all");

    if (!Array.isArray(data?.data)) return [];

    return data.data.map((r) => ({
      value: r.value,
      label: r.name,
    }));
  } catch (error) {
    console.error("Error al obtener roles:", error);
    return [];
  }
}
